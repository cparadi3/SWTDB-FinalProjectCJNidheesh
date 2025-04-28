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

class SellerLoginServletTest {

    private SellerLoginServlet sellerLoginServlet;
    private HttpServletRequest req;
    private HttpServletResponse res;
    private HttpSession session;
    private RequestDispatcher dispatcher;
    private UserService userService;

    @BeforeEach
    void setUp() {
        sellerLoginServlet = new SellerLoginServlet();
        req = mock(HttpServletRequest.class);
        res = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        dispatcher = mock(RequestDispatcher.class);
        userService = mock(UserServiceImpl.class);

        sellerLoginServlet.userService = userService; // inject mock userService

        when(req.getSession()).thenReturn(session);
        when(req.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    void testSellerLoginSuccess() throws Exception {
        when(req.getParameter(UsersDBConstants.COLUMN_USERNAME)).thenReturn("seller1");
        when(req.getParameter(UsersDBConstants.COLUMN_PASSWORD)).thenReturn("password1");

        User user = new User();
        user.setFirstName("John");

        when(userService.login(UserRole.SELLER, "seller1", "password1", session)).thenReturn(user);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(res.getWriter()).thenReturn(pw);

        sellerLoginServlet.doPost(req, res);

        verify(dispatcher, times(1)).include(req, res);
        pw.flush();
        String output = sw.toString();
        assertTrue(output.contains("Welcome John"));
    }

    @Test
    void testSellerLoginFailure() throws Exception {
        when(req.getParameter(UsersDBConstants.COLUMN_USERNAME)).thenReturn("wronguser");
        when(req.getParameter(UsersDBConstants.COLUMN_PASSWORD)).thenReturn("wrongpass");

        when(userService.login(UserRole.SELLER, "wronguser", "wrongpass", session)).thenReturn(null);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(res.getWriter()).thenReturn(pw);

        sellerLoginServlet.doPost(req, res);

        verify(dispatcher, times(1)).include(req, res);
        pw.flush();
        String output = sw.toString();
        assertTrue(output.contains("Incorrect UserName or PassWord"));
    }

    @Test
    void testSellerLoginException() throws Exception {
        when(req.getParameter(UsersDBConstants.COLUMN_USERNAME)).thenReturn("anyuser");
        when(req.getParameter(UsersDBConstants.COLUMN_PASSWORD)).thenReturn("anypass");

        when(userService.login(UserRole.SELLER, "anyuser", "anypass", session))
                .thenThrow(new RuntimeException("Forced Exception"));

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(res.getWriter()).thenReturn(pw);

        sellerLoginServlet.doPost(req, res);

        // No assertion needed, just checking that servlet doesn't crash
    }
}