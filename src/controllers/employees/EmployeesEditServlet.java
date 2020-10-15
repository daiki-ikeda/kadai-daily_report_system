package controllers.employees;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Employee;
import utils.DBUtil;

/**
 * Servlet implementation class EmployeesEditServlet
 */
@WebServlet("/employees/edit")
public class EmployeesEditServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public EmployeesEditServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        EntityManager em = DBUtil.createEntityManager();      //データベースと接続するおまじない。(データベースに「em」と名付けた。(=DBUtil(DAT)のcreateEntityManagerメソッドを呼び出し、「em」と名付けた))

        Employee e = em.find(Employee.class, Integer.parseInt(request.getParameter("id")));    //index.jspの一覧→show.jspから、該当のidのEmployees型（Employee.class(DTO)で定義）のデータ(内容)を取得する。

        em.close();

        request.setAttribute("employee", e);                                //該当のidのEmployees型（Employee.class(DTO)で定義）のデータ(内容・レコード)に"employee"と名付けて、edit.jspに送る。
        request.setAttribute("_token", request.getSession().getId());       //今回のログインのセッションidに"_token"と名付ける。
        request.getSession().setAttribute("employee_id", e.getId());        //該当のidのEmployees型（Employee.class(DTO)で定義）のidに"employee_id"と名付けて、edit.jspに送る。

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/employees/edit.jsp");
        rd.forward(request, response);
    }

}