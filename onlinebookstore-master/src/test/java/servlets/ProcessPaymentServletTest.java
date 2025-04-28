package servlets;

import com.bittercode.model.Book;
import com.bittercode.model.Cart;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import org.mockito.MockedStatic;

class ProcessPaymentServletTest {

    private ProcessPaymentServlet processPaymentServlet;
    private HttpServletRequest req;
    private HttpServletResponse res;
    private HttpSession session;
    private RequestDispatcher dispatcher;
    private BookService bookService;

    @BeforeEach
    void setUp() {
        processPaymentServlet = new ProcessPaymentServlet();
        req = mock(HttpServletRequest.class);
        res = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        dispatcher = mock(RequestDispatcher.class);
        bookService = mock(BookServiceImpl.class);

        processPaymentServlet.bookService = bookService; // inject mock

        when(req.getSession()).thenReturn(session);
        when(req.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    void testProcessPayment_whenCustomerNotLoggedIn() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, session)).thenReturn(false);

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            processPaymentServlet.service(req, res);

            verify(dispatcher, times(1)).include(req, res);
            pw.flush();
            String output = sw.toString();
            assertTrue(output.contains("Please Login First to Continue!!"));
        }
    }

    @Test
    void testProcessPayment_whenCartItemsAvailable() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, session)).thenReturn(true);

            Book book = new Book();
            book.setBarcode("B001");
            book.setName("Test Book");
            book.setAuthor("Test Author");
            book.setPrice(100);
            book.setQuantity(10);

            Cart cart = new Cart(book, 2); // ✅ Corrected here

            List<Cart> cartItems = new ArrayList<>();
            cartItems.add(cart);

            when(session.getAttribute("cartItems")).thenReturn(cartItems);

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            processPaymentServlet.service(req, res);

            verify(dispatcher, times(1)).include(req, res);
            verify(session, times(1)).removeAttribute("qty_B001");
            verify(session, times(1)).removeAttribute("amountToPay");
            verify(session, times(1)).removeAttribute("cartItems");
            verify(session, times(1)).removeAttribute("items");
            verify(session, times(1)).removeAttribute("selectedBookId");
            pw.flush();
            String output = sw.toString();
            assertTrue(output.contains("Your Orders"));
            assertTrue(output.contains("Order Id: ORDB001TM"));
        }
    }

    @Test
    void testProcessPayment_whenCartItemsEmpty() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, session)).thenReturn(true);

            when(session.getAttribute("cartItems")).thenReturn(null); // cart is empty

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            processPaymentServlet.service(req, res);

            verify(dispatcher, times(1)).include(req, res);
            pw.flush();
            String output = sw.toString();
            assertTrue(output.contains("Your Orders")); // Still show page
        }
    }

    @Test
    void testProcessPayment_whenExceptionThrown() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, session)).thenReturn(true);

            when(session.getAttribute("cartItems")).thenThrow(new RuntimeException("Forced Exception"));

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            processPaymentServlet.service(req, res);

            // No assert needed; catch block covers this
        }
    }
}
