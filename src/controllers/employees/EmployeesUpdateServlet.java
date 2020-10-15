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
 * Servlet implementation class EmployeesUpdateServlet
 */
@WebServlet("/employees/update")
public class EmployeesUpdateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public EmployeesUpdateServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String _token = (String)request.getParameter("_token");                 //_form.jspから送信されたセッションIDに"_token"という名前を付ける
        if(_token != null && _token.equals(request.getSession().getId())) {     //今回のログインで生成された(Editサーブレットで呼び出した)getID(セッションID)が、このUpdateサーブレットで呼び出したgetIDと一致していることを確認
            EntityManager em = DBUtil.createEntityManager();     //データベースと接続するおまじない。(データベースに「em」と名付けた。(=DBUtil(DAT)のcreateEntityManagerメソッドを呼び出し、「em」と名付けた))

            Employee e = em.find(Employee.class, (Integer)(request.getSession().getAttribute("employee_id")));    //index.jspの一覧→show.jspから、該当のidのEmployees型（Employee.class(DTO)で定義）のデータ(内容)を取得する。

            // 現在の値と異なる社員番号が入力されていたら
            // 重複チェックを行う指定をする
            Boolean code_duplicate_check = true;
            if(e.getCode().equals(request.getParameter("code"))) {      //edit.jsp(_form.jsp)で作成した社員番号(code)が、現在のcodeと同じであれば
                code_duplicate_check = false;                          //"code_duplicate_check"は"false"、つまりバリデートしない。そして、代入も何もしない。
            } else {                                                   //edit.jsp(_form.jsp)で作成した社員番号(code)が、現在のcodeと違っていれば
                e.setCode(request.getParameter("code"));               //"code_duplicate_check"は"true"、つまりバリデートを行い、edit.jspで作成した"code"を、呼び出しているデータのcodeカラムに入れる
            }

            // パスワード欄に入力があったら
            // パスワードの入力値チェックを行う指定をする
            Boolean password_check_flag = true;                    //パスワードのバリデートを実施。
            String password = request.getParameter("password");
            if(password == null || password.equals("")) {            //edit.jsp(_form.jsp)で作成した社員番号(password)が空欄、つまり更新されていなければ
                password_check_flag = false;                         //"password_check_flag"は"false"、つまりバリデートしない。
            } else {                                                 //edit.jsp(_form.jsp)で作成したpasswordに値があれば、
                e.setPassword(                                       //パスワードをペッパー化して、呼び出しているデータのpasswordカラムに入れる。
                        EncryptUtil.getPasswordEncrypt(
                                password,
                                (String)this.getServletContext().getAttribute("pepper")
                                )
                        );
            }

            e.setName(request.getParameter("name"));                //nameを呼び出しているデータのnameカラムに入れる。
            e.setAdmin_flag(Integer.parseInt(request.getParameter("admin_flag")));
            e.setUpdated_at(new Timestamp(System.currentTimeMillis()));
            e.setDelete_flag(0);                                    //Delete_flag(0)、つまり削除しない。

            List<String> errors = EmployeeValidator.validate(e, code_duplicate_check, password_check_flag);        //初期値を引数(true=バリデート実施)として実行。エラーメッセージリストとして作成される。
            if(errors.size() > 0) {
                em.close();

                request.setAttribute("_token", request.getSession().getId());
                request.setAttribute("employee", e);
                request.setAttribute("errors", errors);

                RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/employees/edit.jsp");
                rd.forward(request, response);
            } else {
                em.getTransaction().begin();
      //【保存】em.persist(e); は不要。「更新」は既存のデータベースから取得したデータに変更をかけてコミットすれば反映されるため。
                em.getTransaction().commit();
                em.close();
                request.getSession().setAttribute("flush", "更新が完了しました。");

                request.getSession().removeAttribute("employee_id");    //Editサーブレット→edit.jsp→このサーブレット(Updateサーブレット)まで"employee_id"を削除

                response.sendRedirect(request.getContextPath() + "/employees/index");
            }
        }
    }

}