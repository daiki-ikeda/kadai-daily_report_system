package controllers.employees;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Employee;

/**
 * Servlet implementation class EmployeesNewServlet
 */
@WebServlet("/employees/new")       //「新規作成」でアクセスされるページ(URL)
public class EmployeesNewServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public EmployeesNewServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("_token", request.getSession().getId());       //セキュリティーのおまじない。(自動生成したIdを"_token"に入れる。getID(セッションID)は、webアプリにアクセスしたときに自動で生成されるID。DBのIDとは異なる。)
        request.setAttribute("employee", new Employee());                   //おまじない。(Employee(従業員データベース)をインスタンス化する。)

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/employees/new.jsp");    //作った「_tolen」と「employee」をner.jspに送る
        rd.forward(request, response);
    }

}