package servlets;

import com.bittercode.constant.db.UsersDBConstants;
import com.bittercode.model.User;
import com.bittercode.model.UserRole;
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

class CustomerLoginServletTest {

    private CustomerLoginServlet customerLoginServlet;
    private HttpServletRequest req;
    private HttpServletResponse res;
    private HttpSession session;
    private RequestDispatcher dispatcher;
    private UserService authService;

    @BeforeEach
    void setUp() {
        customerLoginServlet = new CustomerLoginServlet();
        req = mock(HttpServletRequest.class);
        res = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        dispatcher = mock(RequestDispatcher.class);
        authService = mock(UserServiceImpl.class);

        customerLoginServlet.authService = authService; // inject mock authService

        when(req.getSession()).thenReturn(session);
        when(req.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    void testCustomerLoginSuccess() throws Exception {
        when(req.getParameter(UsersDBConstants.COLUMN_USERNAME)).thenReturn("customer1");
        when(req.getParameter(UsersDBConstants.COLUMN_PASSWORD)).thenReturn("password1");

        User user = new User();
        user.setFirstName("Alice");

        when(authService.login(UserRole.CUSTOMER, "customer1", "password1", session)).thenReturn(user);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(res.getWriter()).thenReturn(pw);

        customerLoginServlet.doPost(req, res);

        verify(dispatcher, times(1)).include(req, res);
        pw.flush();
        String output = sw.toString();
        assertTrue(output.contains("Welcome Alice"));
    }

    @Test
    void testCustomerLoginFailure() throws Exception {
        when(req.getParameter(UsersDBConstants.COLUMN_USERNAME)).thenReturn("wrongcustomer");
        when(req.getParameter(UsersDBConstants.COLUMN_PASSWORD)).thenReturn("wrongpass");

        when(authService.login(UserRole.CUSTOMER, "wrongcustomer", "wrongpass", session)).thenReturn(null);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(res.getWriter()).thenReturn(pw);

        customerLoginServlet.doPost(req, res);

        verify(dispatcher, times(1)).include(req, res);
        pw.flush();
        String output = sw.toString();
        assertTrue(output.contains("Incorrect UserName or PassWord"));
    }

    @Test
    void testCustomerLoginException() throws Exception {
        when(req.getParameter(UsersDBConstants.COLUMN_USERNAME)).thenReturn("anyuser");
        when(req.getParameter(UsersDBConstants.COLUMN_PASSWORD)).thenReturn("anypass");

        User user = new User();
        user.setFirstName("TestUser");
        when(authService.login(UserRole.CUSTOMER, "anyuser", "anypass", session)).thenReturn(user);

        // Force exception when dispatcher.include is called
        doThrow(new RuntimeException("Forced Exception")).when(dispatcher).include(req, res);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(res.getWriter()).thenReturn(pw);

        customerLoginServlet.doPost(req, res);

        // No need for assertion. This just ensures catch block is executed.
    }
}