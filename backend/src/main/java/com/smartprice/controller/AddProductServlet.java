
package com.smartprice.controller;

import com.smartprice.util.DBConnection;
import com.smartprice.service.MyntraPriceService;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.CallableStatement;
import java.sql.SQLException;

@WebServlet("/add-product")
public class AddProductServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.html");
            return;
        }

        int userId = (Integer) session.getAttribute("userId");

        String productUrl = request.getParameter("productUrl");
        String targetPrice = request.getParameter("targetPrice");

        try {
            // Fetch product details from Myntra
            JsonNode product = MyntraPriceService.getProductDetails(productUrl);

            String productName = product.path("name").asText();
            JsonNode priceNode = product.path("price").path("discounted");

            if (productName.isEmpty() || priceNode.isMissingNode()
                    || priceNode.isNull()) {
                throw new Exception("Product name or price not found.");
            }

            BigDecimal currentPrice = priceNode.decimalValue();
            BigDecimal target = new BigDecimal(targetPrice);

            String sql = "INSERT INTO tracked_products " +
                    "(user_id, product_url, product_name, target_price) " +
                    "VALUES (?, ?, ?, ?)";

            try (Connection con = DBConnection.getConnection()) {

                con.setAutoCommit(false);

                try {
                    int productId;

                    try (PreparedStatement ps = con.prepareStatement(
                            sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

                        ps.setInt(1, userId);
                        ps.setString(2, productUrl);
                        ps.setString(3, productName);
                        ps.setBigDecimal(4, target);

                        ps.executeUpdate();

                        try (ResultSet rs = ps.getGeneratedKeys()) {
                            rs.next();
                            productId = rs.getInt(1);
                        }
                    }

                    // Save current price and initial price history
                    try (CallableStatement cs =
                            con.prepareCall("{CALL UpdateProductPrice(?, ?)}")) {

                        cs.setInt(1, productId);
                        cs.setBigDecimal(2, currentPrice);
                        cs.execute();
                    }

                    con.commit();

                } catch (Exception e) {
                    con.rollback();
                    throw e;
                }
            }

            response.sendRedirect("dashboard?added=true");

        } catch (Exception e) {
            response.setContentType("text/plain");
            response.getWriter().println(
                    "Unable to add product: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
