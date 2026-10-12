package com.kousenit.ai

import groovy.concurrent.Awaitable
import groovy.json.JsonSlurper

/**
 * A question that has not been asked yet.
 *
 * toString() asks for text and remembers the answer. {@code as SomeType} asks for JSON
 * matching that type and builds the object. The type you coerce to decides the shape
 * of the question.
 */
class Prompt {
    final Closure<String> backend
    final String instruction
    final List material
    final Map options
    private String answer

    Prompt(Closure<String> backend, String instruction, List material, Map options) {
        this.backend = backend
        this.instruction = instruction
        this.material = material
        this.options = options
    }

    @Override
    String toString() {
        answer ?: (answer = backend(render()))
    }

    def asType(Class type) {
        if (type == String) return toString()
        String json = backend(render(), Schemas.wrap(Schemas.of(type)))
        Schemas.coerce(new JsonSlurper().parseText(json).result, type)
    }

    /** Ask now, on a virtual thread, and collect the text later with {@code await}.
     *  Several prompts can be in flight at once: {@code await(a.async(), b.async())}. */
    Awaitable<String> async() {
        async { toString() }
    }

    /** The prompt text that will be sent. */
    String render() {
        List lines = ["${instruction.capitalize()}."]
        if (options) {
            lines << 'Requirements:'
            lines.addAll(options.collect { k, v -> "- ${AI.instruction(k as String)}: $v" })
        }
        lines << 'Reply with only the result, no preamble.'
        if (material) {
            lines << ''
            lines << material.join('\n\n')
        }
        lines.join('\n')
    }
}
