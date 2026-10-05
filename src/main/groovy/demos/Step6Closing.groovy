package demos

import com.kousenit.ai.AI

// Step 6: the closing beat. propertyMissing makes a getter a question with no material.
// And respondsTo says "no" to everything, yet the object answers anyway.
// An object that will accept any message is either a bug or a language model.

def ai = new AI()

println ai.haiku
println()
println ai."a toast to the Groovy community, in one sentence"
println()
println "ai.respondsTo('anything') -> ${ai.respondsTo('anything') as boolean}"
println ai.anything
