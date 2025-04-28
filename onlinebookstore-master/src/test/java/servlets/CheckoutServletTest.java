package servlets;

import com.bittercode.model.UserRole;
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

class CheckoutServletTest {

    private CheckoutServlet checkoutServlet;
    private HttpServletRequest req;
    private HttpServletResponse res;
    private HttpSession session;
    private RequestDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        checkoutServlet = new CheckoutServlet();
        req = mock(HttpServletRequest.class);
        res = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        dispatcher = mock(RequestDispatcher.class);

        when(req.getSession()).thenReturn(session);
        when(req.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    void testCheckout_whenUserNotLoggedIn() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, session)).thenReturn(false);

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            checkoutServlet.doPost(req, res);

            verify(dispatcher, times(1)).include(req, res);
            pw.flush();
            String output = sw.toString();
            assertTrue(output.contains("Please Login First to Continue!!"));
        }
    }

    @Test
    void testCheckout_whenUserLoggedIn() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, session)).thenReturn(true);
            storeUtilMock.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("cart"))).thenAnswer(invocation -> null);

            when(session.getAttribute("amountToPay")).thenReturn(500.0);

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            checkoutServlet.doPost(req, res);

            verify(dispatcher, times(1)).include(req, res);
            pw.flush();
            String output = sw.toString();
            assertTrue(output.contains("Total Amount"));
            assertTrue(output.contains("500.0")); // checking amount displayed
            assertTrue(output.contains("Pay & Place Order"));
        }
    }

    @Test
    void testCheckout_whenExceptionThrown() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, session)).thenReturn(true);
            storeUtilMock.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("cart"))).thenThrow(new RuntimeException("Forced Exception"));

            when(session.getAttribute("amountToPay")).thenReturn(500.0);

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            checkoutServlet.doPost(req, res);

            // No assertion needed, just testing that exception is caught without crashing
        }
    }
}