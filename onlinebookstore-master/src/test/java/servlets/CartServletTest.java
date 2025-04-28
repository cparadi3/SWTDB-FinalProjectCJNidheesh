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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import static org.mockito.Mockito.mockStatic;
import org.mockito.MockedStatic;

class CartServletTest {

    private CartServlet cartServlet;
    private HttpServletRequest req;
    private HttpServletResponse res;
    private HttpSession session;
    private RequestDispatcher dispatcher;
    private BookService bookService;

    @BeforeEach
    void setUp() {
        cartServlet = new CartServlet();
        req = mock(HttpServletRequest.class);
        res = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        dispatcher = mock(RequestDispatcher.class);
        bookService = mock(BookServiceImpl.class);

        cartServlet.bookService = bookService; // inject mock BookService

        when(req.getSession()).thenReturn(session);
        when(req.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    void testService_whenUserNotLoggedIn() throws Exception {
        when(session.getAttribute("userRole")).thenReturn(null);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(res.getWriter()).thenReturn(pw);

        cartServlet.service(req, res);

        verify(dispatcher, times(1)).include(req, res);
        pw.flush();
        String output = sw.toString();
        assertTrue(output.contains("Please Login First to Continue!!"));
    }

    @Test
    void testService_whenNoItemsInCart() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, session)).thenReturn(true);
            storeUtilMock.when(() -> StoreUtil.updateCartItems(req)).thenAnswer(invocation -> null);
            storeUtilMock.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("cart"))).thenAnswer(invocation -> null);

            when(session.getAttribute("userRole")).thenReturn(UserRole.CUSTOMER.name());
            when(session.getAttribute("items")).thenReturn(null);
            when(bookService.getBooksByCommaSeperatedBookIds("")).thenReturn(List.of());

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            cartServlet.service(req, res);

            verify(dispatcher, times(1)).include(req, res);
            pw.flush();
            String output = sw.toString();
            assertTrue(output.contains("No Items In the Cart"));
        }
    }


    @Test
    void testService_whenCartHasItems() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, session)).thenReturn(true);
            storeUtilMock.when(() -> StoreUtil.updateCartItems(req)).thenAnswer(invocation -> null);
            storeUtilMock.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("cart"))).thenAnswer(invocation -> null);

            when(session.getAttribute("userRole")).thenReturn(UserRole.CUSTOMER.name());
            when(session.getAttribute("items")).thenReturn("1,2");
            when(session.getAttribute("qty_1")).thenReturn(2);
            when(session.getAttribute("qty_2")).thenReturn(1);

            Book book1 = new Book();
            book1.setBarcode("1");
            book1.setName("Book One");
            book1.setAuthor("Author A");
            book1.setPrice(100);

            Book book2 = new Book();
            book2.setBarcode("2");
            book2.setName("Book Two");
            book2.setAuthor("Author B");
            book2.setPrice(150);

            when(bookService.getBooksByCommaSeperatedBookIds("1,2"))
                    .thenReturn(List.of(book1, book2));

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            cartServlet.service(req, res);

            verify(dispatcher, times(1)).include(req, res);
            pw.flush();
            String output = sw.toString();

            assertTrue(output.contains("Shopping Cart"));

            // Book 1 details
            assertTrue(output.contains("Book One"));
            assertTrue(output.contains("Author A"));
            assertTrue(output.contains("&#8377;"));
            assertTrue(output.contains("1")); // Barcode 1
            assertTrue(output.contains("2")); // Quantity 2 for book1
            assertTrue(output.contains("removeFromCart"));
            assertTrue(output.contains("addToCart"));

            // Book 2 details
            assertTrue(output.contains("Book Two"));
            assertTrue(output.contains("Author B"));
            assertTrue(output.contains("2")); // Barcode 2
            assertTrue(output.contains("1")); // Quantity 1 for book2
        }
    }

    @Test
    void testService_whenExceptionIsThrown() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, session)).thenReturn(true);
            storeUtilMock.when(() -> StoreUtil.updateCartItems(req)).thenThrow(new RuntimeException("Forced Exception"));
            storeUtilMock.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("cart"))).thenAnswer(invocation -> null);

            when(session.getAttribute("userRole")).thenReturn(UserRole.CUSTOMER.name());

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            // just call service and ensure no crash
            cartServlet.service(req, res);
        }
    }

    @Test
    void testService_whenBookListIsNull() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, session)).thenReturn(true);
            storeUtilMock.when(() -> StoreUtil.updateCartItems(req)).thenAnswer(invocation -> null);
            storeUtilMock.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("cart"))).thenAnswer(invocation -> null);

            when(session.getAttribute("userRole")).thenReturn(UserRole.CUSTOMER.name());
            when(session.getAttribute("items")).thenReturn(null); // no items
            when(bookService.getBooksByCommaSeperatedBookIds("")).thenReturn(null); // Force book list to be null

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            cartServlet.service(req, res);

            verify(dispatcher, times(1)).include(req, res);
            pw.flush();
            String output = sw.toString();
            assertTrue(output.contains("No Items In the Cart"));
        }
    }
}