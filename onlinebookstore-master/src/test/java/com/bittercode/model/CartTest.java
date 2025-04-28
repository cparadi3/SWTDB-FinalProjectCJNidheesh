package com.bittercode.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class CartTest {

    @Test
    void testCartFields() {
        Book book = new Book();
        Cart cart = new Cart(book, 5);

        assertEquals(book, cart.getBook());
        assertEquals(5, cart.getQuantity());

        Book newBook = new Book();
        cart.setBook(newBook);
        cart.setQuantity(10);

        assertEquals(newBook, cart.getBook());
        assertEquals(10, cart.getQuantity());
    }

    @Test
    void testConstructorNotNull() {
        Book book = new Book();
        Cart cart = new Cart(book, 2);

        assertNotNull(cart);
        assertNotNull(cart.getBook());
    }
}
