package controllers.toppage;

import java.io.IOException;
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
import utils.DBUtil;

/**
 * Servlet implementation class TopPageIndexServlet
 */
@WebServlet("/index.html")
public class TopPageIndexServlet extends HttpServlet {
        private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public TopPageIndexServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        EntityManager em = DBUtil.createEntityManager();      //データベースと接続するおまじない。(データベースに「em」と名付けた。(=DBUtil(DAT)のcreateEntityManagerメソッドを呼び出し、「em」と名付けた))

        Employee login_employee = (Employee)request.getSession().getAttribute("login_employee");        //Loginサーブレットで取得した(codeとpasswordが一致した)Employee型のレコードをオブジェクト化して"login_employee"と名付けた
                                                                                                         //セッションスコープに入れることで、すべてのサーブレットに引き継がれる。
        int page;       //int型の変数"page"を定義
        try{
            page = Integer.parseInt(request.getParameter("page"));          //最初は"page"に値は入っていないので、catchを実行。
                                                                             //topPage/index.jspからpage"が送られてきたら、tryを実行
        } catch(Exception e) {
            page = 1;
        }
        List<Report> reports = em.createNamedQuery("getMyAllReports", Report.class)     //Reports型（Report.class(DTO)で定義）のListに「reports」と名付ける。
                                  .setParameter("employee", login_employee)              //ログインしているユーザー情報(Employee型のレコード"login_employee")を取得して、情報に"employee"と名付けてデータベースにアクセス。
                                  .setFirstResult(15 * (page - 1))   //何件目からのデータを取得するか。(=デフォルトは「1」ページ目で0番目。2ページ目なら15番目(「0」が最初)。)
                                  .setMaxResults(15)                 //データの最大取得件数は何件か。(=最大表示数＝15個)
                                  .getResultList();                  //問い合わせた結果(データベースのレコード群=List)を"reports"に代入

        long reports_count = (long)em.createNamedQuery("getMyReportsCount", Long.class)
                                     .setParameter("employee", login_employee)              //ログインしているユーザー情報(Employee型のレコード"login_employee")を取得して、情報に"employee"と名付ける。
                                     .getSingleResult();

        em.close();


        request.setAttribute("reports", reports);
        request.setAttribute("reports_count", reports_count);
        request.setAttribute("page", page);

        if(request.getSession().getAttribute("flush") != null) {                          //ログイン直後、Loginサーブレットから"flush"にメッセージが入った状態で受け取る。
            request.setAttribute("flush", request.getSession().getAttribute("flush"));    //Loginサーブレットから受け取った"flush"のメッセージを"flush"に入入れてtopPageのindex.jspに送る。
            request.getSession().removeAttribute("flush");                                //"flush"のメッセージを消す。ログイン直後以外は、"flush"には値は入っていない。
        }

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/topPage/index.jsp");
        rd.forward(request, response);
    }

}