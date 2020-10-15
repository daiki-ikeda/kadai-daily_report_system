package controllers.login;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Employee;
import utils.DBUtil;
import utils.EncryptUtil;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    // ログイン画面を表示
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("_token", request.getSession().getId());       //このサーブレットが起動したときに自動生成したIdを"_token"に入れる。getID(セッションID)は、webアプリにアクセスしたときに自動で生成されるID。DBのIDとは異なる。
        request.setAttribute("hasError", false);                           //"hasError"の初期値に"false"をセット。login.jspで最初からエラーメッセージを出さない。下の「ログイン処理を実行」でログインに失敗すると"false"になる。
        if(request.getSession().getAttribute("flush") != null) {           //既に"flush"に値が入っている場合、つまり既にログインしている場合
            request.setAttribute("flush", request.getSession().getAttribute("flush"));   //"flush"に受け取った"flush"の値を入れる
            request.getSession().removeAttribute("flush");                  //getSessionの"flush"を削除。login.jspに送る"flush"にはデータあり。
        }

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/login/login.jsp");
        rd.forward(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    // ログイン処理を実行
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 認証結果を格納する変数
        Boolean check_result = false;

        String code = request.getParameter("code");                 //login.jspで入力された社員番号
        String plain_pass = request.getParameter("password");      //login.jspで入力されたパスワード

        Employee e = null;      //データベースのEmployee型("e"と名付けた)を呼び出し、初期値に"null"を入れる

        if(code != null && !code.equals("") && plain_pass != null && !plain_pass.equals("")) {      //要は、社員番号とパスワードに値があれば実行
            EntityManager em = DBUtil.createEntityManager();      //データベースと接続するおまじない。(データベースに「em」と名付けた。(=DBUtil(DAT)のcreateEntityManagerメソッドを呼び出し、「em」と名付けた))

            String password = EncryptUtil.getPasswordEncrypt(    //入力されたパスワードに、ソルト文字列をを付けて「password」と名付けた
                    plain_pass,
                    (String)this.getServletContext().getAttribute("pepper")
                    );

            // 社員番号とパスワードが正しいかチェックする
            try {
                e = em.createNamedQuery("checkLoginCodeAndPassword", Employee.class)    //Employeeのデータベースの中で、"code"と"password"が一致するものを表示するSQL文
                      .setParameter("code", code)                                       //login.jspから取得したcodeに"code"とつけて、SQL文に入れる
                      .setParameter("pass", password)                                   //login.jspから取得したpasswordに"pass"とつけて、SQL文に入れる
                      .getSingleResult();                                               //SQLの実行。
            } catch(NoResultException ex) {}        //異なっていれば何も実行しない。つまり、e = nullのまま。

            em.close();

            if(e != null) {                         //eにSQL文が入っていれば
                check_result = true;                //"check_result"にtrueを代入
            }                                       //eがnullのままであれば、"check_result"はfalseのまま
        }

        if(!check_result) {                         //"check_result"がtrue出なければ(falseのままであれば)
            // 認証できなかったらログイン画面に戻る
            request.setAttribute("_token", request.getSession().getId());       //セッションIDの取得。ただし、ログイン失敗の処理のため、必要ではない。
            request.setAttribute("hasError", true);                            //"hasError"に"true"を代入し、login.jspに送る。エラーメッセージを表示させる。
            request.setAttribute("code", code);                                //longin.jspで取得した社員番号(code)に"code"と名付けてlogin.jspに送る。ただし、ログイン失敗の処理のため、必要ではない。

            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/login/login.jsp");
            rd.forward(request, response);
        } else {
            // 認証できたらログイン状態にしてトップページへリダイレクト
            request.getSession().setAttribute("login_employee", e);          //一致したデータベースに"login_employee"と名前を付ける。(一致したデータベースをオブジェクト化)
                                                                             //セッションスコープに入れることで、ログアウト時(Logoutサーブレット)まで保持。
            request.getSession().setAttribute("flush", "ログインしました。");    //"flush"に「ログインしました。」のメッセージを入れる。
            response.sendRedirect(request.getContextPath() + "/");               //TopPageIndexサーブレットに遷移
        }
    }

}