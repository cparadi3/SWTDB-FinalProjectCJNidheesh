package servlets;

import com.bittercode.constant.db.BooksDBConstants;
import com.bittercode.model.Book;
import com.bittercode.model.UserRole;
import com.bittercode.service.BookService;
import com.bittercode.service.impl.BookServiceImpl;
import com.bittercode.util.StoreUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import org.mockito.MockedStatic;

class UpdateBookServletTest {

    private UpdateBookServlet updateBookServlet;
    private HttpServletRequest req;
    private HttpServletResponse res;
    private HttpSession session;
    private RequestDispatcher dispatcher;
    private BookService bookService;

    @BeforeEach
    void setUp() {
        updateBookServlet = new UpdateBookServlet();
        req = mock(HttpServletRequest.class);
        res = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        dispatcher = mock(RequestDispatcher.class);
        bookService = mock(BookServiceImpl.class);

        updateBookServlet.bookService = bookService; // inject mock

        when(req.getSession()).thenReturn(session);
        when(req.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    void testUpdateBook_whenSellerNotLoggedIn() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.SELLER, session)).thenReturn(false);

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            updateBookServlet.service(req, res);

            verify(dispatcher, times(1)).include(req, res);
            pw.flush();
            String output = sw.toString();
            assertTrue(output.contains("Please Login First to Continue!!"));
        }
    }

    @Test
    void testUpdateBook_whenShowingUpdateForm() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.SELLER, session)).thenReturn(true);
            storeUtilMock.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("storebooks"))).thenAnswer(invocation -> null);

            when(req.getParameter("updateFormSubmitted")).thenReturn(null);
            when(req.getParameter("bookId")).thenReturn("B001");

            Book book = new Book("B001", "Book Name", "Author Name", 150, 10);
            when(bookService.getBookById("B001")).thenReturn(book);

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            updateBookServlet.service(req, res);

            verify(dispatcher, times(1)).include(req, res);
            pw.flush();
            String output = sw.toString();
            assertTrue(output.contains("Book Name"));
            assertTrue(output.contains("Author Name"));
            assertTrue(output.contains("150"));
            assertTrue(output.contains("10"));
        }
    }

    @Test
    void testUpdateBook_whenUpdateSuccess() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.SELLER, session)).thenReturn(true);
            storeUtilMock.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("storebooks"))).thenAnswer(invocation -> null);

            when(req.getParameter("updateFormSubmitted")).thenReturn("true");
            when(req.getParameter(BooksDBConstants.COLUMN_NAME)).thenReturn("Updated Book");
            when(req.getParameter(BooksDBConstants.COLUMN_BARCODE)).thenReturn("B002");
            when(req.getParameter(BooksDBConstants.COLUMN_AUTHOR)).thenReturn("Updated Author");
            when(req.getParameter(BooksDBConstants.COLUMN_PRICE)).thenReturn("200");
            when(req.getParameter(BooksDBConstants.COLUMN_QUANTITY)).thenReturn("15");

            when(bookService.updateBook(any())).thenReturn("SUCCESS");

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            updateBookServlet.service(req, res);

            verify(dispatcher, times(1)).include(req, res);
            pw.flush();
            String output = sw.toString();
            assertTrue(output.contains("Book Detail Updated Successfully"));
        }
    }

    @Test
    void testUpdateBook_whenUpdateFailure() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.SELLER, session)).thenReturn(true);
            storeUtilMock.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("storebooks"))).thenAnswer(invocation -> null);

            when(req.getParameter("updateFormSubmitted")).thenReturn("true");
            when(req.getParameter(BooksDBConstants.COLUMN_NAME)).thenReturn("Updated Book");
            when(req.getParameter(BooksDBConstants.COLUMN_BARCODE)).thenReturn("B003");
            when(req.getParameter(BooksDBConstants.COLUMN_AUTHOR)).thenReturn("Author 3");
            when(req.getParameter(BooksDBConstants.COLUMN_PRICE)).thenReturn("250");
            when(req.getParameter(BooksDBConstants.COLUMN_QUANTITY)).thenReturn("5");

            when(bookService.updateBook(any())).thenReturn("FAILURE");

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            updateBookServlet.service(req, res);

            verify(dispatcher, times(1)).include(req, res);
            pw.flush();
            String output = sw.toString();
            assertTrue(output.contains("Failed to Update Book"));
        }
    }

    @Test
    void testUpdateBook_whenExceptionThrown() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.SELLER, session)).thenReturn(true);
            storeUtilMock.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("storebooks"))).thenAnswer(invocation -> null);

            when(req.getParameter("updateFormSubmitted")).thenReturn(null);
            when(req.getParameter("bookId")).thenReturn("B001");

            // Force exception inside try block
            when(bookService.getBookById("B001")).thenThrow(new RuntimeException("Forced Exception"));

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            updateBookServlet.service(req, res);

            // No assertion needed; just ensure servlet doesn't crash
        }
    }

    @Test
    void testUpdateBook_whenBookIdIsNull() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.SELLER, session)).thenReturn(true);
            storeUtilMock.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("storebooks"))).thenAnswer(invocation -> null);

            when(req.getParameter("updateFormSubmitted")).thenReturn(null);
            when(req.getParameter("bookId")).thenReturn(null); // bookId is null

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            updateBookServlet.service(req, res);

            verify(dispatcher, times(1)).include(req, res);
            // No additional asserts needed — just testing that code path is taken
        }
    }
}