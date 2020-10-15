package controllers.employees;

import java.io.IOException;
import java.sql.Timestamp;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Employee;
import utils.DBUtil;

/**
 * Servlet implementation class EmployeesDestroyServlet
 */
@WebServlet("/employees/destroy")
public class EmployeesDestroyServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public EmployeesDestroyServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String _token = (String)request.getParameter("_token");
        if(_token != null && _token.equals(request.getSession().getId())) {     //今回のログインで生成された(Editサーブレットで呼び出した)getID(セッションID)が、このDestroyサーブレットで呼び出したgetIDと一致していることを確認
            EntityManager em = DBUtil.createEntityManager();                  //データベースと接続するおまじない。(データベースに「em」と名付けた。(=DBUtil(DAT)のcreateEntityManagerメソッドを呼び出し、「em」と名付けた))

            Employee e = em.find(Employee.class, (Integer)(request.getSession().getAttribute("employee_id")));    //index.jspの一覧→show.jspから、該当のidのEmployees型（Employee.class(DTO)で定義）のデータ(内容)を取得する。
            e.setDelete_flag(1);                                               //Delete_flag(1)として、データを消したことを残す。index.jspで、Delete_flag(1)=「削除済み」
            e.setUpdated_at(new Timestamp(System.currentTimeMillis()));      //削除日を取得

            em.getTransaction().begin();
  //【保存】em.remove(e); は不要。"Delete_frag"を使用すると論理削除が可能。=データがデータベースに残る。
            em.getTransaction().commit();
            em.close();
            request.getSession().setAttribute("flush", "削除が完了しました。");

            response.sendRedirect(request.getContextPath() + "/employees/index");
        }
    }

}