package com.kousenit.ai

import groovy.http.Body
import groovy.http.HttpBuilderClient
import groovy.http.Post

/**
 * OpenRouter's chat completions endpoint as a Groovy 6 declarative HTTP client.
 * The AST transform generates the implementation; {@code create { header ... }} supplies the
 * runtime bearer token, the same way a Spring RestClient bean would carry it.
 * Note that {@code create(Closure)} starts from a blank builder, so it must restate the base URI.
 */
@HttpBuilderClient(OpenRouterApi.BASE_URI)
interface OpenRouterApi {
    String BASE_URI = 'https://openrouter.ai/api/v1/'   // trailing slash: a leading-slash path would replace /api/v1

    /** Raw JSON body, so the caller can record it before parsing. */
    @Post('chat/completions')
    String chat(@Body Map body)
}
