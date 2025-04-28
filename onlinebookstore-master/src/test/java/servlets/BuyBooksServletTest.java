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

class BuyBooksServletTest {

    private BuyBooksServlet buyBooksServlet;
    private HttpServletRequest req;
    private HttpServletResponse res;
    private HttpSession session;
    private RequestDispatcher dispatcher;
    private BookService bookService;

    @BeforeEach
    void setUp() {
        buyBooksServlet = new BuyBooksServlet();
        req = mock(HttpServletRequest.class);
        res = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        dispatcher = mock(RequestDispatcher.class);
        bookService = mock(BookServiceImpl.class);

        buyBooksServlet.bookService = bookService; // inject mock

        when(req.getSession()).thenReturn(session);
        when(req.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    void testBuyBooks_whenCustomerNotLoggedIn() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, session)).thenReturn(false);

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            buyBooksServlet.doPost(req, res);

            verify(dispatcher, times(1)).include(req, res);
            pw.flush();
            String output = sw.toString();
            assertTrue(output.contains("Please Login First to Continue!!"));
        }
    }

    @Test
    void testBuyBooks_whenBooksAvailable() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, session)).thenReturn(true);
            storeUtilMock.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("cart"))).thenAnswer(invocation -> null);

            Book book = new Book();
            book.setBarcode("B001");
            book.setName("Book Name");
            book.setAuthor("Author Name");
            book.setPrice(250);
            book.setQuantity(10);

            when(bookService.getAllBooks()).thenReturn(List.of(book));

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            buyBooksServlet.doPost(req, res);

            verify(dispatcher, times(1)).include(req, res);
            pw.flush();
            String output = sw.toString();
            assertTrue(output.contains("Books Available In Our Store"));
            assertTrue(output.contains("Book Name"));
            assertTrue(output.contains("Author Name"));
            assertTrue(output.contains("250"));
            assertTrue(output.contains("10"));
            assertTrue(output.contains("PAY NOW"));
        }
    }

    @Test
    void testBuyBooks_whenExceptionThrown() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, session)).thenReturn(true);
            storeUtilMock.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("cart"))).thenThrow(new RuntimeException("Forced Exception"));

            Book book = new Book();
            book.setBarcode("B002");
            book.setName("Another Book");
            book.setAuthor("Another Author");
            book.setPrice(150);
            book.setQuantity(5);

            when(bookService.getAllBooks()).thenReturn(List.of(book));

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            buyBooksServlet.doPost(req, res);

            // No assertion needed; just make sure servlet doesn't crash
        }
    }
}