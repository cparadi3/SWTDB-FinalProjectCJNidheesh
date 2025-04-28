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

class ReceiptServletTest {

    private ReceiptServlet receiptServlet;
    private HttpServletRequest req;
    private HttpServletResponse res;
    private HttpSession session;
    private RequestDispatcher dispatcher;
    private BookService bookService;

    @BeforeEach
    void setUp() {
        receiptServlet = new ReceiptServlet();
        req = mock(HttpServletRequest.class);
        res = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        dispatcher = mock(RequestDispatcher.class);
        bookService = mock(BookServiceImpl.class);

        receiptServlet.bookService = bookService; // inject mock

        when(req.getSession()).thenReturn(session);
        when(req.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    void testReceipt_whenCustomerNotLoggedIn() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, session)).thenReturn(false);

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            receiptServlet.service(req, res);

            verify(dispatcher, times(1)).include(req, res);
            pw.flush();
            String output = sw.toString();
            assertTrue(output.contains("Please Login First to Continue!!"));
        }
    }

    @Test
    void testReceipt_whenPurchaseSuccess() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, session)).thenReturn(true);

            Book book = new Book();
            book.setBarcode("B001");
            book.setName("Book 1");
            book.setAuthor("Author 1");
            book.setPrice(100);
            book.setQuantity(10);

            when(req.getParameter("qty1")).thenReturn("2");
            when(req.getParameter("checked1")).thenReturn("pay");
            when(bookService.getAllBooks()).thenReturn(List.of(book));

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            receiptServlet.service(req, res);

            verify(dispatcher, times(1)).include(req, res);
            verify(bookService, times(1)).updateBookQtyById("B001", 8);
            pw.flush();
            String output = sw.toString();
            assertTrue(output.contains("Total Paid Amount"));
            assertTrue(output.contains("Book 1"));
        }
    }

    @Test
    void testReceipt_whenQuantityExceedsAvailable() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, session)).thenReturn(true);

            Book book = new Book();
            book.setBarcode("B002");
            book.setName("Book 2");
            book.setAuthor("Author 2");
            book.setPrice(150);
            book.setQuantity(1);

            when(req.getParameter("qty1")).thenReturn("5"); // more than available
            when(req.getParameter("checked1")).thenReturn("pay");
            when(bookService.getAllBooks()).thenReturn(List.of(book));

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            receiptServlet.service(req, res);

            verify(dispatcher, times(1)).include(req, res);
            pw.flush();
            String output = sw.toString();
            assertTrue(output.contains("Please Select the Qty less than Available"));
        }
    }

    @Test
    void testReceipt_whenCheckboxMissing() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, session)).thenReturn(true);

            Book book = new Book();
            book.setBarcode("B003");
            book.setName("Book 3");
            book.setAuthor("Author 3");
            book.setPrice(200);
            book.setQuantity(5);

            when(req.getParameter("qty1")).thenReturn("2");
            when(req.getParameter("checked1")).thenReturn(null); // checkbox missing
            when(bookService.getAllBooks()).thenReturn(List.of(book));

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            receiptServlet.service(req, res);

            verify(dispatcher, times(1)).include(req, res);
            pw.flush();
            String output = sw.toString();
            assertTrue(output.contains("Total Paid Amount"));
        }
    }

    @Test
    void testReceipt_whenExceptionThrown() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, session)).thenReturn(true);

            when(bookService.getAllBooks()).thenThrow(new RuntimeException("Forced Exception"));

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            receiptServlet.service(req, res);

            // No assert needed, just ensuring servlet does not crash
        }
    }

    @Test
    void testReceipt_whenCheckboxValueNotPay() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, session)).thenReturn(true);

            Book book = new Book();
            book.setBarcode("B004");
            book.setName("Book 4");
            book.setAuthor("Author 4");
            book.setPrice(250);
            book.setQuantity(5);

            when(req.getParameter("qty1")).thenReturn("2");
            when(req.getParameter("checked1")).thenReturn("no_pay"); // Not "pay"
            when(bookService.getAllBooks()).thenReturn(List.of(book));

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            receiptServlet.service(req, res);

            verify(dispatcher, times(1)).include(req, res);
            pw.flush();
            String output = sw.toString();
            assertTrue(output.contains("Total Paid Amount")); // Receipt page still shown
        }
    }
}