package com.example.lab5.manual.search;

import com.example.lab5.manual.dao.FunctionDAO;
import com.example.lab5.manual.dao.PointDAO;
import com.example.lab5.manual.dao.UserDAO;
import com.example.lab5.manual.dto.FunctionDTO;
import com.example.lab5.manual.dto.PointDTO;
import com.example.lab5.manual.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ManualSortingTest {
    private static final Logger logger = LoggerFactory.getLogger(ManualSortingTest.class);
    private SearchService searchService;

    @BeforeEach
    void setUp() {
        UserDAO userDAO = new UserDAO();
        FunctionDAO functionDAO = new FunctionDAO();
        PointDAO pointDAO = new PointDAO();
        searchService = new SearchService(userDAO, functionDAO, pointDAO);
    }

    @Test
    void testUserSortingAscending() {
        logger.info("Testing user sorting in ascending order");

        List<UserDTO> users = createTestUsers();

        // Сортировка по логину (asc)
        List<UserDTO> sortedByLogin = searchService.sortUsers(new ArrayList<>(users), "login", "asc");
        assertEquals("admin_user", sortedByLogin.get(0).getLogin());
        assertEquals("zeta_user", sortedByLogin.get(4).getLogin());

        // Сортировка по роли (asc)
        List<UserDTO> sortedByRole = searchService.sortUsers(new ArrayList<>(users), "role", "asc");
        assertEquals("ADMIN", sortedByRole.get(0).getRole());
        assertEquals("USER", sortedByRole.get(4).getRole());

        logger.info("User ascending sorting tests passed");
    }

    @Test
    void testUserSortingDescending() {
        logger.info("Testing user sorting in descending order");

        List<UserDTO> users = createTestUsers();

        // Сортировка по логину (desc)
        List<UserDTO> sortedByLoginDesc = searchService.sortUsers(new ArrayList<>(users), "login", "desc");
        assertEquals("zeta_user", sortedByLoginDesc.get(0).getLogin());
        assertEquals("admin_user", sortedByLoginDesc.get(4).getLogin());

        // Сортировка по роли (desc)
        List<UserDTO> sortedByRoleDesc = searchService.sortUsers(new ArrayList<>(users), "role", "desc");
        assertEquals("USER", sortedByRoleDesc.get(0).getRole());
        assertEquals("ADMIN", sortedByRoleDesc.get(4).getRole());

        logger.info("User descending sorting tests passed");
    }

    @Test
    void testFunctionSorting() {
        logger.info("Testing function sorting");

        List<FunctionDTO> functions = createTestFunctions();

        // Сортировка по имени (asc)
        List<FunctionDTO> sortedByName = searchService.sortFunctions(new ArrayList<>(functions), "name", "asc");
        assertEquals("cubic", sortedByName.get(0).getName());
        assertEquals("sine", sortedByName.get(4).getName());

        // Сортировка по user ID (desc)
        List<FunctionDTO> sortedByUserId = searchService.sortFunctions(new ArrayList<>(functions), "userid", "desc");
        assertEquals(3L, sortedByUserId.get(0).getUserId());
        assertEquals(1L, sortedByUserId.get(4).getUserId());

        logger.info("Function sorting tests passed");
    }

    @Test
    void testPointSorting() {
        logger.info("Testing point sorting");

        List<PointDTO> points = createTestPoints();

        // Сортировка по X (asc)
        List<PointDTO> sortedByX = searchService.sortPoints(new ArrayList<>(points), "x", "asc");
        assertEquals(1.0, sortedByX.get(0).getXValue());
        assertEquals(10.0, sortedByX.get(4).getXValue());

        // Сортировка по Y (desc)
        List<PointDTO> sortedByY = searchService.sortPoints(new ArrayList<>(points), "y", "desc");
        assertEquals(100.0, sortedByY.get(0).getYValue());
        assertEquals(1.0, sortedByY.get(4).getYValue());

        logger.info("Point sorting tests passed");
    }

    @Test
    void testMultiFieldSorting() {
        logger.info("Testing multi-field sorting");

        List<UserDTO> users = Arrays.asList(
                new UserDTO("user_b", "USER", "pass1"),
                new UserDTO("user_a", "ADMIN", "pass2"),
                new UserDTO("user_c", "USER", "pass3"),
                new UserDTO("user_d", "ADMIN", "pass4"),
                new UserDTO("user_e", "MODERATOR", "pass5")
        );

        for (int i = 0; i < users.size(); i++) {
            users.get(i).setId((long) (i + 1));
        }

        // Множественная сортировка: сначала по роли, потом по логину
        Map<String, String> sortCriteria = new LinkedHashMap<>();
        sortCriteria.put("role", "asc");
        sortCriteria.put("login", "asc");

        List<UserDTO> multiSorted = searchService.sortUsersByMultipleFields(new ArrayList<>(users), sortCriteria);

        assertEquals("ADMIN", multiSorted.get(0).getRole());
        assertEquals("user_a", multiSorted.get(0).getLogin());
        assertEquals("ADMIN", multiSorted.get(1).getRole());
        assertEquals("user_d", multiSorted.get(1).getLogin());
        assertEquals("MODERATOR", multiSorted.get(2).getRole());
        assertEquals("USER", multiSorted.get(3).getRole());

        logger.info("Multi-field sorting tests passed");
    }

    @Test
    void testSortingPerformance() {
        logger.info("Testing sorting performance with large dataset");

        int dataSize = 1000;
        List<UserDTO> largeUserList = createLargeUserDataset(dataSize);

        long startTime = System.nanoTime();
        List<UserDTO> sortedUsers = searchService.sortUsers(largeUserList, "login", "asc");
        long endTime = System.nanoTime();

        double durationMs = (endTime - startTime) / 1_000_000.0;

        assertNotNull(sortedUsers);
        assertEquals(dataSize, sortedUsers.size());
        logger.info("Sorted {} users in {} ms", dataSize, durationMs);

        // Проверяем что сортировка корректна
        for (int i = 0; i < sortedUsers.size() - 1; i++) {
            assertTrue(sortedUsers.get(i).getLogin().compareTo(sortedUsers.get(i + 1).getLogin()) <= 0);
        }
    }

    private List<UserDTO> createTestUsers() {
        return Arrays.asList(
                new UserDTO("zeta_user", "USER", "pass1"),
                new UserDTO("beta_user", "MODERATOR", "pass2"),
                new UserDTO("alpha_user", "ADMIN", "pass3"),
                new UserDTO("gamma_user", "USER", "pass4"),
                new UserDTO("admin_user", "ADMIN", "pass5")
        );
    }

    private List<FunctionDTO> createTestFunctions() {
        return Arrays.asList(
                new FunctionDTO(2L, "sine", "f(x) = sin(x)"),
                new FunctionDTO(1L, "quadratic", "f(x) = x^2"),
                new FunctionDTO(3L, "exponential", "f(x) = e^x"),
                new FunctionDTO(1L, "cubic", "f(x) = x^3"),
                new FunctionDTO(2L, "linear", "f(x) = x")
        );
    }

    private List<PointDTO> createTestPoints() {
        return Arrays.asList(
                new PointDTO(1L, 5.0, 25.0),
                new PointDTO(1L, 1.0, 1.0),
                new PointDTO(2L, 10.0, 100.0),
                new PointDTO(1L, 2.0, 4.0),
                new PointDTO(2L, 3.0, 9.0)
        );
    }

    private List<UserDTO> createLargeUserDataset(int size) {
        List<UserDTO> users = new ArrayList<>();
        Random random = new Random();
        String[] roles = {"USER", "ADMIN", "MODERATOR"};

        for (int i = 0; i < size; i++) {
            String login = "user_" + (size - i) + "_" + random.nextInt(1000);
            String role = roles[random.nextInt(roles.length)];
            users.add(new UserDTO(login, role, "password" + i));
        }

        return users;
    }
}