package demos

import com.kousenit.ai.AI

// Step 4: method names can be strings. Spaces, capitals, even interpolation.
// Almost nobody outside the Groovy community knows this works, and it is a better
// prompt surface than camelCase.

def ai = new AI()

def line = 'The best way to predict the future is to invent it.'

println ai."translate to Scots Gaelic"(line)
println()

def language = 'German'
println ai."translate to $language"(line)
println()

println ai."rewrite as a limerick about Glasgow"(line)
