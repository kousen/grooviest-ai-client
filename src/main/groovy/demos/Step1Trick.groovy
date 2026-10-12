package demos

import com.kousenit.ai.AI

// Step 1: the two-minute trick. There is no summarize() method. There is no translateToFrench().
// methodMissing turns the name into the instruction and the argument into the material.

def ai = new AI()

def text = '''Apache Groovy is a dynamic language for the JVM with optional static typing.
It compiles to Java bytecode, interoperates with every Java library, and adds closures,
operator overloading, builders, and a metaobject protocol that lets a class decide at
runtime how to answer a method it never declared.'''

println ai.summarize(text)
println()

println ai.translateToSpanish('Groovy makes the JVM fun again.')
println()

println ai.explainLikeImFive('What is metaprogramming?')
