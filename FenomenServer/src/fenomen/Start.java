package fenomen;


import java.io.IOException;

import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import database.StaticConnector;

/**
 * Servlet implementation class Start
 */
public class Start extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Start() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doProcess(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doProcess(request, response);
	}

	private void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// старт программы 
		StaticConnector.getConnectWrap();
		Writer writer=response.getWriter();
		writer.write("<html>");
		writer.write("<head>");
		writer.write("<title> start page </title>");
		writer.write("</head>");
		writer.write("<body>");
		writer.write("<h1> Fenomen Server started... </h1>");
		writer.write("<b> <a href=\"fenomen_monitor.jnlp\">start monitor </a></b>");
		writer.write("</body>");
		writer.write("</html>");
		response.flushBuffer();
	}
}
