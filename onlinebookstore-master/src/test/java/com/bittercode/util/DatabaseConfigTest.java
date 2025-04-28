package com.bittercode.util;

import static org.junit.Assert.*;
import org.junit.Test;

public class DatabaseConfigTest {

    @Test
    public void testDatabaseConfigFields() {
        // Access fields after static block
        assertNotNull(DatabaseConfig.DRIVER_NAME);
        assertNotNull(DatabaseConfig.DB_HOST);
        assertNotNull(DatabaseConfig.DB_PORT);
        assertNotNull(DatabaseConfig.DB_NAME);
        assertNotNull(DatabaseConfig.DB_USER_NAME);
        assertNotNull(DatabaseConfig.DB_PASSWORD);
        assertNotNull(DatabaseConfig.CONNECTION_STRING);
    }
}