package demos

import com.kousenit.ai.AI

// Step 5: learning. The first call falls through to methodMissing.
// Then we pin a real, memoized method onto the metaclass. respondsTo flips from false
// to true, and repeated calls with the same material never leave the JVM.

def ai = new AI()
def text = 'Groovy closures capture their lexical scope and can be curried, composed, and memoized.'

println "Responds to summarize? ${ai.respondsTo('summarize') as boolean}"
println ai.summarize(text)
println()

// Teach it: pin the method, memoized by argument.
ai.metaClass.summarize = { String s -> ai.methodMissing('summarize', [s] as Object[]).toString() }.memoize()

println "Responds to summarize now? ${ai.respondsTo('summarize') as boolean}"
def timed = { Closure c -> long t = System.nanoTime(); def r = c(); println "(${(System.nanoTime() - t) / 1_000_000} ms)"; r }
println timed { ai.summarize(text) }
println timed { ai.summarize(text) }
