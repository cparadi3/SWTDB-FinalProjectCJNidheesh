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

class AboutServletTest {

    private AboutServlet aboutServlet;
    private HttpServletRequest req;
    private HttpServletResponse res;
    private HttpSession session;
    private RequestDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        aboutServlet = new AboutServlet();
        req = mock(HttpServletRequest.class);
        res = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        dispatcher = mock(RequestDispatcher.class);

        when(req.getSession()).thenReturn(session);
        when(req.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    void testAbout_whenCustomerLoggedIn() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, session)).thenReturn(true);
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.SELLER, session)).thenReturn(false);
            storeUtilMock.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("about"))).thenAnswer(invocation -> null);

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            aboutServlet.service(req, res);

            verify(dispatcher, times(1)).include(req, res);
            pw.flush();
            String output = sw.toString();
            assertTrue(output.contains("iframe"));
        }
    }

    @Test
    void testAbout_whenSellerLoggedIn() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, session)).thenReturn(false);
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.SELLER, session)).thenReturn(true);
            storeUtilMock.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("about"))).thenAnswer(invocation -> null);

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            aboutServlet.service(req, res);

            verify(dispatcher, times(1)).include(req, res);
            pw.flush();
            String output = sw.toString();
            assertTrue(output.contains("iframe"));
        }
    }

    @Test
    void testAbout_whenNotLoggedIn() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, session)).thenReturn(false);
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.SELLER, session)).thenReturn(false);

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            when(res.getWriter()).thenReturn(pw);

            aboutServlet.service(req, res);

            verify(dispatcher, times(1)).include(req, res);
            pw.flush();
            String output = sw.toString();
            assertTrue(output.contains("Please Login First to Continue!!"));
        }
    }
}