# grooviest-ai-client

Demo code and (later) slides for Ken's Community Over Code Glasgow talk, 12 Oct 2026, 40 minutes.
The talk arc lives in `TALK_PLAN.md`. Read it before changing the demos.

## Decisions

- Groovy 6.0.0 (released 19 Sep 2026), JDK 25, Gradle 9.8, Spock `2.4-groovy-5.0`. Spock has no Groovy 6
  build yet, so `build.gradle` sets `spock.iKnowWhatImDoing.disableGroovyVersionCheck` for both the
  Groovy compiler fork and the test JVM. Spock warns that this is unsupported; only the specs depend on it.
  Drop the flag when a `groovy-6.0` Spock variant appears on Maven Central.
- OpenRouter chat completions API via Groovy 6's `@HttpBuilderClient` (incubating `groovy-http-builder`
  module) + `groovy-json`. No vendor SDK, so the magic is clearly Groovy and the HTTP layer fits on
  one slide. `create(Closure)` starts from a blank builder, so it restates `baseUri`; the base URI
  needs its trailing slash or a leading-slash path replaces `/api/v1`. OpenRouter rather than a
  vendor key because Ken keeps `OPENROUTER_API_KEY` in his shell and it reaches any model. Default
  `anthropic/claude-sonnet-5.5`, override with `AI_MODEL`; reasoning effort `low` for demo latency.
- `AI` takes a `Closure<String> backend` `(prompt, schema = null) -> reply`. Tests pass a closure;
  production uses `new OpenRouterClient().&complete`. No interface.
- `methodMissing` returns a lazy `Prompt`. `toString()` = text, `asType(Class)` = structured output.
  This is deliberate: the coercion target decides the JSON schema.
- Every API response is recorded in `recordings/` keyed by request-body hash and replayed when
  present. `AI_LIVE=true` bypasses replay. Recordings are committed so the talk works offline.

## Conventions

- One demo script per talk beat in `src/main/groovy/demos/StepN*.groovy`; Gradle registers a task
  per script (`./gradlew step3Types`). Keep each script short enough to read on a slide.
- Spock specs never touch the network. Use a closure backend or seed a recording in a `@TempDir`.
- Prefer `LocalDate[]` over `List<LocalDate>` in demos; generics erase, arrays keep component type.
- A Map key named `properties` is unreadable via `map.properties` or `map['properties']` in
  Groovy 5.0.x and 6.0.0; use `map.get('properties')`. Observed in tests, not yet checked against Groovy 4.
