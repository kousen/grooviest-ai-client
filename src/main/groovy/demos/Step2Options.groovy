package demos

import com.kousenit.ai.AI
import com.kousenit.ai.OpenRouterClient

// Step 2: positional arguments are the material, named arguments are requirements.
// Groovy gathers named args into a Map and passes it first; methodMissing peels it off.

def ai = new AI()

def text = new File(OpenRouterClient.projectRoot(), 'TALK_PLAN.md').text

println ai.summarize(text, maxWords: 30)
println()

println ai.summarize(text, maxWords: 30, tone: 'pirate')
println()

println ai.rewrite('The meeting has been rescheduled.', audience: 'a five-year-old', language: 'Spanish')
