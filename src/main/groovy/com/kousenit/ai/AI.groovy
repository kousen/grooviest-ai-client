package com.kousenit.ai

/**
 * An object that answers any method call by asking Claude.
 *
 * The method name is the instruction, positional arguments are the material to work on,
 * and named arguments are requirements. Nothing is sent until the answer is used:
 * println it for text, or coerce it with {@code as SomeType} for structured data.
 *
 * <pre>
 *   def ai = new AI()
 *   println ai.summarize(text)
 *   println ai.translateToFrench(doc, tone: 'formal')
 *   def dates = ai.extractDates(text) as LocalDate[]
 * </pre>
 */
class AI {
    /** (String prompt, Map jsonSchema = null) -> String reply. Swap it for tests or another model. */
    final Closure<String> backend

    AI(Closure<String> backend = new OpenRouterClient().&complete) {
        this.backend = backend
    }

    def methodMissing(String name, Object args) {
        List material = args.toList()
        Map options = material && material[0] instanceof Map ? material.remove(0) : [:]
        new Prompt(backend, instruction(name), material, options)
    }

    /** ai.haiku, ai.summary ... a property read is a call with no arguments. */
    def propertyMissing(String name) {
        methodMissing(name, new Object[0])
    }

    /** translateToFrench -> "translate to french". A name that already has spaces is used as-is. */
    static String instruction(String name) {
        name.contains(' ') ? name : name.split(/(?=\p{Upper})/)*.toLowerCase().join(' ')
    }
}
