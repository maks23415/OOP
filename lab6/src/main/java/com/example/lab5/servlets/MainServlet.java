package com.example.lab5.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/")
public class MainServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Lab5 Manual API</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; margin: 40px; }");
            out.println("h1 { color: #333; }");
            out.println("ul { list-style-type: none; padding: 0; }");
            out.println("li { margin: 10px 0; }");
            out.println("a { text-decoration: none; color: #007bff; }");
            out.println("a:hover { text-decoration: underline; }");
            out.println(".endpoint { background: #f5f5f5; padding: 10px; margin: 5px 0; border-radius: 5px; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>🚀 Lab5 Manual API - Tomcat Ready!</h1>");
            out.println("<p>API endpoints available:</p>");

            out.println("<h2>👥 Users API</h2>");
            out.println("<div class='endpoint'>GET <a href='/lab5/users'>/users</a> - Get all users</div>");
            out.println("<div class='endpoint'>GET /users/{id} - Get user by ID</div>");
            out.println("<div class='endpoint'>GET /users/login/{login} - Get user by login</div>");
            out.println("<div class='endpoint'>GET /users/role/{role} - Get users by role</div>");
            out.println("<div class='endpoint'>POST /users - Create user</div>");
            out.println("<div class='endpoint'>PUT /users/{id} - Update user</div>");
            out.println("<div class='endpoint'>DELETE /users/{id} - Delete user</div>");

            out.println("<h2>📈 Functions API</h2>");
            out.println("<div class='endpoint'>GET <a href='/lab5/functions'>/functions</a> - Get all functions</div>");
            out.println("<div class='endpoint'>GET /functions/{id} - Get function by ID</div>");
            out.println("<div class='endpoint'>GET /functions/user/{userId} - Get functions by user ID</div>");
            out.println("<div class='endpoint'>GET /functions/name/{name} - Get functions by name</div>");
            out.println("<div class='endpoint'>GET /functions/stats/{functionId} - Get function statistics</div>");
            out.println("<div class='endpoint'>POST /functions - Create function</div>");
            out.println("<div class='endpoint'>PUT /functions/{id} - Update function</div>");
            out.println("<div class='endpoint'>DELETE /functions/{id} - Delete function</div>");

            out.println("<h2>📐 Points API</h2>");
            out.println("<div class='endpoint'>GET <a href='/lab5/points'>/points</a> - Get all points</div>");
            out.println("<div class='endpoint'>GET /points/{id} - Get point by ID</div>");
            out.println("<div class='endpoint'>GET /points/function/{functionId} - Get points by function ID</div>");
            out.println("<div class='endpoint'>GET /points/max/{functionId} - Get point with max Y</div>");
            out.println("<div class='endpoint'>GET /points/min/{functionId} - Get point with min Y</div>");
            out.println("<div class='endpoint'>GET /points/stats/{functionId} - Get point statistics</div>");
            out.println("<div class='endpoint'>POST /points - Create point</div>");
            out.println("<div class='endpoint'>POST /points/generate/{functionId} - Generate points for function</div>");
            out.println("<div class='endpoint'>PUT /points/{id} - Update point</div>");
            out.println("<div class='endpoint'>DELETE /points/{id} - Delete point</div>");

            out.println("<p>Use tools like Postman to test POST, PUT, DELETE requests</p>");
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
    }
}