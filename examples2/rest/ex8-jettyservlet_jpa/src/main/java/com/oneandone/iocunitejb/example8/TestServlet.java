package com.oneandone.iocunitejb.example8;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author aschoerk
 */
@WebServlet(urlPatterns = "/testservlet")
public class TestServlet extends HttpServlet {

    private static final long serialVersionUID = 4589439242489119848L;

    @EJB
    ServiceIntf service;

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }


    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
