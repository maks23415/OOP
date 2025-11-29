package com.example.lab5.manual.dto;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AllDTOTest {

    @Test
    void testUserDTOConstructorAndGetters() {
        UserDTO user = new UserDTO("testuser", "ADMIN", "password123");

        assertNull(user.getId());
        assertEquals("testuser", user.getLogin());
        assertEquals("ADMIN", user.getRole());
        assertEquals("password123", user.getPassword());
    }

    @Test
    void testUserDTOSetters() {
        UserDTO user = new UserDTO();

        user.setId(1L);
        user.setLogin("newuser");
        user.setRole("USER");
        user.setPassword("newpass");

        assertEquals(1L, user.getId());
        assertEquals("newuser", user.getLogin());
        assertEquals("USER", user.getRole());
        assertEquals("newpass", user.getPassword());
    }

    @Test
    void testUserDTOToString() {
        UserDTO user = new UserDTO("testuser", "ADMIN", "password");
        user.setId(1L);

        String result = user.toString();

        assertTrue(result.contains("testuser"));
        assertTrue(result.contains("ADMIN"));
        assertTrue(result.contains("1"));
        assertFalse(result.contains("password")); // Password should not be in toString
    }

    @Test
    void testUserDTOEqualsAndHashCode() {
        UserDTO user1 = new UserDTO("user1", "ADMIN", "pass1");
        user1.setId(1L);

        UserDTO user2 = new UserDTO("user1", "ADMIN", "pass1");
        user2.setId(1L);

        UserDTO user3 = new UserDTO("user2", "USER", "pass2");
        user3.setId(2L);

        assertEquals(user1, user2);
        assertNotEquals(user1, user3);
        assertEquals(user1.hashCode(), user2.hashCode());
        assertNotEquals(user1.hashCode(), user3.hashCode());
    }
}

class FunctionDTOTest {

    @Test
    void testFunctionDTOConstructorAndGetters() {
        FunctionDTO function = new FunctionDTO(1L, "quadratic", "f(x) = x^2");

        assertNull(function.getId());
        assertEquals(1L, function.getUserId());
        assertEquals("quadratic", function.getName());
        assertEquals("f(x) = x^2", function.getSignature());
    }

    @Test
    void testFunctionDTOSetters() {
        FunctionDTO function = new FunctionDTO();

        function.setId(10L);
        function.setUserId(1L);
        function.setName("linear");
        function.setSignature("f(x) = x");

        assertEquals(10L, function.getId());
        assertEquals(1L, function.getUserId());
        assertEquals("linear", function.getName());
        assertEquals("f(x) = x", function.getSignature());
    }

    @Test
    void testFunctionDTOToString() {
        FunctionDTO function = new FunctionDTO(1L, "testfunc", "f(x) = x");
        function.setId(10L);

        String result = function.toString();

        assertTrue(result.contains("testfunc"));
        assertTrue(result.contains("10"));
        assertTrue(result.contains("1"));
        assertTrue(result.contains("f(x) = x"));
    }
}

class PointDTOTest {

    @Test
    void testPointDTOConstructorAndGetters() {
        PointDTO point = new PointDTO(1L, 2.5, 6.25);

        assertNull(point.getId());
        assertEquals(1L, point.getFunctionId());
        assertEquals(2.5, point.getXValue());
        assertEquals(6.25, point.getYValue());
    }

    @Test
    void testPointDTOSetters() {
        PointDTO point = new PointDTO();

        point.setId(100L);
        point.setFunctionId(10L);
        point.setXValue(3.0);
        point.setYValue(9.0);

        assertEquals(100L, point.getId());
        assertEquals(10L, point.getFunctionId());
        assertEquals(3.0, point.getXValue());
        assertEquals(9.0, point.getYValue());
    }

    @Test
    void testPointDTOToString() {
        PointDTO point = new PointDTO(1L, 2.5, 6.25);
        point.setId(100L);

        String result = point.toString();

        assertTrue(result.contains("100"));
        assertTrue(result.contains("1"));
        assertTrue(result.contains("2.5"));
        assertTrue(result.contains("6.25"));
    }
}
