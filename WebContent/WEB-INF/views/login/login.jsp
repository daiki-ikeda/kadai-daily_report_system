<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:import url="/WEB-INF/views/layout/app.jsp">
    <c:param name="content">
        <c:if test="${hasError}">                           <%-- test属性には、trueかfalseを必ず入れる必要がある。そのため、if文やwhen文で使われる。 --%>
                                                            <%-- Loginサーブレットで、codeかpasswordのいずれかが間違えると、"hasError"が"true"になって実行される --%>
            <div id="flush_error">
                社員番号かパスワードが間違っています。
            </div>
        </c:if>
        <c:if test="${flush != null}">                      <%-- Logoutサーブレットが実行されると、"flush"にメッセージが入って送られてくる。 --%>
            <div id="flush_success">
                <c:out value="${flush}"></c:out>
            </div>
        </c:if>
        <h2>ログイン</h2>
        <form method="POST" action="<c:url value='/login' />">             <%-- Loginサーブレットに下記のデータを送る。 --%>
            <label for="code">社員番号</label><br />
            <input type="text" name="code" value="${code}" />
            <br /><br />

            <label for="password">パスワード</label><br />
            <input type="password" name="password" />
            <br /><br />

            <input type="hidden" name="_token" value="${_token}" />       <%-- Loginサーブレットの「ログイン画面表示」時に生成したセッションIDに"_token"と名付けて、Loginサーブレットに送る。 --%>
            <button type="submit">ログイン</button>
        </form>
    </c:param>
</c:import>