package com.bittercode.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class AddressTest {

    @Test
    void testAddressFields() {
        Address address = new Address();

        address.setAddressLine1("123 Main Street");
        address.setAddressLine2("Apt 4B");
        address.setCity("New York");
        address.setState("NY");
        address.setCountry("USA");
        address.setPinCode(10001L);
        address.setPhone("1234567890");

        assertEquals("123 Main Street", address.getAddressLine1());
        assertEquals("Apt 4B", address.getAddressLine2());
        assertEquals("New York", address.getCity());
        assertEquals("NY", address.getState());
        assertEquals("USA", address.getCountry());
        assertEquals(10001L, address.getPinCode());
        assertEquals("1234567890", address.getPhone());
    }

    @Test
    void testDefaultConstructor() {
        Address address = new Address();
        assertNotNull(address);
    }
}