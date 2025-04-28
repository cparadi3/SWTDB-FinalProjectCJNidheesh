package com.bittercode.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    void testSetAndGetRoles() {
        User user = new User();

        // Create roles and set them
        UserRole role1 = UserRole.CUSTOMER;
        UserRole role2 = UserRole.SELLER;
        List<UserRole> roles = Arrays.asList(role1, role2);
        user.setRoles(roles);

        // Verify roles
        List<UserRole> retrievedRoles = user.getRoles();
        assertNotNull(retrievedRoles);
        assertEquals(2, retrievedRoles.size());
        assertTrue(retrievedRoles.contains(UserRole.CUSTOMER));
        assertTrue(retrievedRoles.contains(UserRole.SELLER));
    }

    @Test
    void testDefaultConstructor() {
        User user = new User();
        assertNotNull(user);
        assertNull(user.getRoles());
    }
}