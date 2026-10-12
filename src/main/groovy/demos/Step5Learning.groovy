package demos

import com.kousenit.ai.AI

// Step 5: learning. The first call falls through to methodMissing.
// Then we pin a real, memoized method onto the metaclass. respondsTo flips from false
// to true, and repeated calls with the same material never leave the JVM.

val ai = new AI()        // the binding is final; the object still learns
def text = 'Groovy closures capture their lexical scope and can be curried, composed, and memoized.'

println "Responds to summarize? ${ai.respondsTo('summarize') as boolean}"
println ai.summarize(text)
println()

// Teach it: pin the method, memoized by argument.
ai.metaClass.summarize = { String s ->
    ai.methodMissing('summarize', [s] as Object[]).toString()
}.memoize()

println "Responds to summarize now? ${ai.respondsTo('summarize') as boolean}"

// Groovy 6: System.timed returns a Timed<T> record (result + duration), so no stopwatch closure.
2.times {
    val run = System.timed { ai.summarize(text) }
    println "$run.result ($run.millis ms)"
}
