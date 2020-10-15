package filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import models.Employee;

/**
 * Servlet Filter implementation class LoginFilter
 */
@WebFilter("/*")
public class LoginFilter implements Filter {

    /**
     * Default constructor.
     */
    public LoginFilter() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @see Filter#destroy()
     */
    public void destroy() {
        // TODO Auto-generated method stub
    }

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String context_path = ((HttpServletRequest)request).getContextPath();       //"getContextPath"は「http://localhost:8080/daily_report_system」
        String servlet_path = ((HttpServletRequest)request).getServletPath();       //"getServletPath"は「http://localhost:8080/daily_report_system/〇〇」の〇〇の部分

        if(!servlet_path.matches("/css.*")) {       // CSSフォルダ内は認証処理から除外する。CSSフォルダにないものに、以下を実行。
            HttpSession session = ((HttpServletRequest)request).getSession();       //セッションスコープのメソッド(getSession())に"session"と名付ける。

            // セッションスコープに保存された従業員（ログインユーザ）情報を取得
            Employee e = (Employee)session.getAttribute("login_employee");          //Loginサーブレットで生成したから受け取ったEmployee型の"login_employee"(ログインしたユーザー情報)に"e"と名付ける。

            if(!servlet_path.equals("/login")) {        // ログイン画面以外について。loginサーブレットでないものに、以下を実行。
                // ログアウトしている状態であれば
                // ログイン画面にリダイレクト
                if(e == null) {                       //"login_employee"(ログインしたユーザー情報)がnull。つまりログアウト状態ならば、
                    ((HttpServletResponse)response).sendRedirect(context_path + "/login");       // ログイン画面に遷移
                    return;
                }

                // 従業員管理の機能は管理者のみが閲覧できるようにする
                if(servlet_path.matches("/employees.*") && e.getAdmin_flag() == 0) {         //employeesフォルダーのサーブレット(index,create,newなど)で、「管理者」ならば
                    ((HttpServletResponse)response).sendRedirect(context_path + "/");        //http://localhost:8080/daily_report_system/employees/(=従業員管理フォルダー)にアクセスする。
                    return;
                }
            } else {                                    // ログイン画面について
                // ログインしているのにログイン画面を表示させようとした場合は
                // システムのトップページにリダイレクト
                if(e != null) {
                    ((HttpServletResponse)response).sendRedirect(context_path + "/");
                    return;
                }
            }
        }

        chain.doFilter(request, response);
    }

    /**
     * @see Filter#init(FilterConfig)
     */
    public void init(FilterConfig fConfig) throws ServletException {
        // TODO Auto-generated method stub
    }

}
