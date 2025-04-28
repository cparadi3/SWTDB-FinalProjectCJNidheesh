package com.bittercode.service;

import com.bittercode.model.Book;
import com.bittercode.model.StoreException;
import com.bittercode.service.impl.BookServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.sql.PreparedStatement;

import static org.junit.Assert.*;

public class BookServiceTest {

    private BookService bookService;
    private MockedStatic<com.bittercode.util.DBUtil> mockedStatic;

    @Before
    public void setUp() {
        bookService = new BookServiceImpl();
    }

    @After
    public void tearDown() {
        if (mockedStatic != null) {
            mockedStatic.close();
        }
    }

    private void mockDBUtilToThrowException() throws SQLException {
        Connection mockConnection = Mockito.mock(Connection.class);
        Mockito.when(mockConnection.prepareStatement(Mockito.anyString()))
                .thenThrow(new SQLException("Forced SQLException"));

        mockedStatic = Mockito.mockStatic(com.bittercode.util.DBUtil.class);
        mockedStatic.when(com.bittercode.util.DBUtil::getConnection).thenReturn(mockConnection);
    }

    // --- Happy path tests ---

    @Test
    public void testAddBook() throws StoreException {
        Book book = new Book("B101", "Java Basics", "John Doe", 299.99, 10);
        String result = bookService.addBook(book);
        assertNotNull(result);
    }

    @Test
    public void testGetAllBooks() throws StoreException {
        List<Book> books = bookService.getAllBooks();
        assertNotNull(books);
    }

    @Test
    public void testGetBookById() throws StoreException {
        Book book = bookService.getBookById("B101");
        assertNotNull(book);
    }

    @Test
    public void testGetBooksByCommaSeparatedBookIds() throws StoreException {
        List<Book> books = bookService.getBooksByCommaSeperatedBookIds("'B101'");
        assertNotNull(books);
        assertFalse(books.isEmpty());
    }

    @Test
    public void testDeleteBookById() throws StoreException {
        Book book = new Book("B102", "Temp Book", "Jane Doe", 9.99, 1);
        bookService.addBook(book);

        String result = bookService.deleteBookById("B102");
        assertNotNull(result);
    }

    @Test
    public void testUpdateBookQtyById() throws StoreException {
        String result = bookService.updateBookQtyById("B101", 20);
        assertNotNull(result);
    }

    @Test
    public void testUpdateBook() throws StoreException {
        Book book = new Book("B101", "Updated Java Basics", "John Updated", 399.99, 15);
        String result = bookService.updateBook(book);
        assertNotNull(result);
    }

    // --- Negative/failure path tests ---

    @Test
    public void testAddBookFailure() throws StoreException {
        Book book = new Book("B101", "Duplicate Book", "Someone", 9.99, 1);
        String result = bookService.addBook(book);
        assertTrue(result.toLowerCase().contains("duplicate") || result.toLowerCase().contains("fail"));
    }

    @Test
    public void testGetBookByIdFailure() {
        try {
            Book book = bookService.getBookById(null);
            assertNull(book);
        } catch (Exception e) {
            fail("Exception was thrown: " + e.getMessage());
        }
    }

    @Test
    public void testGetAllBooksFailure() {
        try {
            List<Book> books = bookService.getAllBooks();
            assertNotNull(books);
        } catch (Exception e) {
            fail("Exception was thrown: " + e.getMessage());
        }
    }

    @Test
    public void testGetBooksByCommaSeparatedBookIdsFailure() {
        try {
            List<Book> books = bookService.getBooksByCommaSeperatedBookIds("''");
            assertNotNull(books);
        } catch (Exception e) {
            fail("Exception was thrown: " + e.getMessage());
        }
    }

    @Test
    public void testDeleteBookByIdFailure() throws StoreException {
        String result = bookService.deleteBookById("NON_EXISTENT");
        assertTrue(result.toLowerCase().contains("fail"));
    }

    @Test
    public void testDeleteBookByIdException() {
        try {
            String result = bookService.deleteBookById(null);
            assertNotNull(result);
        } catch (Exception e) {
            fail("Should be handled internally");
        }
    }

    @Test
    public void testUpdateBookQtyByIdFailure() throws StoreException {
        String result = bookService.updateBookQtyById("INVALID_ID", 5);
        assertNotNull(result);
    }

    @Test
    public void testUpdateBookQtyByIdException() {
        try {
            String result = bookService.updateBookQtyById(null, 10);
            assertNotNull(result);
        } catch (Exception e) {
            fail("Should not throw exception");
        }
    }

    @Test
    public void testUpdateBookFailure() throws StoreException {
        Book book = new Book("INVALID_ID", "Fail Update", "None", 0.0, 0);
        String result = bookService.updateBook(book);
        assertNotNull(result);
    }

    @Test
    public void testUpdateBookException() {
        Book book = new Book(); // Empty book
        try {
            String result = bookService.updateBook(book);
            assertNotNull(result);
        } catch (Exception e) {
            fail("Should not throw exception");
        }
    }

    @Test
    public void testAddBookException() {
        Book book = new Book(); // Missing fields
        try {
            String result = bookService.addBook(book);
            assertTrue(result.toLowerCase().contains("fail"));
        } catch (Exception e) {
            fail("Should not throw exception");
        }
    }

    // --- Forced SQLException Handling tests (using Mocked DBUtil) ---

    @Test
    public void testGetBookByIdExceptionHandling() throws Exception {
        mockDBUtilToThrowException();
        Book book = bookService.getBookById("any_id");
        assertNull(book);
    }

    @Test
    public void testGetAllBooksExceptionHandling() throws Exception {
        mockDBUtilToThrowException();
        List<Book> books = bookService.getAllBooks();
        assertNotNull(books);
    }

    @Test
    public void testDeleteBookByIdExceptionHandling() throws Exception {
        mockDBUtilToThrowException();
        String result = bookService.deleteBookById("any_id");
        assertTrue(result.contains("FAILURE"));
    }

    @Test
    public void testUpdateBookQtyByIdExceptionHandling() throws Exception {
        mockDBUtilToThrowException();
        String result = bookService.updateBookQtyById("any_id", 5);
        assertTrue(result.contains("FAILURE"));
    }

    @Test
    public void testUpdateBookExceptionHandling() throws Exception {
        mockDBUtilToThrowException();
        Book book = new Book("id", "name", "author", 100, 10);
        String result = bookService.updateBook(book);
        assertTrue(result.contains("FAILURE"));
    }

    @Test
    public void testGetBooksByCommaSeparatedBookIdsExceptionHandling() throws Exception {
        mockDBUtilToThrowException();
        List<Book> books = bookService.getBooksByCommaSeperatedBookIds("'B101'");
        assertNotNull(books); // Should still return empty list, not crash
    }

    @Test
    public void testAddBookWhenInsertFails() throws Exception {
        Connection mockConnection = Mockito.mock(Connection.class);
        PreparedStatement mockPreparedStatement = Mockito.mock(PreparedStatement.class);

        Mockito.when(mockConnection.prepareStatement(Mockito.anyString()))
                .thenReturn(mockPreparedStatement);
        Mockito.when(mockPreparedStatement.executeUpdate()).thenReturn(0); // Simulate insert fail

        mockedStatic = Mockito.mockStatic(com.bittercode.util.DBUtil.class);
        mockedStatic.when(com.bittercode.util.DBUtil::getConnection).thenReturn(mockConnection);

        BookService bookService = new BookServiceImpl();
        Book book = new Book("FAKEID", "Fake Book", "Fake Author", 0.0, 0);

        String result = bookService.addBook(book);

        assertTrue(result.contains("FAILURE"));
    }

}
