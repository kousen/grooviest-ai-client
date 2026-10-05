package demos

import com.kousenit.ai.AI

import java.time.LocalDate

// Step 3: the type you coerce to decides the shape of the question.
// `as LocalDate[]` sends a JSON schema for an array of dates and builds real LocalDates.
// `as Person` derives the schema from the record's components. This is where it stops being a toy.

record Person(String name, String role, LocalDate birthday) {}

enum Sentiment { POSITIVE, NEGATIVE, NEUTRAL }

def ai = new AI()

def bio = '''Ken Kousen, President of Kousen IT, Inc., was born on March 12, 1962.
He speaks at Community Over Code in Glasgow on 12 October 2026, and again at
the Grails tutorial that afternoon at 14:20. His book deadline is 1 December 2026.'''

LocalDate[] dates = ai.extractDates(bio) as LocalDate[]
println "Dates: ${dates*.dayOfWeek}"
println "Earliest: ${dates.min()}"
println()

Person ken = ai.extractPerson(bio) as Person
println "Record: $ken"
println "Age at the talk: ${ken.birthday().until(LocalDate.of(2026, 10, 12)).years}"
println()

Sentiment mood = ai.classifySentiment('Conference wifi failed during my demo again.') as Sentiment
println "Mood: $mood"

int count = ai.countTheNumberOfDistinctCities(bio) as int
println "Cities: $count"
println()

// Teaching moment: generics are erased, so `as List<LocalDate>` is just `as List`.
// The schema can only ask for an array of strings. Arrays keep their component type.
println "as List<LocalDate> gives: ${(ai.extractDates(bio) as List<LocalDate>)*.getClass()*.simpleName}"
