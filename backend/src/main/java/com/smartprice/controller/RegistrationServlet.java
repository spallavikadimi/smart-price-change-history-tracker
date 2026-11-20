
package com.smartprice.controller;

import com.smartprice.util.DBConnection;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

@WebServlet("/register")
public class RegistrationServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws IOException {

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        response.setContentType("text/plain");

        if (name == null || email == null || password == null ||
                name.trim().isEmpty() ||
                email.trim().isEmpty() ||
                password.isEmpty()) {

            response.getWriter().println("Please fill all fields.");
            return;
        }

        String hashedPassword = BCrypt.hashpw(
                password, BCrypt.gensalt());

        try {

            Connection con = DBConnection.getConnection();

            String sql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, name.trim());
            ps.setString(2, email.trim());
            ps.setString(3, hashedPassword);

            ps.executeUpdate();

            response.getWriter().println("Registration successful!");

            ps.close();
            con.close();

        } catch (SQLException e) {

            if ("23000".equals(e.getSQLState())) {
                response.getWriter().println("Email already registered.");
            } else {
                response.getWriter().println("Registration failed.");
                e.printStackTrace();
            }
        }
    }
}
