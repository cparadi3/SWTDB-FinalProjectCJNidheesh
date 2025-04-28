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

class ViewBookServletTest {

    private ViewBookServlet viewBookServlet;
    private HttpServletRequest req;
    private HttpServletResponse res;
    private HttpSession session;
    private RequestDispatcher dispatcher;
    private BookService bookService;

    @BeforeEach
    void setUp() {
        viewBookServlet = new ViewBookServlet();
        req = mock(HttpServletRequest.class);
        res = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        dispatcher = mock(RequestDispatcher.class);
        bookService = mock(BookServiceImpl.class);

        viewBookServlet.bookService = bookService; // inject mock service

        when(req.getSession()).thenReturn(session);
        when(req.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    void testViewBook_whenCustomerNotLoggedIn() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, session)).thenReturn(false);

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            viewBookServlet.service(req, res);

            verify(dispatcher, times(1)).include(req, res);
            pw.flush();
            String output = sw.toString();
            assertTrue(output.contains("Please Login First to Continue!!"));
        }
    }

    @Test
    void testViewBook_whenBooksAvailable_AddToCartShown() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, session)).thenReturn(true);
            storeUtilMock.when(() -> StoreUtil.updateCartItems(req)).thenAnswer(invocation -> null);
            storeUtilMock.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("books"))).thenAnswer(invocation -> null);

            Book book = new Book();
            book.setBarcode("B001");
            book.setName("Test Book");
            book.setAuthor("Test Author");
            book.setPrice(100);
            book.setQuantity(10);

            when(bookService.getAllBooks()).thenReturn(List.of(book));
            when(session.getAttribute("qty_B001")).thenReturn(null); // No cart item yet

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            viewBookServlet.service(req, res);

            verify(dispatcher, times(1)).include(req, res);
            pw.flush();
            String output = sw.toString();
            assertTrue(output.contains("Add To Cart"));
            assertTrue(output.contains("Test Book"));
            assertTrue(output.contains("Test Author"));
        }
    }

    @Test
    void testViewBook_whenBooksAvailable_RemoveFromCartShown() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, session)).thenReturn(true);
            storeUtilMock.when(() -> StoreUtil.updateCartItems(req)).thenAnswer(invocation -> null);
            storeUtilMock.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("books"))).thenAnswer(invocation -> null);

            Book book = new Book();
            book.setBarcode("B002");
            book.setName("Test Book 2");
            book.setAuthor("Test Author 2");
            book.setPrice(200);
            book.setQuantity(5);

            when(bookService.getAllBooks()).thenReturn(List.of(book));
            when(session.getAttribute("qty_B002")).thenReturn(1); // Already in cart

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            viewBookServlet.service(req, res);

            verify(dispatcher, times(1)).include(req, res);
            pw.flush();
            String output = sw.toString();
            assertTrue(output.contains("removeFromCart"));
            assertTrue(output.contains("addToCart"));
            assertTrue(output.contains("Test Book 2"));
        }
    }

    @Test
    void testViewBook_whenExceptionThrown() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, session)).thenReturn(true);
            storeUtilMock.when(() -> StoreUtil.updateCartItems(req)).thenThrow(new RuntimeException("Forced Exception"));

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            viewBookServlet.service(req, res);

            // No assertion needed — just ensuring servlet catches the exception
        }
    }

    @Test
    void testViewBook_whenBookOutOfStock() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, session)).thenReturn(true);
            storeUtilMock.when(() -> StoreUtil.updateCartItems(req)).thenAnswer(invocation -> null);
            storeUtilMock.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("books"))).thenAnswer(invocation -> null);

            Book book = new Book();
            book.setBarcode("B003");
            book.setName("Out Of Stock Book");
            book.setAuthor("Author 3");
            book.setPrice(300);
            book.setQuantity(0);

            when(bookService.getAllBooks()).thenReturn(List.of(book));

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            viewBookServlet.service(req, res);

            verify(dispatcher, times(1)).include(req, res);
            pw.flush();
            String output = sw.toString();
            assertTrue(output.contains("Out Of Stock")); // Check for out of stock button
        }
    }

    @Test
    void testViewBook_whenBookTrending() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, session)).thenReturn(true);
            storeUtilMock.when(() -> StoreUtil.updateCartItems(req)).thenAnswer(invocation -> null);
            storeUtilMock.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("books"))).thenAnswer(invocation -> null);

            Book book = new Book();
            book.setBarcode("B004");
            book.setName("Trending Book");
            book.setAuthor("Famous Author");
            book.setPrice(500);
            book.setQuantity(25); // quantity > 20

            when(bookService.getAllBooks()).thenReturn(List.of(book));

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            viewBookServlet.service(req, res);

            verify(dispatcher, times(1)).include(req, res);
            pw.flush();
            String output = sw.toString();
            assertTrue(output.contains("Trending")); // Check trending
        }
    }

}
