package com.bittercode.constant;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Optional;

public class ResponseCodeTest {

    @Test
    public void testAllResponseCodesGetters() {
        for (ResponseCode code : ResponseCode.values()) {
            assertNotNull(code.getMessage());
            assertTrue(code.getCode() > 0);
        }
    }

    @Test
    public void testGetMessageByStatusCodeFound() {
        Optional<ResponseCode> response = ResponseCode.getMessageByStatusCode(200);
        assertTrue(response.isPresent());
        assertEquals(ResponseCode.SUCCESS, response.get());
    }

    @Test
    public void testGetMessageByStatusCodeNotFound() {
        Optional<ResponseCode> response = ResponseCode.getMessageByStatusCode(999); // non-existing code
        assertFalse(response.isPresent());
    }
}