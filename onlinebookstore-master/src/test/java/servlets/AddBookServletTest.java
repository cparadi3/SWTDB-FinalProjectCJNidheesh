package servlets;

import com.bittercode.constant.db.BooksDBConstants;
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

class AddBookServletTest {

    private AddBookServlet addBookServlet;
    private HttpServletRequest req;
    private HttpServletResponse res;
    private HttpSession session;
    private RequestDispatcher dispatcher;
    private BookService bookService;

    @BeforeEach
    void setUp() {
        addBookServlet = new AddBookServlet();
        req = mock(HttpServletRequest.class);
        res = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        dispatcher = mock(RequestDispatcher.class);
        bookService = mock(BookServiceImpl.class);

        addBookServlet.bookService = bookService; // inject mock

        when(req.getSession()).thenReturn(session);
        when(req.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    void testAddBook_whenSellerNotLoggedIn() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.SELLER, session)).thenReturn(false);

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            addBookServlet.service(req, res);

            verify(dispatcher, times(1)).include(req, res);
            pw.flush();
            String output = sw.toString();
            assertTrue(output.contains("Please Login First to Continue!!"));
        }
    }

    @Test
    void testAddBook_whenBookNameIsNull() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.SELLER, session)).thenReturn(true);
            storeUtilMock.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("addbook"))).thenAnswer(invocation -> null);

            when(req.getParameter(BooksDBConstants.COLUMN_NAME)).thenReturn(null);

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            addBookServlet.service(req, res);

            verify(dispatcher, times(1)).include(req, res);
            pw.flush();
            String output = sw.toString();
            assertTrue(output.contains("Book Name"));
            assertTrue(output.contains("Book Author"));
            assertTrue(output.contains("Book Price"));
        }
    }

    @Test
    void testAddBook_whenAddSuccess() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.SELLER, session)).thenReturn(true);
            storeUtilMock.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("addbook"))).thenAnswer(invocation -> null);

            when(req.getParameter(BooksDBConstants.COLUMN_NAME)).thenReturn("Book1");
            when(req.getParameter(BooksDBConstants.COLUMN_AUTHOR)).thenReturn("Author1");
            when(req.getParameter(BooksDBConstants.COLUMN_PRICE)).thenReturn("100");
            when(req.getParameter(BooksDBConstants.COLUMN_QUANTITY)).thenReturn("5");

            when(bookService.addBook(any())).thenReturn("SUCCESS");

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            addBookServlet.service(req, res);

            verify(dispatcher, times(1)).include(req, res);
            pw.flush();
            String output = sw.toString();
            assertTrue(output.contains("Book Detail Updated Successfully"));
        }
    }

    @Test
    void testAddBook_whenAddFailure() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.SELLER, session)).thenReturn(true);
            storeUtilMock.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("addbook"))).thenAnswer(invocation -> null);

            when(req.getParameter(BooksDBConstants.COLUMN_NAME)).thenReturn("Book2");
            when(req.getParameter(BooksDBConstants.COLUMN_AUTHOR)).thenReturn("Author2");
            when(req.getParameter(BooksDBConstants.COLUMN_PRICE)).thenReturn("150");
            when(req.getParameter(BooksDBConstants.COLUMN_QUANTITY)).thenReturn("10");

            when(bookService.addBook(any())).thenReturn("FAILURE");

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            addBookServlet.service(req, res);

            verify(dispatcher, times(1)).include(req, res);
            pw.flush();
            String output = sw.toString();
            assertTrue(output.contains("Failed to Add Books!"));
        }
    }

    @Test
    void testAddBook_whenExceptionThrown() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.SELLER, session)).thenReturn(true);
            storeUtilMock.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("addbook"))).thenAnswer(invocation -> null);

            when(req.getParameter(BooksDBConstants.COLUMN_NAME)).thenReturn("Book3");
            when(req.getParameter(BooksDBConstants.COLUMN_AUTHOR)).thenReturn("Author3");
            when(req.getParameter(BooksDBConstants.COLUMN_PRICE)).thenReturn("200");
            when(req.getParameter(BooksDBConstants.COLUMN_QUANTITY)).thenReturn("5");

            when(bookService.addBook(any())).thenThrow(new RuntimeException("Forced Exception"));

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            addBookServlet.service(req, res);

            // No assertion needed; just ensure it doesn't crash
        }
    }

    @Test
    void testAddBook_whenBookNameIsBlank() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.SELLER, session)).thenReturn(true);
            storeUtilMock.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("addbook"))).thenAnswer(invocation -> null);

            when(req.getParameter(BooksDBConstants.COLUMN_NAME)).thenReturn(""); // Blank string

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            addBookServlet.service(req, res);

            verify(dispatcher, times(1)).include(req, res);
            pw.flush();
            String output = sw.toString();

            assertTrue(output.contains("Book Name"));
            assertTrue(output.contains("Book Author"));
            assertTrue(output.contains("Book Price"));
        }
    }
}