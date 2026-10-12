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
He is speaking at the Community Over Code conference in Glasgow, Scotland on 12 October 2026.
He is giving a Groovy metaprogramming talk at 10:00 and a Grails tutorial at 14:20, 
both in Tweed (the room, not the fabric).

The final text of his latest book, "Claude Code: Up and Running", was just submitted to 
O'Reilly Media and should be available soon.'''

def dates = ai.extractDates(bio) as LocalDate[]
println "Dates: ${dates*.dayOfWeek}"
println "Earliest: ${dates.min()}"
println()

def ken = ai.extractPerson(bio) as Person
println "Record: $ken"
println "Age at the talk: ${ken.birthday().until(LocalDate.of(2026, 10, 12)).years}"
val (name: who, birthday: born) = ken              // Groovy 6: destructure by property name
println "Destructured: $who, born $born"
println()

def mood = ai.classifySentiment('Conference wifi failed during my demo again.') as Sentiment
println "Mood: $mood"

def count = ai.countTheNumberOfDistinctCities(bio) as int
println "Cities: $count"
println()

// Teaching moment: generics are erased, so `as List<LocalDate>` is just `as List`.
// The schema can only ask for an array of strings. Arrays keep their component type.
println "as List<LocalDate> gives: ${(ai.extractDates(bio) as List<LocalDate>)*.getClass()*.simpleName}"
