package com.kousenit.ai

import groovy.json.JsonOutput
import spock.lang.Specification
import spock.lang.TempDir

class OpenRouterClientSpec extends Specification {

    @TempDir File dir

    def "the request body has the chat completions shape"() {
        given:
        def client = new OpenRouterClient(model: 'anthropic/claude-sonnet-5.5', effort: 'low', maxTokens: 100)

        expect:
        client.request('Hi') == [
            model     : 'anthropic/claude-sonnet-5.5',
            max_tokens: 100,
            messages  : [[role: 'user', content: 'Hi']],
            provider  : [require_parameters: true],
            reasoning : [effort: 'low']
        ]
        client.request('Hi', [type: 'object']).response_format ==
            [type: 'json_schema', json_schema: [name: 'result', strict: true, schema: [type: 'object']]]
        !new OpenRouterClient(effort: null).request('Hi').containsKey('reasoning')
    }

    def "a recorded request is answered from disk without a network or an API key"() {
        given:
        def client = new OpenRouterClient(recordings: dir, live: false)
        def reply = [choices: [[message: [role: 'assistant', content: 'Bonjour']]]]
        client.recordingFor(JsonOutput.toJson(client.request('Say hello in French'))).text = JsonOutput.toJson(reply)

        expect:
        client.complete('Say hello in French') == 'Bonjour'
    }

    def "an error body is reported instead of silently returning nothing"() {
        given:
        def client = new OpenRouterClient(recordings: dir, live: false)
        client.recordingFor(JsonOutput.toJson(client.request('x'))).text =
            JsonOutput.toJson(error: [code: 400, message: 'nope'])

        when:
        client.complete('x')

        then:
        thrown(IllegalStateException)
    }
}
