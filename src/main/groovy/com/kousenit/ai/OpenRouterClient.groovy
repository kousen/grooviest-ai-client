package com.kousenit.ai

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import java.security.MessageDigest

/**
 * Thin client for OpenRouter's chat completions API: a Groovy 6 {@code @HttpBuilderClient}
 * interface plus groovy-json. Any model OpenRouter serves works; set AI_MODEL to switch
 * without touching code.
 *
 * Every response is recorded under {@link #recordings}, keyed by a hash of the request body.
 * A request that matches a recording is answered from disk, so the demos survive conference
 * wifi. Set AI_LIVE=true to ignore recordings and always hit the network.
 */
class OpenRouterClient {
    String model = System.getenv('AI_MODEL') ?: 'anthropic/claude-sonnet-5.5'
    String effort = 'low'          // reasoning effort; keeps live-demo latency down. null to omit.
    int maxTokens = 4096
    File recordings = new File(projectRoot(), 'recordings')
    boolean live = Boolean.parseBoolean(System.getenv('AI_LIVE'))

    @Lazy private OpenRouterApi api = {
        String apiKey = System.getenv('OPENROUTER_API_KEY')
        if (!apiKey) throw new IllegalStateException('Set OPENROUTER_API_KEY, or run a demo that has a recording')
        OpenRouterApi.create {
            baseUri OpenRouterApi.BASE_URI
            header 'Authorization', "Bearer $apiKey"
        }
    }()

    /** The request body. Public so tests can check the shape without touching the network. */
    Map request(String prompt, Map schema = null) {
        Map body = [
            model     : model,
            max_tokens: maxTokens,
            messages  : [[role: 'user', content: prompt]],
            provider  : [require_parameters: true]    // only route to endpoints that honour every parameter
        ]
        if (effort) body.reasoning = [effort: effort]
        if (schema) body.response_format = [type: 'json_schema', json_schema: [name: 'result', strict: true, schema: schema]]
        body
    }

    /** Text of the reply. When a schema is given, that text is JSON matching the schema. */
    String complete(String prompt, Map schema = null) {
        Map body = request(prompt, schema)
        File recording = recordingFor(JsonOutput.toJson(body))
        String response = (!live && recording.exists()) ? recording.text : api.chat(body)
        recordings.mkdirs()
        recording.text = response

        def reply = new JsonSlurper().parseText(response)
        if (reply.error) throw new IllegalStateException("OpenRouter error: ${reply.error.message}")
        reply.choices[0].message.content
    }

    /** Walk up from the working directory to the Gradle project root, so IDE run configs and
     *  the groovy command find the same recordings whatever their working directory. */
    static File projectRoot() {
        File dir = new File(System.getProperty('user.dir')).absoluteFile
        while (dir != null && !new File(dir, 'settings.gradle').exists()) dir = dir.parentFile
        dir ?: new File('.').absoluteFile
    }

    File recordingFor(String requestJson) {
        new File(recordings, "${sha256(requestJson)}.json")
    }

    private static String sha256(String s) {
        MessageDigest.getInstance('SHA-256').digest(s.bytes).encodeHex().toString()
    }
}
