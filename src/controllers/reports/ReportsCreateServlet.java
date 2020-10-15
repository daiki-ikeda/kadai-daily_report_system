package controllers.reports;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Employee;
import models.Report;
import models.validators.ReportValidator;
import utils.DBUtil;

/**
 * Servlet implementation class ReportsCreateServlet
 */
@WebServlet("/reports/create")
public class ReportsCreateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReportsCreateServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String _token = (String)request.getParameter("_token");                 //不正アクセスではないかチェック(EmployeesCreateServlet参照)
        if(_token != null && _token.equals(request.getSession().getId())) {
            EntityManager em = DBUtil.createEntityManager();                  //データベースと接続

            Report r = new Report();                                            //Report型をインスタンス化(reportテーブルを用意)

            r.setEmployee((Employee)request.getSession().getAttribute("login_employee"));   //インスタンス化したReport型と繋がっているemployee型に、"login_employee"の情報(id)を入れる。Employee型でキャストする必要あり。
                                                                                            //ログインしているEmployee型のみのレポートがcreateできるようにするため。
                                                                                            //受け取った"login_employee"をEmployee型でキャストし、「r」のEmployeeに入れる(setする)。
            Date report_date = new Date(System.currentTimeMillis());                    //Date型をインスタンス化(名前は"report_date"★)し、このサーブレット(Createサーブレット)にアクセスした時点での現在の日付を初期値として取得する。
                                                                                           //Newサーブレットでも取得していたので、そこからセッションスコープに入れて送ってきてもよい。(ただし、その場合は最後にremoveする必要があるが…)
            String rd_str = request.getParameter("report_date");                          //String型をインスタンス化(名前は"rd_str")し、new.jspから送られてきた日付("report_date")を入れる。
            if(rd_str != null && !rd_str.equals("")) {                                    //"rd_str"が値が入っていた場合、
                report_date = Date.valueOf(request.getParameter("report_date"));          //new.jspから送られてきた日付("report_date")をDate型でキャストして、このサーブレットでインスタンス化した"report_date"★に上書き。
            }
            r.setReport_date(report_date);                             //インスタンス化したReport型に"report_date"を入れる。

            r.setTitle(request.getParameter("title"));                 //インスタンス化したReport型の"title"に受け取った"title"を入れる。
            r.setContent(request.getParameter("content"));             //インスタンス化したReport型の"content"に受け取った"content"を入れる。

            Timestamp currentTime = new Timestamp(System.currentTimeMillis());      //Timestamp型をインスタンス化して"currentTime"と名付け、現在の時間を入れる。
            r.setCreated_at(currentTime);                              //インスタンス化したReport型の"created_at"に"currentTime"を入れる。
            r.setUpdated_at(currentTime);                              //インスタンス化したReport型の"updated_at"に"currentTime"を入れる。

            r.setLike_count(0);             //いいね数(like_count)の初期値"0"


            List<String> errors = ReportValidator.validate(r);        //"errors"という名前のReportValidate型のリスト(エラーメッセージ群)を作成。
            if(errors.size() > 0) {
                em.close();

                request.setAttribute("_token", request.getSession().getId());
                request.setAttribute("report", r);
                request.setAttribute("errors", errors);

                RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/reports/new.jsp");
                rd.forward(request, response);
            } else {
                em.getTransaction().begin();
                em.persist(r);
                em.getTransaction().commit();
                em.close();
                request.getSession().setAttribute("flush", "登録が完了しました。");

                response.sendRedirect(request.getContextPath() + "/reports/index");
            }
        }
    }

}