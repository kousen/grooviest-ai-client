package com.kousenit.ai

import groovy.json.JsonOutput
import spock.lang.Specification

import java.time.LocalDate

record Person(String name, int age, LocalDate birthday) {}

enum Sentiment { POSITIVE, NEGATIVE, NEUTRAL }

class AISpec extends Specification {

    def "method names become instructions"() {
        expect:
        AI.instruction(name) == expected

        where:
        name                        | expected
        'summarize'                 | 'summarize'
        'translateToFrench'         | 'translate to french'
        'explainLikeImFive'         | 'explain like im five'
        'translate to Scots Gaelic' | 'translate to Scots Gaelic'
    }

    def "a missing method renders its name as the instruction and its args as material"() {
        given:
        def ai = new AI({ String prompt, Map schema = null -> 'Bonjour' })

        when:
        def prompt = ai.translateToFrench('Hello')

        then:
        prompt.render() == '''Translate to french.
Reply with only the result, no preamble.

Hello'''
        "$prompt" == 'Bonjour'
    }

    def "named arguments become requirements"() {
        given:
        def ai = new AI({ String prompt, Map schema = null -> 'short' })

        expect:
        ai.summarize('long text', maxWords: 25, tone: 'pirate').render().contains(
            'Requirements:\n- max words: 25\n- tone: pirate')
    }

    def "the answer is fetched once and remembered"() {
        given:
        int calls = 0
        def ai = new AI({ String prompt, Map schema = null -> "answer ${++calls}" })

        when:
        def prompt = ai.summarize('text')
        3.times { prompt.toString() }

        then:
        prompt.toString() == 'answer 1'
        calls == 1
    }

    def "quoted method names and properties work too"() {
        given:
        def ai = new AI({ String prompt, Map schema = null -> prompt.readLines()[0] })

        expect:
        ai."translate to Scots Gaelic"('hi').toString() == 'Translate to Scots Gaelic.'
        ai.haiku.toString() == 'Haiku.'
    }

    def "coercing to a type asks for JSON matching that type"() {
        given:
        Map sentSchema = null
        def ai = new AI({ String prompt, Map schema = null ->
            sentSchema = schema
            JsonOutput.toJson(result: [name: 'Ken', age: 60, birthday: '1962-03-12'])
        })

        when:
        def person = ai.extractPerson('Ken Kousen, 60, born March 12 1962') as Person

        then:
        person == new Person('Ken', 60, LocalDate.of(1962, 3, 12))
        sentSchema.get('properties').result == [
            type                : 'object',
            properties          : [name: [type: 'string'], age: [type: 'integer'],
                                   birthday: [type: 'string', format: 'date']],
            required            : ['name', 'age', 'birthday'],
            additionalProperties: false
        ]
    }

    def "arrays keep their element type, enums come back as constants"() {
        given:
        def ai = new AI({ String prompt, Map schema = null ->
            schema.get('properties').result.type == 'array' ?
                JsonOutput.toJson(result: ['2026-10-12', '2026-10-14']) :
                JsonOutput.toJson(result: 'POSITIVE')
        })

        expect:
        (ai.extractDates('text') as LocalDate[]) == [LocalDate.of(2026, 10, 12), LocalDate.of(2026, 10, 14)] as LocalDate[]
        (ai.classifySentiment('text') as Sentiment) == Sentiment.POSITIVE
    }
}
