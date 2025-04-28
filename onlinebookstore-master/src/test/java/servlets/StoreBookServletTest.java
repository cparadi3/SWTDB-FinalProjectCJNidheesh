package servlets;

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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import org.mockito.MockedStatic;

class StoreBookServletTest {

    private StoreBookServlet storeBookServlet;
    private HttpServletRequest req;
    private HttpServletResponse res;
    private HttpSession session;
    private RequestDispatcher dispatcher;
    private BookService bookService;

    @BeforeEach
    void setUp() {
        storeBookServlet = new StoreBookServlet();
        req = mock(HttpServletRequest.class);
        res = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        dispatcher = mock(RequestDispatcher.class);
        bookService = mock(BookServiceImpl.class);

        storeBookServlet.bookService = bookService; // inject mock

        when(req.getSession()).thenReturn(session);
        when(req.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    void testStoreBooks_whenSellerNotLoggedIn() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.SELLER, session)).thenReturn(false);

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            storeBookServlet.service(req, res);

            verify(dispatcher, times(1)).include(req, res);
            pw.flush();
            String output = sw.toString();
            assertTrue(output.contains("Please Login First to Continue!!"));
        }
    }

    @Test
    void testStoreBooks_whenNoBooksAvailable() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.SELLER, session)).thenReturn(true);
            storeUtilMock.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("storebooks"))).thenAnswer(invocation -> null);

            when(bookService.getAllBooks()).thenReturn(List.of()); // Empty book list

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            storeBookServlet.service(req, res);

            verify(dispatcher, times(1)).include(req, res);
            pw.flush();
            String output = sw.toString();
            assertTrue(output.contains("No Books Available in the store"));
        }
    }

    @Test
    void testStoreBooks_whenBooksAvailable() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.SELLER, session)).thenReturn(true);
            storeUtilMock.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("storebooks"))).thenAnswer(invocation -> null);

            Book book1 = new Book();
            book1.setBarcode("101");
            book1.setName("Book 1");
            book1.setAuthor("Author 1");
            book1.setPrice(100);
            book1.setQuantity(10);

            when(bookService.getAllBooks()).thenReturn(List.of(book1));

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            storeBookServlet.service(req, res);

            verify(dispatcher, times(1)).include(req, res);
            pw.flush();
            String output = sw.toString();
            assertTrue(output.contains("Book 1"));
            assertTrue(output.contains("Author 1"));
            assertTrue(output.contains("100"));
            assertTrue(output.contains("10"));
        }
    }

    @Test
    void testStoreBooks_whenExceptionThrown() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.SELLER, session)).thenReturn(true);
            storeUtilMock.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("storebooks"))).thenThrow(new RuntimeException("Forced Exception"));

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            storeBookServlet.service(req, res);

            // No assertion needed; just make sure servlet does not crash
        }
    }

    @Test
    void testStoreBooks_whenBooksNull() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.SELLER, session)).thenReturn(true);
            storeUtilMock.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("storebooks"))).thenAnswer(invocation -> null);

            when(bookService.getAllBooks()).thenReturn(null); // books == null case

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            storeBookServlet.service(req, res);

            verify(dispatcher, times(1)).include(req, res);
            pw.flush();
            String output = sw.toString();
            assertTrue(output.contains("No Books Available in the store"));
        }
    }
}