package controllers.employees;

import java.io.IOException;
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
import models.validators.EmployeeValidator;
import utils.DBUtil;
import utils.EncryptUtil;

/**
 * Servlet implementation class EmployeesCreateServlet
 */
@WebServlet("/employees/create")
public class EmployeesCreateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public EmployeesCreateServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String _token = (String)request.getParameter("_token");                 //_form.jspから送信されたセッションIDに"_token"という名前を付ける
        if(_token != null && _token.equals(request.getSession().getId())) {     //今回のログインで生成された(Newサーブレットで呼び出した)getID(セッションID)が、このCreateサーブレットで呼び出したgetIDと一致していることを確認
                                                                                //同じセッションであれば、Newサーブレットで呼び出したIDと、このCreateサーブレットで呼び出したIDは同じなので、正規のアクセスと言える。
            EntityManager em = DBUtil.createEntityManager();      //データベースと接続するおまじない。(データベースに「em」と名付けた。(=DBUtil(DAT)のcreateEntityManagerメソッドを呼び出し、「em」と名付けた))

            Employee e = new Employee();                //テーブル"Employees"のDTO("Employeeクラス")をインスタンス化
                                                        //この瞬間、Auto incrementのidが自動生成
            e.setCode(request.getParameter("code"));    //codeカラムに_form.jspで取得したcodeを入れる
            e.setName(request.getParameter("name"));    //nameカラムに_form.jspで取得したnameを入れる
            e.setPassword(                              //passwordを受け取り、utils>EncryptUtilでペッパーを付けた文字をハッシュ化して、passwordカラムに入れる。
                    EncryptUtil.getPasswordEncrypt(
                            request.getParameter("password"),
                            (String)this.getServletContext().getAttribute("pepper")
                            )
                    );
            e.setAdmin_flag(Integer.parseInt(request.getParameter("admin_flag")));   //Admin_flagカラムに_form.jspで取得したAdmin_flag(管理or一般)を入れる

            Timestamp currentTime = new Timestamp(System.currentTimeMillis());    //現在日時の情報を持つ日付型のオブジェクトを取得する。
            e.setCreated_at(currentTime);       //取得した時間を"Created_at"にセット
            e.setUpdated_at(currentTime);       //取得した時間を"Updated_at"にセット
            e.setDelete_flag(0);

            List<String> errors = EmployeeValidator.validate(e, true, true);        //初期値を引数(true=バリデート実施)として実行。エラーメッセージリストとして作成される。
            if(errors.size() > 0) {         //EmployeeValidatorで作ったエラーメッセージリストが一つでもあれば実行。
                em.close();                 //新規登録はせずに、まずはデータベースの接続を閉じる。

                request.setAttribute("_token", request.getSession().getId());   //エラーの場合、"_token"は、new.jsp→_form.jspで使用しないので、送らなくてもよい。(書かなくてもよい)
                request.setAttribute("employee", e);                            //エラーの場合、データベース"employee"は、new.jsp→_form.jspで使用しないので、送らなくてもよい。(書かなくてもよい)
                request.setAttribute("errors", errors);     //エラーメッセージリスト「errors」をnew.jsp→_form.jspに送る。(_form.jspでは、一番上にリストを表示している。)

                RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/employees/new.jsp");
                rd.forward(request, response);
            } else {
                em.getTransaction().begin();        //データ登録に関するすべての処理で、エラーが無かった場合に一つの処理としてまとめて実行する。=トランザクション開始。
                em.persist(e);                      //データベースへの保存
                em.getTransaction().commit();       //データベースへの新規登録を確定
                em.close();                         //データベースの接続を閉じる。
                request.getSession().setAttribute("flush", "登録が完了しました。");   //"flush"に「登録が完了しました。」を入れて、indexサーブレットに送る。

                response.sendRedirect(request.getContextPath() + "/employees/index");   //indexサーブレットに遷移する。response.sendRedirect(request.getContextPath()+"URL")で、URL先に遷移する。
            }
        }
    }

}