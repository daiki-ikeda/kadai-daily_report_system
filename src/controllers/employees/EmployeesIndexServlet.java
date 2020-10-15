package controllers.employees;

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
import utils.DBUtil;

/**
 * Servlet implementation class EmployeesIndexServlet
 */
@WebServlet("/employees/index")
public class EmployeesIndexServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public EmployeesIndexServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        EntityManager em = DBUtil.createEntityManager();   //データベースと接続するおまじない。(データベースに「em」と名付けた。(=DBUtil(DAT)のcreateEntityManagerメソッドを呼び出し、「em」と名付けた))

        int page = 1;       //開くページ数を取得する。デフォルトは1ページ目
        try{
            page = Integer.parseInt(request.getParameter("page"));      //最初にこのサーブレットにアクセスされた時点では、getPrameter("page")には値はないので、初期値=1が入る。
                                                                         //このサーブレットからshow.jspに遷移し、そこで"page"をクリックすることで、pageに「1」以外の数字が入る
        } catch(NumberFormatException e) { }        // pageのパラメーターが無かったり(但しデフォルトは「1」)、page=a のように文字が入った場合は、エラーを返す。

        List<Employee> employees = em.createNamedQuery("getAllEmployees", Employee.class) //Employees型（Employee.class(DTO)で定義）のListに「employees」と名付ける。
                                                                                           //createNamedQueryの引数に、Employee.class(DTO)で定義した"getAllEmployees"(SELECT文)を入れることで、データベース(em)からデータを取得できる。
                                     .setFirstResult(15 * (page - 1))   //何件目からのデータを取得するか。(=ページの最初に表示するデータのid。1ページ目なら0番目。2ページ目なら15番目(「0」が最初)。)
                                     .setMaxResults(15)     //データの最大取得件数は何件か。(=最大表示数＝15個)
                                     .getResultList();      //問い合わせた結果(データベースのレコード群=List)を"employees"に代入

        long employees_count = (long)em.createNamedQuery("getEmployeesCount", Long.class)
                                       .getSingleResult();  //問い合わせ結果(レコードの全件数=long型)を"employees_count"に代入。「全 80 件」などと表示したい。

        em.close();

        //リクエストスコープ：１回のデータ遷移の間だけ、データが存在する。通常サーブレットからjspにデータを送る際に使用する。
        //書き方：request.setAttribute("jspで使用する変数名", このサーブレット内で定義して代入した内容);
        request.setAttribute("employees", employees);                   //Listデータを、リクエストスコープの"employees"にset
        request.setAttribute("employees_count", employees_count);       //全件数を、リクエストスコープの"employees_count"にset
        request.setAttribute("page", page);                             //ページ数を、リクエストスコープの"page"にset
        if(request.getSession().getAttribute("flush") != null) {                       //別サーブレット(Createサーブレット)で"flush"に値を入れていれば
            request.setAttribute("flush", request.getSession().getAttribute("flush"));  //別サーブレットから受け取った"flush"の値を、このサーブレットの"flash"にset
            request.getSession().removeAttribute("flush");                              //別サーブレットで"flash"に入れた文字を消す。
        }

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/employees/index.jsp");  //ビュー(jsp)と接続するおまじない。(getRequestDispatcherメソッドの引数にビューとなるjspファイルの場所を入れ、「rd」と名付けた。)
        rd.forward(request, response);      //ビュー(jsp)と接続するおまじない。(forwardメソッドで、rd(ビューへのアクセス)にrequestとresponseの情報を入れる。
    }
}