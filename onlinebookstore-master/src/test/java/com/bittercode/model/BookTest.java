package com.bittercode.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class BookTest {

    @Test
    void testParameterizedConstructor() {
        Book book = new Book("12345", "Sample Book", "Author Name", 19.99, 5);

        assertEquals("12345", book.getBarcode());
        assertEquals("Sample Book", book.getName());
        assertEquals("Author Name", book.getAuthor());
        assertEquals(19.99, book.getPrice());
        assertEquals(5, book.getQuantity());
    }

    @Test
    void testDefaultConstructor() {
        Book book = new Book();
        assertNotNull(book);
    }

    @Test
    void testSetAndGetBarcode() {
        Book book = new Book();
        book.setBarcode("98765");
        assertEquals("98765", book.getBarcode());
    }

    @Test
    void testSetAndGetName() {
        Book book = new Book();
        book.setName("New Book");
        assertEquals("New Book", book.getName());
    }

    @Test
    void testSetAndGetAuthor() {
        Book book = new Book();
        book.setAuthor("New Author");
        assertEquals("New Author", book.getAuthor());
    }

    @Test
    void testSetAndGetQuantity() {
        Book book = new Book();
        book.setQuantity(10);
        assertEquals(10, book.getQuantity());
    }

    @Test
    void testSetAndGetPrice() {
        Book book = new Book();
        book.setPrice(29.99);
        assertEquals(29.99, book.getPrice());
    }
}