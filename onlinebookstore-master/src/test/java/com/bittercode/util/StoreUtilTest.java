package com.bittercode.util;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.bittercode.model.UserRole;
import org.junit.Test;

public class StoreUtilTest {

    @Test
    public void testIsLoggedInTrue() {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute(UserRole.CUSTOMER.toString())).thenReturn("testuser@example.com");

        boolean result = StoreUtil.isLoggedIn(UserRole.CUSTOMER, session);
        assertTrue(result);
    }

    @Test
    public void testIsLoggedInFalse() {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute(UserRole.CUSTOMER.toString())).thenReturn(null);

        boolean result = StoreUtil.isLoggedIn(UserRole.CUSTOMER, session);
        assertFalse(result);
    }

    @Test
    public void testSetActiveTab() {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        StoreUtil.setActiveTab(printWriter, "homeTab");

        String output = stringWriter.toString();
        assertTrue(output.contains("document.getElementById"));
        assertTrue(output.contains("classList.add"));
    }

    @Test
    public void testUpdateCartItemsAddToCart() {
        HttpSession session = mock(HttpSession.class);
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getParameter("selectedBookId")).thenReturn("book1");
        when(request.getParameter("addToCart")).thenReturn("true");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("items")).thenReturn(null);
        when(session.getAttribute("qty_book1")).thenReturn(null);

        StoreUtil.updateCartItems(request);

        verify(session).setAttribute("items", "book1");
        verify(session).setAttribute("qty_book1", 1);
    }

    @Test
    public void testUpdateCartItemsRemoveFromCart() {
        HttpSession session = mock(HttpSession.class);
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getParameter("selectedBookId")).thenReturn("book2");
        when(request.getParameter("addToCart")).thenReturn(null); // No "addToCart" param means remove
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("qty_book2")).thenReturn(1); // simulate only 1 quantity
        when(session.getAttribute("items")).thenReturn("book2,book3"); // <-- important fix here

        StoreUtil.updateCartItems(request);

        verify(session).removeAttribute("qty_book2");
        verify(session).setAttribute(eq("items"), anyString());
    }

    @Test
    public void testUpdateCartItemsAddNewBookWhenItemsExist() {
        HttpSession session = mock(HttpSession.class);
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getParameter("selectedBookId")).thenReturn("book1");
        when(request.getParameter("addToCart")).thenReturn("true");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("items")).thenReturn("book3"); // existing items, but not containing book1
        when(session.getAttribute("qty_book1")).thenReturn(null);

        StoreUtil.updateCartItems(request);

        verify(session).setAttribute("items", "book3,book1"); // book1 appended after book3
        verify(session).setAttribute("qty_book1", 1);
    }

    @Test
    public void testUpdateCartItemsDecreaseQuantity() {
        HttpSession session = mock(HttpSession.class);
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getParameter("selectedBookId")).thenReturn("book3");
        when(request.getParameter("addToCart")).thenReturn(null); // No addToCart, so remove
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("qty_book3")).thenReturn(3); // simulate quantity = 3
        when(session.getAttribute("items")).thenReturn("book3,book4");

        StoreUtil.updateCartItems(request);

        // Verify that qty is decremented
        verify(session).setAttribute("qty_book3", 2);
    }

    @Test
    public void testUpdateCartItemsAddToCartWhenAlreadyExists() {
        HttpSession session = mock(HttpSession.class);
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getParameter("selectedBookId")).thenReturn("book4");
        when(request.getParameter("addToCart")).thenReturn("true");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("items")).thenReturn("book1,book4");
        when(session.getAttribute("qty_book4")).thenReturn(2); // already has 2 quantity

        StoreUtil.updateCartItems(request);

        // Should add one more quantity
        verify(session).setAttribute("qty_book4", 3); // expected qty = 3
    }

    @Test
    public void testUpdateCartItemsWhenItemsEmptyString() {
        HttpSession session = mock(HttpSession.class);
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getParameter("selectedBookId")).thenReturn("book5");
        when(request.getParameter("addToCart")).thenReturn("true");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("items")).thenReturn(""); // items is empty string
        when(session.getAttribute("qty_book5")).thenReturn(null);

        StoreUtil.updateCartItems(request);

        verify(session).setAttribute("items", "book5"); // expect book5 set directly
        verify(session).setAttribute("qty_book5", 1);
    }

    @Test
    public void testUpdateCartItemsWhenSelectedBookIdIsNull() {
        HttpSession session = mock(HttpSession.class);
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getParameter("selectedBookId")).thenReturn(null); // simulate null bookId
        when(request.getSession()).thenReturn(session);

        StoreUtil.updateCartItems(request);

        // No exception should happen, and no cart update expected
        verify(session, never()).setAttribute(anyString(), any());
        verify(session, never()).removeAttribute(anyString());
    }

    @Test
    public void testUpdateCartItemsRemoveWhenQtyIsNull() {
        HttpSession session = mock(HttpSession.class);
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getParameter("selectedBookId")).thenReturn("book6");
        when(request.getParameter("addToCart")).thenReturn(null); // trigger remove from cart
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("qty_book6")).thenReturn(null); // simulate qty missing
        when(session.getAttribute("items")).thenReturn("book6,book7");

        StoreUtil.updateCartItems(request);

        // Should not throw exception, may still try to clean items
        verify(session).removeAttribute("qty_book6");
        verify(session).setAttribute(eq("items"), anyString());
    }

    @Test
    public void testStoreUtilConstructor() {
        new StoreUtil();
    }

}