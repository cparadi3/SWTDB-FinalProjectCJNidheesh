package com.bittercode.model;

import static org.junit.jupiter.api.Assertions.*;

import com.bittercode.constant.ResponseCode;
import org.junit.jupiter.api.Test;

class StoreExceptionTest {

    @Test
    void testConstructorWithErrorMessage() {
        StoreException exception = new StoreException("Error message only");
        assertEquals("Error message only", exception.getErrorMessage());
        assertEquals("BAD_REQUEST", exception.getErrorCode()); // corrected
        assertEquals(400, exception.getStatusCode()); // corrected
    }

    @Test
    void testConstructorWithResponseCode() {
        StoreException exception = new StoreException(ResponseCode.FAILURE);
        assertEquals(ResponseCode.FAILURE.name(), exception.getErrorCode());
        assertEquals(ResponseCode.FAILURE.getMessage(), exception.getErrorMessage());
        assertEquals(422, exception.getStatusCode());
    }

    @Test
    void testConstructorWithErrorCodeAndMessage() {
        StoreException exception = new StoreException("ERROR_123", "Something went wrong");
        assertEquals("ERROR_123", exception.getErrorCode());
        assertEquals("Something went wrong", exception.getErrorMessage());
        assertEquals(422, exception.getStatusCode()); // corrected
    }

    @Test
    void testConstructorWithStatusCodeErrorCodeAndMessage() {
        StoreException exception = new StoreException(500, "ERROR_500", "Internal Server Error");
        assertEquals(500, exception.getStatusCode());
        assertEquals("ERROR_500", exception.getErrorCode());
        assertEquals("Internal Server Error", exception.getErrorMessage());
    }

    @Test
    void testSetterMethods() {
        StoreException exception = new StoreException("Initial Error");
        exception.setErrorCode("NEW_CODE");
        exception.setErrorMessage("Updated Message");
        exception.setStatusCode(404);

        assertEquals("NEW_CODE", exception.getErrorCode());
        assertEquals("Updated Message", exception.getErrorMessage());
        assertEquals(404, exception.getStatusCode());
    }
}
