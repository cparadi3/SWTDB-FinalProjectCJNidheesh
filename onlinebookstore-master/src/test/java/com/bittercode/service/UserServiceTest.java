package com.bittercode.service;

import com.bittercode.model.User;
import com.bittercode.model.UserRole;
import com.bittercode.service.impl.UserServiceImpl;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpSession;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import java.sql.PreparedStatement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mockito.MockedStatic;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;


public class UserServiceTest {

    private UserService userService;
    private HttpSession session;

    @Before
    public void setUp() {
        userService = new UserServiceImpl();
        session = mock(HttpSession.class);
    }

    @Test
    public void testRegister() throws Exception {
        User user = new User();
        user.setEmailId("testuser@example.com");
        user.setPassword("password123");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setAddress("Test Address");
        user.setPhone(1234567890L);

        String result = userService.register(UserRole.CUSTOMER, user);
        assertNotNull(result);
    }

    @Test
    public void testLogin() throws Exception {
        userService.login(UserRole.CUSTOMER, "someemail@example.com", "password", session);
    }

    @Test
    public void testIsLoggedInWhenUserIsNull() {
        when(session.getAttribute("user")).thenReturn(null);

        boolean isLoggedIn = userService.isLoggedIn(UserRole.CUSTOMER, session);
        assertFalse(isLoggedIn);
    }

    @Test
    public void testIsLoggedInWhenUserIsPresent() {
        User user = new User();
        user.setEmailId("mockuser@example.com");
        user.setPassword("mockpass");
        user.setFirstName("Mock");
        user.setLastName("User");
        user.setAddress("Mock Address");
        user.setPhone(9876543210L);

        when(session.getAttribute("user")).thenReturn(user);

        userService.isLoggedIn(UserRole.CUSTOMER, session);
    }

    @Test
    public void testLogout() {
        boolean result = userService.logout(session);
        assertTrue(result);
    }

    @Test
    public void testRegisterDuplicateUser() throws Exception {
        User user = new User();
        user.setEmailId("duplicate@example.com");
        user.setPassword("dup123");
        user.setFirstName("Dup");
        user.setLastName("User");
        user.setAddress("Dup Address");
        user.setPhone(9999999999L);

        String firstRegister = userService.register(UserRole.CUSTOMER, user);
        assertNotNull(firstRegister);

        String secondRegister = userService.register(UserRole.CUSTOMER, user);
        assertTrue(secondRegister.toLowerCase().contains("already") || secondRegister.toLowerCase().contains("duplicate"));
    }

    @Test
    public void testRegisterSQLExceptionHandling() throws Exception {
        Connection mockConnection = mock(Connection.class);
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Forced SQLException"));

        MockedStatic<com.bittercode.util.DBUtil> mockedStatic = mockStatic(com.bittercode.util.DBUtil.class);
        mockedStatic.when(com.bittercode.util.DBUtil::getConnection).thenReturn(mockConnection);

        UserService userService = new UserServiceImpl();
        User user = new User();
        user.setEmailId("failuser@example.com");
        user.setPassword("failpass");
        user.setFirstName("Fail");
        user.setLastName("User");
        user.setAddress("Nowhere");
        user.setPhone(1234567890L);

        String result = userService.register(UserRole.CUSTOMER, user);
        assertTrue(result.contains("FAILURE") || result.contains("User already registered"));

        mockedStatic.close();
    }

    @Test
    public void testLoginSQLExceptionHandling() throws Exception {
        Connection mockConnection = mock(Connection.class);
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Forced SQLException"));

        MockedStatic<com.bittercode.util.DBUtil> mockedStatic = mockStatic(com.bittercode.util.DBUtil.class);
        mockedStatic.when(com.bittercode.util.DBUtil::getConnection).thenReturn(mockConnection);

        UserService userService = new UserServiceImpl();
        User user = userService.login(UserRole.CUSTOMER, "anyemail@example.com", "anyPassword", session);
        assertNull(user);

        mockedStatic.close();
    }

    @Test
    public void testLoginFailureWrongCredentials() throws Exception {
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // Simulate no matching user

        MockedStatic<com.bittercode.util.DBUtil> mockedStatic = mockStatic(com.bittercode.util.DBUtil.class);
        mockedStatic.when(com.bittercode.util.DBUtil::getConnection).thenReturn(mockConnection);

        UserService userService = new UserServiceImpl();
        User user = userService.login(UserRole.CUSTOMER, "wrongemail@example.com", "wrongpass", session);
        assertNull(user); // Login should fail

        mockedStatic.close();
    }

    @Test
    public void testIsLoggedInWhenRoleIsNull() {
        when(session.getAttribute(UserRole.CUSTOMER.toString())).thenReturn("mockuser@example.com");

        boolean isLoggedIn = userService.isLoggedIn(null, session);
        assertTrue(isLoggedIn);
    }

    @Test
    public void testRegisterInsertFails() throws Exception {
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0); // Simulate insert fails

        MockedStatic<com.bittercode.util.DBUtil> mockedStatic = mockStatic(com.bittercode.util.DBUtil.class);
        mockedStatic.when(com.bittercode.util.DBUtil::getConnection).thenReturn(mockConnection);

        UserService userService = new UserServiceImpl();
        User user = new User();
        user.setEmailId("failinsert@example.com");
        user.setPassword("fail123");
        user.setFirstName("Fail");
        user.setLastName("Insert");
        user.setAddress("Fail Address");
        user.setPhone(9876543210L);

        String result = userService.register(UserRole.CUSTOMER, user);
        assertTrue(result.contains("FAILURE")); // Because insert failed

        mockedStatic.close();
    }

    @Test
    public void testLoginSuccess() throws Exception {
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true); // Simulate user found
        when(mockResultSet.getString("firstName")).thenReturn("John");
        when(mockResultSet.getString("lastName")).thenReturn("Doe");
        when(mockResultSet.getLong("phone")).thenReturn(1234567890L);

        MockedStatic<com.bittercode.util.DBUtil> mockedStatic = mockStatic(com.bittercode.util.DBUtil.class);
        mockedStatic.when(com.bittercode.util.DBUtil::getConnection).thenReturn(mockConnection);

        UserService userService = new UserServiceImpl();
        User user = userService.login(UserRole.CUSTOMER, "johndoe@example.com", "password", session);

        assertNotNull(user);
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals(Long.valueOf(1234567890L), Long.valueOf(user.getPhone()));
        assertEquals("johndoe@example.com", user.getEmailId());
        assertEquals("password", user.getPassword());

        mockedStatic.close();
    }

    @Test
    public void testRegisterDuplicateSQLExceptionHandling() throws Exception {
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenThrow(new SQLException("Duplicate entry error"));

        MockedStatic<com.bittercode.util.DBUtil> mockedStatic = mockStatic(com.bittercode.util.DBUtil.class);
        mockedStatic.when(com.bittercode.util.DBUtil::getConnection).thenReturn(mockConnection);

        UserService userService = new UserServiceImpl();
        User user = new User();
        user.setEmailId("duplicateuser@example.com");
        user.setPassword("duplicatepass");
        user.setFirstName("Duplicate");
        user.setLastName("User");
        user.setAddress("Duplicate Address");
        user.setPhone(9876543211L);

        String result = userService.register(UserRole.CUSTOMER, user);

        assertEquals("User already registered with this email !!", result); // Should hit the Duplicate message!

        mockedStatic.close();
    }

    @Test
    public void testRegisterSQLExceptionWithoutDuplicateHandling() throws Exception {
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenThrow(new SQLException("Some random SQL error"));

        MockedStatic<com.bittercode.util.DBUtil> mockedStatic = mockStatic(com.bittercode.util.DBUtil.class);
        mockedStatic.when(com.bittercode.util.DBUtil::getConnection).thenReturn(mockConnection);

        UserService userService = new UserServiceImpl();
        User user = new User();
        user.setEmailId("nonduplicate@example.com");
        user.setPassword("pass123");
        user.setFirstName("Non");
        user.setLastName("Duplicate");
        user.setAddress("Unknown");
        user.setPhone(1112223333L);

        String result = userService.register(UserRole.CUSTOMER, user);

        assertTrue(result.contains("FAILURE")); // Should remain FAILURE, not "User already registered"

        mockedStatic.close();
    }

    @Test
    public void testRegisterSuccess() throws Exception {
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1); // Simulate successful insert

        MockedStatic<com.bittercode.util.DBUtil> mockedStatic = mockStatic(com.bittercode.util.DBUtil.class);
        mockedStatic.when(com.bittercode.util.DBUtil::getConnection).thenReturn(mockConnection);

        UserService userService = new UserServiceImpl();
        User user = new User();
        user.setEmailId("successuser@example.com");
        user.setPassword("success123");
        user.setFirstName("Success");
        user.setLastName("User");
        user.setAddress("Success Address");
        user.setPhone(9876543210L);

        String result = userService.register(UserRole.CUSTOMER, user);

        assertEquals("SUCCESS", result); // Should return SUCCESS

        mockedStatic.close();
    }

    @Test
    public void testLoginAsSeller() throws Exception {
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true); // simulate user found
        when(mockResultSet.getString("firstName")).thenReturn("SellerFirst");
        when(mockResultSet.getString("lastName")).thenReturn("SellerLast");
        when(mockResultSet.getLong("phone")).thenReturn(9876543211L);

        MockedStatic<com.bittercode.util.DBUtil> mockedStatic = mockStatic(com.bittercode.util.DBUtil.class);
        mockedStatic.when(com.bittercode.util.DBUtil::getConnection).thenReturn(mockConnection);

        UserService userService = new UserServiceImpl();
        User user = userService.login(UserRole.SELLER, "seller@example.com", "sellerpass", session);

        assertNotNull(user);
        assertEquals("SellerFirst", user.getFirstName());
        assertEquals("SellerLast", user.getLastName());
        assertEquals(Long.valueOf(9876543211L), Long.valueOf(user.getPhone()));
        assertEquals("seller@example.com", user.getEmailId());
        assertEquals("sellerpass", user.getPassword());

        mockedStatic.close();
    }

    @Test
    public void testRegisterAsSeller() throws Exception {
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1); // simulate successful insert

        MockedStatic<com.bittercode.util.DBUtil> mockedStatic = mockStatic(com.bittercode.util.DBUtil.class);
        mockedStatic.when(com.bittercode.util.DBUtil::getConnection).thenReturn(mockConnection);

        UserService userService = new UserServiceImpl();
        User user = new User();
        user.setEmailId("sellersignup@example.com");
        user.setPassword("sellersignuppass");
        user.setFirstName("Seller");
        user.setLastName("Signup");
        user.setAddress("Seller Address");
        user.setPhone(9876543211L);

        String result = userService.register(UserRole.SELLER, user);

        assertEquals("SUCCESS", result);

        mockedStatic.close();
    }

}
