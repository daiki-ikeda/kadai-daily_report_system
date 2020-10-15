package models.validators;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import models.Employee;
import utils.DBUtil;

public class EmployeeValidator {
    public static List<String> validate(Employee e, Boolean code_duplicate_check_flag, Boolean password_check_flag) {   //"validate"という名前のlist型を定義。引数は、テーブル「employees」,boolean,boolean。
                                                                                                                         //初期の引数は、Createサーブレットからのデータを受け取る。
                                                                                                                         //booleanの「flag」はスイッチのようなもので、初期値が"true"であれば、チェックを実施する。初期値が"false"であれば、チェックしない。
        List<String> errors = new ArrayList<String>();

        String code_error = _validateCode(e.getCode(), code_duplicate_check_flag);      //社員番号エラーチェック。引数のCode(=新規作成した社員番号)を、重複チェックした結果を受け取る。
        if(!code_error.equals("")) {                //
            errors.add(code_error);                 //
        }

        String name_error = _validateName(e.getName());
        if(!name_error.equals("")) {
            errors.add(name_error);
        }

        String password_error = _validatePassword(e.getPassword(), password_check_flag);
        if(!password_error.equals("")) {
            errors.add(password_error);
        }

        return errors;
    }

    // 社員番号
    private static String _validateCode(String code, Boolean code_duplicate_check_flag) {      //社員番号エラー(Code_error)の定義。引数は、「データベース"Employee"から社員番号」,boolean
        // 必須入力チェック
        if(code == null || code.equals("")) {   //nullという値が来るかどうかではなく、パラメータが入力されたかどうかのワンセットの決まりとして覚えておく。
            return "社員番号を入力してください。";
        }

        // すでに登録されている社員番号との重複チェック
        if(code_duplicate_check_flag) {                             //Createサーブレットからの引数が"true"なので、チェックを実施。
            EntityManager em = DBUtil.createEntityManager();      //データベースと接続するおまじない。(データベースに「em」と名付けた。(=DBUtil(DAT)のcreateEntityManagerメソッドを呼び出し、「em」と名付けた))
            long employees_count = (long)em.createNamedQuery("checkRegisteredCode", Long.class)
                                           .setParameter("code", code)      //取得したcodeを"checkRegisteredCode"の"code"にsetする。
                                             .getSingleResult();            //codeの重複があれば「1」、無ければ「0」をgetする。
            em.close();
            if(employees_count > 0) {
                return "入力された社員番号の情報はすでに存在しています。";
            }
        }

        return "";
    }

    // 社員名の必須入力チェック
    private static String _validateName(String name) {
        if(name == null || name.equals("")) {
            return "氏名を入力してください。";
        }

        return "";
    }

    // パスワードの必須入力チェック
    private static String _validatePassword(String password, Boolean password_check_flag) {
        // パスワードを変更する場合のみ実行
        if(password_check_flag && (password == null || password.equals(""))) {
            return "パスワードを入力してください。";
        }
        return "";
    }
}