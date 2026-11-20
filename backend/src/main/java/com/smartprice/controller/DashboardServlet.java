
package com.smartprice.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.html");
        } else {
            try {
                request.getRequestDispatcher("/dashboard.html")
                       .forward(request, response);
            } catch (Exception e) {
                response.getWriter().println("Unable to open dashboard.");
                e.printStackTrace();
            }
        }
    }
}
