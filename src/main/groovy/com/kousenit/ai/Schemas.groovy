package com.kousenit.ai

import java.time.LocalDate

/**
 * JSON Schema from a Java type, and the way back.
 * Handles scalars, LocalDate, enums, arrays, and records (nested as deep as you like).
 *
 * Note that {@code as List<LocalDate>} erases to plain List at runtime, so the element
 * type is unknown and you get strings. Ask for {@code LocalDate[]} instead: arrays keep
 * their component type.
 */
class Schemas {

    /** Structured outputs want an object at the top level, so wrap whatever we need in one. */
    static Map wrap(Map schema) {
        [type: 'object', properties: [result: schema], required: ['result'], additionalProperties: false]
    }

    // Switching on a Class: a list case means "is one of these", a closure case is a predicate.
    static Map of(Class type) {
        switch (type) {
            case [int, Integer, long, Long, short, Short, byte, Byte, BigInteger] -> [type: 'integer']
            case [double, Double, float, Float, BigDecimal]                      -> [type: 'number']
            case [boolean, Boolean]                                             -> [type: 'boolean']
            case [LocalDate]                                                    -> [type: 'string', format: 'date']
            case { it.isArray() }                                               -> [type: 'array', items: of(type.componentType)]
            case { List.isAssignableFrom(it) }                                  -> [type: 'array', items: [type: 'string']]
            case { it.isEnum() }                                                -> [type: 'string', enum: type.enumConstants*.name()]
            case { it.isRecord() }                                              -> recordSchema(type)
            default                                                             -> [type: 'string']
        }
    }

    static Object coerce(Object value, Class type) {
        switch (type) {
            case [LocalDate]        -> LocalDate.parse(value as String)
            case { it.isArray() }   -> value.collect { coerce(it, type.componentType) }.asType(type)
            case { it.isEnum() }    -> Enum.valueOf(type, value as String)
            case { it.isRecord() }  -> newRecord(value as Map, type)
            default                 -> value.asType(type)
        }
    }

    private static Map recordSchema(Class type) {
        def components = type.recordComponents
        [type                : 'object',
         properties          : components.collectEntries { [it.name, of(it.type)] },
         required            : components*.name,
         additionalProperties: false]
    }

    private static Object newRecord(Map values, Class type) {
        def components = type.recordComponents
        type.getDeclaredConstructor(components*.type as Class[])
            .newInstance(components.collect { coerce(values[it.name], it.type) } as Object[])
    }
}
