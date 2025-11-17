package com.example.lab5.manual.service;

import com.example.lab5.manual.dao.FunctionDAO;
import com.example.lab5.manual.dao.PointDAO;
import com.example.lab5.manual.dao.UserDAO;
import com.example.lab5.manual.dto.FunctionDTO;
import com.example.lab5.manual.dto.PointDTO;
import com.example.lab5.manual.dto.UserDTO;
import com.example.lab5.manual.search.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ManualSortingPerformanceService {
    private static final Logger logger = LoggerFactory.getLogger(ManualSortingPerformanceService.class);

    private final SearchService searchService;

    public ManualSortingPerformanceService() {
        UserDAO userDAO = new UserDAO();
        FunctionDAO functionDAO = new FunctionDAO();
        PointDAO pointDAO = new PointDAO();
        this.searchService = new SearchService(userDAO, functionDAO, pointDAO);
    }

    public SortingPerformanceResult measureSortingPerformance(int dataSize) {
        logger.info("Measuring sorting performance for data size: {}", dataSize);

        SortingPerformanceResult result = new SortingPerformanceResult(dataSize);

        // Генерация тестовых данных
        List<UserDTO> testUsers = generateTestUsers(dataSize);
        List<FunctionDTO> testFunctions = generateTestFunctions(dataSize);
        List<PointDTO> testPoints = generateTestPoints(dataSize);

        // Измерение производительности сортировки пользователей
        measureUserSortingPerformance(testUsers, result);

        // Измерение производительности сортировки функций
        measureFunctionSortingPerformance(testFunctions, result);

        // Измерение производительности сортировки точек
        measurePointSortingPerformance(testPoints, result);

        logger.info("Sorting performance measurement completed for data size: {}", dataSize);
        return result;
    }

    private void measureUserSortingPerformance(List<UserDTO> users, SortingPerformanceResult result) {
        logger.debug("Measuring user sorting performance for {} users", users.size());

        // Сортировка по логину (asc)
        long startTime = System.nanoTime();
        List<UserDTO> sortedByLogin = searchService.sortUsers(new ArrayList<>(users), "login", "asc");
        long endTime = System.nanoTime();
        result.setUserSortLoginAsc((endTime - startTime) / 1_000_000.0);

        // Сортировка по роли (asc)
        startTime = System.nanoTime();
        List<UserDTO> sortedByRole = searchService.sortUsers(new ArrayList<>(users), "role", "asc");
        endTime = System.nanoTime();
        result.setUserSortRoleAsc((endTime - startTime) / 1_000_000.0);

        // Сортировка по логину (desc)
        startTime = System.nanoTime();
        List<UserDTO> sortedByLoginDesc = searchService.sortUsers(new ArrayList<>(users), "login", "desc");
        endTime = System.nanoTime();
        result.setUserSortLoginDesc((endTime - startTime) / 1_000_000.0);

        // Множественная сортировка
        Map<String, String> multiSort = new LinkedHashMap<>();
        multiSort.put("role", "asc");
        multiSort.put("login", "asc");

        startTime = System.nanoTime();
        List<UserDTO> multiSorted = searchService.sortUsersByMultipleFields(new ArrayList<>(users), multiSort);
        endTime = System.nanoTime();
        result.setUserSortMultiField((endTime - startTime) / 1_000_000.0);

        logger.info("User sorting completed - LoginAsc: {}ms, RoleAsc: {}ms, LoginDesc: {}ms, Multi: {}ms",
                result.getUserSortLoginAsc(), result.getUserSortRoleAsc(),
                result.getUserSortLoginDesc(), result.getUserSortMultiField());
    }

    private void measureFunctionSortingPerformance(List<FunctionDTO> functions, SortingPerformanceResult result) {
        logger.debug("Measuring function sorting performance for {} functions", functions.size());

        // Сортировка по имени (asc)
        long startTime = System.nanoTime();
        List<FunctionDTO> sortedByName = searchService.sortFunctions(new ArrayList<>(functions), "name", "asc");
        long endTime = System.nanoTime();
        result.setFunctionSortNameAsc((endTime - startTime) / 1_000_000.0);

        // Сортировка по user ID (asc)
        startTime = System.nanoTime();
        List<FunctionDTO> sortedByUserId = searchService.sortFunctions(new ArrayList<>(functions), "userid", "asc");
        endTime = System.nanoTime();
        result.setFunctionSortUserIdAsc((endTime - startTime) / 1_000_000.0);

        logger.info("Function sorting completed - NameAsc: {}ms, UserIdAsc: {}ms",
                result.getFunctionSortNameAsc(), result.getFunctionSortUserIdAsc());
    }

    private void measurePointSortingPerformance(List<PointDTO> points, SortingPerformanceResult result) {
        logger.debug("Measuring point sorting performance for {} points", points.size());

        // Сортировка по X (asc)
        long startTime = System.nanoTime();
        List<PointDTO> sortedByX = searchService.sortPoints(new ArrayList<>(points), "x", "asc");
        long endTime = System.nanoTime();
        result.setPointSortXAsc((endTime - startTime) / 1_000_000.0);

        // Сортировка по Y (desc)
        startTime = System.nanoTime();
        List<PointDTO> sortedByY = searchService.sortPoints(new ArrayList<>(points), "y", "desc");
        endTime = System.nanoTime();
        result.setPointSortYDesc((endTime - startTime) / 1_000_000.0);

        logger.info("Point sorting completed - XAsc: {}ms, YDesc: {}ms",
                result.getPointSortXAsc(), result.getPointSortYDesc());
    }

    private List<UserDTO> generateTestUsers(int size) {
        List<UserDTO> users = new ArrayList<>();
        Random random = new Random();
        String[] roles = {"USER", "ADMIN", "MODERATOR", "GUEST"};

        for (int i = 0; i < size; i++) {
            String login = "user_" + (char)('a' + random.nextInt(26)) + "_" + random.nextInt(10000);
            String role = roles[random.nextInt(roles.length)];
            users.add(new UserDTO(login, role, "pass" + i));
        }

        return users;
    }

    private List<FunctionDTO> generateTestFunctions(int size) {
        List<FunctionDTO> functions = new ArrayList<>();
        Random random = new Random();
        String[] names = {"linear", "quadratic", "cubic", "sine", "cosine", "exponential", "logarithmic"};
        String[] signatures = {"f(x) = x", "f(x) = x^2", "f(x) = x^3", "f(x) = sin(x)", "f(x) = cos(x)", "f(x) = e^x", "f(x) = ln(x)"};

        for (int i = 0; i < size; i++) {
            String name = names[random.nextInt(names.length)] + "_" + random.nextInt(1000);
            String signature = signatures[random.nextInt(signatures.length)];
            Long userId = (long) (random.nextInt(10) + 1);
            functions.add(new FunctionDTO(userId, name, signature));
        }

        return functions;
    }

    private List<PointDTO> generateTestPoints(int size) {
        List<PointDTO> points = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < size; i++) {
            Double xValue = random.nextDouble() * 100;
            Double yValue = random.nextDouble() * 1000;
            Long functionId = (long) (random.nextInt(5) + 1);
            points.add(new PointDTO(functionId, xValue, yValue));
        }

        return points;
    }

    public static class SortingPerformanceResult {
        private final int dataSize;
        private double userSortLoginAsc;
        private double userSortRoleAsc;
        private double userSortLoginDesc;
        private double userSortMultiField;
        private double functionSortNameAsc;
        private double functionSortUserIdAsc;
        private double pointSortXAsc;
        private double pointSortYDesc;

        public SortingPerformanceResult(int dataSize) {
            this.dataSize = dataSize;
        }

        // Геттеры и сеттеры
        public int getDataSize() { return dataSize; }
        public double getUserSortLoginAsc() { return userSortLoginAsc; }
        public void setUserSortLoginAsc(double userSortLoginAsc) { this.userSortLoginAsc = userSortLoginAsc; }
        public double getUserSortRoleAsc() { return userSortRoleAsc; }
        public void setUserSortRoleAsc(double userSortRoleAsc) { this.userSortRoleAsc = userSortRoleAsc; }
        public double getUserSortLoginDesc() { return userSortLoginDesc; }
        public void setUserSortLoginDesc(double userSortLoginDesc) { this.userSortLoginDesc = userSortLoginDesc; }
        public double getUserSortMultiField() { return userSortMultiField; }
        public void setUserSortMultiField(double userSortMultiField) { this.userSortMultiField = userSortMultiField; }
        public double getFunctionSortNameAsc() { return functionSortNameAsc; }
        public void setFunctionSortNameAsc(double functionSortNameAsc) { this.functionSortNameAsc = functionSortNameAsc; }
        public double getFunctionSortUserIdAsc() { return functionSortUserIdAsc; }
        public void setFunctionSortUserIdAsc(double functionSortUserIdAsc) { this.functionSortUserIdAsc = functionSortUserIdAsc; }
        public double getPointSortXAsc() { return pointSortXAsc; }
        public void setPointSortXAsc(double pointSortXAsc) { this.pointSortXAsc = pointSortXAsc; }
        public double getPointSortYDesc() { return pointSortYDesc; }
        public void setPointSortYDesc(double pointSortYDesc) { this.pointSortYDesc = pointSortYDesc; }

        public String toMarkdownTable() {
            return String.format(
                    "| Data Size | Operation | Field | Direction | Time (ms) |\n" +
                            "|-----------|-----------|-------|-----------|-----------|\n" +
                            "| %d | User Sorting | login | ASC | %.3f |\n" +
                            "| %d | User Sorting | role | ASC | %.3f |\n" +
                            "| %d | User Sorting | login | DESC | %.3f |\n" +
                            "| %d | User Sorting | role+login | MULTI | %.3f |\n" +
                            "| %d | Function Sorting | name | ASC | %.3f |\n" +
                            "| %d | Function Sorting | user_id | ASC | %.3f |\n" +
                            "| %d | Point Sorting | x_value | ASC | %.3f |\n" +
                            "| %d | Point Sorting | y_value | DESC | %.3f |\n",
                    dataSize, userSortLoginAsc,
                    dataSize, userSortRoleAsc,
                    dataSize, userSortLoginDesc,
                    dataSize, userSortMultiField,
                    dataSize, functionSortNameAsc,
                    dataSize, functionSortUserIdAsc,
                    dataSize, pointSortXAsc,
                    dataSize, pointSortYDesc
            );
        }

        public String toCSV() {
            return String.format(
                    "DataSize,Operation,Field,Direction,TimeMs\n" +
                            "%d,UserSorting,login,ASC,%.3f\n" +
                            "%d,UserSorting,role,ASC,%.3f\n" +
                            "%d,UserSorting,login,DESC,%.3f\n" +
                            "%d,UserSorting,role_login,MULTI,%.3f\n" +
                            "%d,FunctionSorting,name,ASC,%.3f\n" +
                            "%d,FunctionSorting,user_id,ASC,%.3f\n" +
                            "%d,PointSorting,x_value,ASC,%.3f\n" +
                            "%d,PointSorting,y_value,DESC,%.3f\n",
                    dataSize, userSortLoginAsc,
                    dataSize, userSortRoleAsc,
                    dataSize, userSortLoginDesc,
                    dataSize, userSortMultiField,
                    dataSize, functionSortNameAsc,
                    dataSize, functionSortUserIdAsc,
                    dataSize, pointSortXAsc,
                    dataSize, pointSortYDesc
            );
        }
    }
}