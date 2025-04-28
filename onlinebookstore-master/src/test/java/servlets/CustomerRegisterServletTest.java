package servlets;

import com.bittercode.constant.ResponseCode;
import com.bittercode.constant.db.UsersDBConstants;
import com.bittercode.model.UserRole;
import com.bittercode.service.UserService;
import com.bittercode.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class CustomerRegisterServletTest {

    private CustomerRegisterServlet customerRegisterServlet;
    private HttpServletRequest req;
    private HttpServletResponse res;
    private RequestDispatcher dispatcher;
    private UserService userService;

    @BeforeEach
    void setUp() {
        customerRegisterServlet = new CustomerRegisterServlet();
        req = mock(HttpServletRequest.class);
        res = mock(HttpServletResponse.class);
        dispatcher = mock(RequestDispatcher.class);
        userService = mock(UserServiceImpl.class);

        customerRegisterServlet.userService = userService; // inject mock

        when(req.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    private void mockRequestParameters() {
        when(req.getParameter(UsersDBConstants.COLUMN_PASSWORD)).thenReturn("password123");
        when(req.getParameter(UsersDBConstants.COLUMN_FIRSTNAME)).thenReturn("John");
        when(req.getParameter(UsersDBConstants.COLUMN_LASTNAME)).thenReturn("Doe");
        when(req.getParameter(UsersDBConstants.COLUMN_ADDRESS)).thenReturn("123 Main St");
        when(req.getParameter(UsersDBConstants.COLUMN_PHONE)).thenReturn("1234567890");
        when(req.getParameter(UsersDBConstants.COLUMN_MAILID)).thenReturn("john.doe@example.com");
    }

    @Test
    void testCustomerRegisterSuccess() throws Exception {
        mockRequestParameters();
        when(userService.register(eq(UserRole.CUSTOMER), any())).thenReturn("SUCCESS");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(res.getWriter()).thenReturn(pw);

        customerRegisterServlet.service(req, res);

        verify(dispatcher, times(1)).include(req, res);
        pw.flush();
        String output = sw.toString();
        assertTrue(output.contains("User Registered Successfully"));
    }

    @Test
    void testCustomerRegisterFailure() throws Exception {
        mockRequestParameters();
        when(userService.register(eq(UserRole.CUSTOMER), any())).thenReturn("FAILURE");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(res.getWriter()).thenReturn(pw);

        customerRegisterServlet.service(req, res);

        verify(dispatcher, times(1)).include(req, res);
        pw.flush();
        String output = sw.toString();
        assertTrue(output.contains("FAILURE"));
        assertTrue(output.contains("Sorry for interruption! Try again"));
    }

    @Test
    void testCustomerRegisterExceptionThrown() throws Exception {
        mockRequestParameters();
        when(userService.register(eq(UserRole.CUSTOMER), any())).thenThrow(new RuntimeException("Forced Exception"));

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(res.getWriter()).thenReturn(pw);

        customerRegisterServlet.service(req, res);

        // No assertion needed; just making sure servlet does not crash
    }
}
