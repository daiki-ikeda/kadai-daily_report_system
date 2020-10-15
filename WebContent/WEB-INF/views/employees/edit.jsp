<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:import url="/WEB-INF/views/layout/app.jsp">
    <c:param name="content">
        <c:choose>
            <c:when test="${employee != null}">
                <h2>id : ${employee.id} の従業員情報　編集ページ</h2>           <%-- 該当のidのEmployees型（Employee.class(DTO)で定義）のidを表示。Editサーブレットで定義した"employee_id"でも良い。 --%>
                <p>（パスワードは変更する場合のみ入力してください）</p>
                <form method="POST" action="<c:url value='/employees/update' />">   <%-- 編集した情報をEmployeeUpdateサーブレットに送る。 --%>
                    <c:import url="_form.jsp" />
                </form>

                <p><a href="#" onclick="confirmDestroy();">この従業員情報を削除する</a></p>
                <form method="POST" action="<c:url value='/employees/destroy' />">   <%-- 下記の情報(このログインのセッションID)をEmployeeDestroyサーブレットに送る。 --%>
                    <input type="hidden" name="_token" value="${_token}" />
                </form>
                <script>
                    function confirmDestroy() {                               <%-- 以下はJavaScriptで削除機能を実行している。 --%>
                        if(confirm("本当に削除してよろしいですか？")) {        <%-- JavaScript --%>
                            document.forms[1].submit();                        <%-- JavaScript --%>
                        }
                    }
                </script>
            </c:when>
            <c:otherwise>
                <h2>お探しのデータは見つかりませんでした。</h2>    //"employee"にデータがない場合(URL操作などで遷移してきた場合)、表示する。
            </c:otherwise>
        </c:choose>

        <p><a href="<c:url value='/employees/index' />">一覧に戻る</a></p>
    </c:param>
</c:import>