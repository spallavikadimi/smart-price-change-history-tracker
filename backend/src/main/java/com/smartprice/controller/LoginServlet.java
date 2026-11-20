
package com.smartprice.controller;

import com.smartprice.util.DBConnection;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        response.setContentType("text/plain");

        try {

            Connection con = DBConnection.getConnection();

            String sql = "SELECT * FROM users WHERE email = ?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, email.trim());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                String storedPassword = rs.getString("password");

                if (BCrypt.checkpw(password, storedPassword)) {

                    HttpSession session = request.getSession();
                    session.setAttribute("userId", rs.getInt("user_id"));
                    session.setAttribute("userName", rs.getString("name"));

                    response.sendRedirect("dashboard");

                } else {

                    response.getWriter().println("Incorrect password.");

                }

            } else {

                response.getWriter().println("Email not registered.");

            }

            rs.close();
            ps.close();
            con.close();

        } catch (SQLException e) {

            response.getWriter().println("Login failed.");
            e.printStackTrace();

        }
    }
}
