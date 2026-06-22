package io.lb.impl.ktor.server.request

import io.lb.common.data.request.MiddlewareAuthHeader
import io.lb.common.data.request.MiddlewareAuthHeaderType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MiddlewareAuthHeaderTest {
    @Test
    fun `fromString with Bearer prefix returns Bearer type and stripped token`() {
        val result = MiddlewareAuthHeader.fromString("Bearer mytoken123")
        assertEquals(MiddlewareAuthHeaderType.Bearer, result.type)
        assertEquals("mytoken123", result.token)
        assertEquals("Bearer mytoken123", result.fullToken())
    }

    @Test
    fun `fromString with Basic prefix returns Basic type and stripped token`() {
        val result = MiddlewareAuthHeader.fromString("Basic dXNlcjpwYXNz")
        assertEquals(MiddlewareAuthHeaderType.Basic, result.type)
        assertEquals("dXNlcjpwYXNz", result.token)
        assertEquals("Basic dXNlcjpwYXNz", result.fullToken())
    }

    @Test
    fun `fromString with lowercase bearer prefix returns Bearer type`() {
        val result = MiddlewareAuthHeader.fromString("bearer mytoken")
        assertEquals(MiddlewareAuthHeaderType.Bearer, result.type)
        assertEquals("mytoken", result.token)
    }

    @Test
    fun `fromString with raw token and no prefix returns None type`() {
        val result = MiddlewareAuthHeader.fromString("rawtoken")
        assertEquals(MiddlewareAuthHeaderType.None, result.type)
        assertEquals("rawtoken", result.token)
        assertEquals("rawtoken", result.fullToken())
    }

    @Test
    fun `fromString with unknown prefix returns None type with full value as token`() {
        val result = MiddlewareAuthHeader.fromString("Digest somecredentials")
        assertEquals(MiddlewareAuthHeaderType.None, result.type)
        assertEquals("Digest somecredentials", result.token)
    }

    @Test
    fun `fromString with Bearer and multi-word token preserves full token`() {
        val result = MiddlewareAuthHeader.fromString("Bearer a.b.c")
        assertEquals(MiddlewareAuthHeaderType.Bearer, result.type)
        assertEquals("a.b.c", result.token)
    }
}
