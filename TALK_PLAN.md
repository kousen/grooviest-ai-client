# The Grooviest AI Client: When methodMissing Meets Large Language Models

**Conference:** Community Over Code (Apache), Glasgow
**Date/Time:** 12 October 2026, 12:00–12:40 GMT
**Track:** Groovy — Room: Tweed
**Speaker:** Ken Kousen

> Note: the Grails 7 tutorial ("The Quest for the Holy Grails") runs in the same
> room at 14:20 the same day. Consider a brief callback to this talk from that one
> (e.g. the AI client appearing as a Grails service).

## Abstract (as submitted)

What if any method name became an AI prompt? Using Groovy's `methodMissing`,
`ExpandoMetaClass`, and dynamic dispatch, we'll build an LLM client where
`ai.summarize(text)` and `ai.translateToFrench(doc)` just work — no method
definitions required. Pure Groovy magic meets AI.

## Background

This was an "aspirational" talk — proposed before the content existed. The core
trick (`methodMissing` → prompt) is only a two-minute demo, so the 40 minutes
must be built on **escalation**: each step makes the client less of a toy.

## Proposed progression (demo-driven)

1. **The two-minute trick.** `ai.summarize(text)` — intercept the call in
   `methodMissing`, split the camelCase name into words, send it as the prompt
   with the argument as context. Big laugh, then move on.

2. **Arguments and options.** Positional args become context; named args become
   options (`ai.summarize(text, maxWords: 50)`), possibly mapped onto model
   parameters or appended to the prompt.

3. **Return types.** `ai.extractDates(text) as List<LocalDate>` — use `asType`
   (or a structured-output / JSON-schema request driven by the coerced type) so
   the client returns real objects, not strings. This is where it stops being a
   toy.

4. **String method names.** Groovy allows `ai."translate to French"(doc)` and
   GString method names. Almost nobody outside the Groovy community knows this
   works, and it is a better prompt surface than camelCase.

5. **Learning with ExpandoMetaClass.** First call to `translateToFrench` goes
   through the LLM; on success, pin a real method onto the metaclass (or memoize
   by args) so later calls skip the "figure out what this means" step.

6. **Closing beat.** `propertyMissing` (`ai.summary` as a getter) and
   `respondsTo` returning true for everything — a nice philosophical note to
   end on: an object that claims to respond to any message.

## Practical notes

- **Conference wifi will fail.** Record responses and build a replay/cache mode
  toggle from day one so every demo has an offline fallback.
- **Python bridge.** One slide showing `__getattr__` doing the same thing, for
  the non-Groovy attendees (and because Ken is learning Python).
- Keep the client Groovy-native where possible (JDK `HttpClient`, `JsonSlurper`),
  so the magic is clearly Groovy and not a framework.
- Decide early which model/API to target and keep the HTTP layer swappable.

## Open questions

- Which LLM provider/API for the live demo?
- How much of the structured-output step to show live vs. pre-built?
- Slides vs. live coding ratio — likely mostly live with a few framing slides.
- Exact form of the callback in the Grails talk.
