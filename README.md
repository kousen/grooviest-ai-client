# The Grooviest AI Client

Demo code for **"The Grooviest AI Client: When methodMissing Meets Large Language Models"**,
Community Over Code, Glasgow, 12 October 2026. See [TALK_PLAN.md](TALK_PLAN.md) for the arc.

```groovy
def ai = new AI()
println ai.summarize(text)
println ai.translateToFrench(doc, tone: 'formal')
LocalDate[] dates = ai.extractDates(text) as LocalDate[]
Person ken = ai.extractPerson(bio) as Person
println ai."translate to Scots Gaelic"(line)
```

None of those methods exist. `methodMissing` turns the name into the instruction, positional
arguments into the material, and named arguments into requirements. Nothing is sent until the
answer is used: `toString()` asks for text, `as SomeType` asks for JSON matching a schema derived
from the type (records, arrays, enums, scalars, `LocalDate`).

## Layout

| File | Role |
|---|---|
| `src/main/groovy/com/kousenit/ai/AI.groovy` | `methodMissing`, `propertyMissing`, camelCase to instruction |
| `.../Prompt.groovy` | the pending question: `toString()` and `asType(Class)` |
| `.../Schemas.groovy` | JSON Schema from a type, and parsed JSON back into the type |
| `.../OpenRouterClient.groovy` | JDK `HttpClient` call to OpenRouter's chat completions API, with recording and replay |
| `src/main/groovy/demos/Step1..6*.groovy` | the talk's progression, one script per beat |
| `recordings/` | recorded API responses, keyed by request hash. Commit these. |
| `slides.md` | the Slidev deck |

## Running

Requires JDK 25 (Gradle toolchain) and `OPENROUTER_API_KEY` for anything not yet recorded.
The build uses Groovy 6.0.0; to run scripts with the `groovy` command, install the matching version
(`sdk install groovy 6.0.0`), since classes compiled by Groovy 6 do not load on a Groovy 5 runtime.
The default model is `anthropic/claude-sonnet-5.5`; set `AI_MODEL` to any OpenRouter model id that
supports structured outputs (for example `openai/gpt-6-luna`).

```bash
./gradlew test                 # Spock, no network
./gradlew step1Trick           # one task per demo script: step1Trick ... step6Closing
AI_LIVE=true ./gradlew step1Trick   # ignore recordings, always call the API
```

The first run of each demo records every response under `recordings/`. Later runs replay from
disk, so the demos work with no network. Change the prompt, model, or effort and the hash changes,
so the recording is refreshed on the next live run.

The scripts also run straight from the `groovy` command once the classes are built:

```bash
./gradlew classes && groovy -cp build/classes/groovy/main src/main/groovy/demos/Step4Strings.groovy
```

## Slides

```bash
npm install
npm run dev        # live preview with hot reload
npm run export     # PDF via Playwright
```
