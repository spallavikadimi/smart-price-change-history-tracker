
package com.smartprice.controller;

import com.smartprice.util.DBConnection;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/my-products")
public class ProductsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendError(401);
            return;
        }

        int userId = (Integer) session.getAttribute("userId");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        StringBuilder result = new StringBuilder("[");

        try (Connection con = DBConnection.getConnection();
             CallableStatement ps = con.prepareCall("{CALL GetUserProducts(?)}")) {

            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            boolean first = true;

            while (rs.next()) {

                if (!first) {
                    result.append(",");
                }

                result.append("{")
                    .append("\"productName\":\"")
                    .append(rs.getString("product_name"))
                    .append("\",")
                    .append("\"productUrl\":\"")
                    .append(rs.getString("product_url"))
                    .append("\",")
                    .append("\"currentPrice\":\"")
                    .append(rs.getString("current_price"))
                    .append("\",")
                    .append("\"targetPrice\":\"")
                    .append(rs.getString("target_price"))
                    .append("\"}");

                first = false;
            }

            rs.close();

            result.append("]");
            response.getWriter().println(result);

        } catch (SQLException e) {
            response.setStatus(500);
            response.getWriter().println("{\"error\":\"Unable to fetch products\"}");
            e.printStackTrace();
        }
    }
}
