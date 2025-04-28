package servlets;

import com.bittercode.service.UserService;
import com.bittercode.service.impl.UserServiceImpl;
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

class LogoutServletTest {

    private LogoutServlet logoutServlet;
    private HttpServletRequest req;
    private HttpServletResponse res;
    private HttpSession session;
    private RequestDispatcher dispatcher;
    private UserService authService;

    @BeforeEach
    void setUp() {
        logoutServlet = new LogoutServlet();
        req = mock(HttpServletRequest.class);
        res = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        dispatcher = mock(RequestDispatcher.class);
        authService = mock(UserServiceImpl.class);

        logoutServlet.authService = authService; // inject mock authService

        when(req.getSession()).thenReturn(session);
        when(req.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    void testLogoutSuccess() throws Exception {
        when(authService.logout(session)).thenReturn(true);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(res.getWriter()).thenReturn(pw);

        logoutServlet.doGet(req, res);

        verify(dispatcher, times(1)).include(req, res);
        pw.flush();
        String output = sw.toString();
        assertTrue(output.contains("Successfully logged out!"));
    }

    @Test
    void testLogoutFailure() throws Exception {
        when(authService.logout(session)).thenReturn(false);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(res.getWriter()).thenReturn(pw);

        logoutServlet.doGet(req, res);

        verify(dispatcher, times(1)).include(req, res);
        pw.flush();
        String output = sw.toString();
        // In case of logout failure, no success message should be printed
        assertTrue(output.isEmpty() || !output.contains("Successfully logged out!"));
    }

    @Test
    void testLogoutException() throws Exception {
        when(authService.logout(session)).thenThrow(new RuntimeException("Forced Exception"));

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(res.getWriter()).thenReturn(pw);

        logoutServlet.doGet(req, res);

        // no assertion needed, just verifying exception doesn't crash servlet
    }
}
