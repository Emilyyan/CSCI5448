package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import beans.User;
import database.Account;


/**
 * Servlet implementation class LoginController
 */
//@WebServlet("/LoginController")
public class LoginController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private DataSource ds;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoginController() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		try {
			InitialContext initContext = new InitialContext();

			Context env = (Context) initContext.lookup("java:comp/env");

			ds = (DataSource) env.lookup("jdbc/CraigsList");

		} catch (NamingException e) {
			throw new ServletException();
		}
	}
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		PrintWriter out = response.getWriter();
		request.getRequestDispatcher("/signup.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		PrintWriter out = response.getWriter();
		
		Connection conn = null;

		try {
			conn = ds.getConnection();
			out.println("Connected to database.");
		} catch (SQLException e) {
			out.println("Unable to connect to database.");
			System.out.println("Unable to connect to database.");
			return;
			//throw new ServletException();
		}

		Account account = new Account(conn);
		
		String email = request.getParameter("loginEmail");
		String password = request.getParameter("loginPassword");
		
		session.setAttribute("loginEmail", email);
		//session.setAttribute("loginPassword", password);
		
		request.setAttribute("loginEmail", email);
		request.setAttribute("loginPassword", password);
		
		User user = new User(email, password);
		
		try {
			if(account.login(email, password)) {
				request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
			}
			else {
				request.setAttribute("message", "email address or password not recognised");
				request.getRequestDispatcher("/signup.jsp").forward(request, response);
			}
		} catch (SQLException e) {

			request.setAttribute("email", "Database error: please try again later.");
			request.getRequestDispatcher("/signup.jsp").forward(request, response);
			
		}
	
	}
}
