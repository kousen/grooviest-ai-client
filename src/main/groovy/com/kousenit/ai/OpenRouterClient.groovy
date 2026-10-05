package com.kousenit.ai

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.security.MessageDigest

/**
 * Thin client for OpenRouter's chat completions API: plain JDK HttpClient plus groovy-json.
 * Any model OpenRouter serves works; set AI_MODEL to switch without touching code.
 *
 * Every response is recorded under {@link #recordings}, keyed by a hash of the request body.
 * A request that matches a recording is answered from disk, so the demos survive conference
 * wifi. Set AI_LIVE=true to ignore recordings and always hit the network.
 */
class OpenRouterClient {
    String model = System.getenv('AI_MODEL') ?: 'anthropic/claude-sonnet-5.5'
    String effort = 'low'          // reasoning effort; keeps live-demo latency down. null to omit.
    int maxTokens = 4096
    File recordings = new File('recordings')
    boolean live = Boolean.parseBoolean(System.getenv('AI_LIVE'))

    private final HttpClient http = HttpClient.newHttpClient()

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
        String json = JsonOutput.toJson(request(prompt, schema))
        File recording = recordingFor(json)
        String response = (!live && recording.exists()) ? recording.text : send(json)
        recordings.mkdirs()
        recording.text = response

        def reply = new JsonSlurper().parseText(response)
        if (reply.error) throw new IllegalStateException("OpenRouter error: ${reply.error.message}")
        reply.choices[0].message.content
    }

    File recordingFor(String requestJson) {
        new File(recordings, "${sha256(requestJson)}.json")
    }

    private String send(String json) {
        String apiKey = System.getenv('OPENROUTER_API_KEY')
        if (!apiKey) {
            throw new IllegalStateException('Set OPENROUTER_API_KEY, or run a demo that has a recording')
        }
        def request = HttpRequest.newBuilder('https://openrouter.ai/api/v1/chat/completions'.toURI())
            .header('content-type', 'application/json')
            .header('authorization', "Bearer $apiKey")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build()
        def response = http.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() != 200) {
            throw new IllegalStateException("HTTP ${response.statusCode()}: ${response.body()}")
        }
        response.body()
    }

    private static String sha256(String s) {
        MessageDigest.getInstance('SHA-256').digest(s.bytes).encodeHex().toString()
    }
}
