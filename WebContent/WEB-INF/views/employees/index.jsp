<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:import url="../layout/app.jsp">           <%-- 共通のひな型(layoutフォルダーの「app.jsp」)を挿入 --%>
    <c:param name="content">                 <%-- このタグで挟んだ内容に"content"と名付け、app.jsp(共通のひな型)の${param.content}に挿入 --%>
        <c:if test="${flush != null}">       <%-- test属性には、trueかfalseを必ず入れる必要がある。そのため、if文やwhen文で使われる。 --%>
                                             <%-- Indexサーブレット内でflushの値が入っていれば実行(=Createサーブレットで新規登録・Updateサーブレットで更新)が成功すれば実行) --%>
            <div id="flush_success">         <%-- このdivのidに"flush_success"と名付ける。CSSで「#flush_success{}を設定するとフラッシュメッセージになる。」 --%>
                <c:out value="${flush}"></c:out>    <%-- 「flush」の中身を表示する --%>
            </div>
        </c:if>
        <h2>従業員　一覧</h2>
        <table id="employee_list">
            <tbody>
                <tr>
                    <th>社員番号</th>
                    <th>氏名</th>
                    <th>操作</th>
                </tr>
                <c:forEach var="employee" items="${employees}" varStatus="status">           <%-- forEachで繰り返しの記述をする。 --%>
                                                                                             <%-- EmployeeIndexサーブレットから送ってきたリスト(employees)の各レコードに"employee"という名前を付ける --%>
                                                                                             <%-- varStatus（現在のループ回数）に"status"と名付ける --%>
                    <tr class="row${status.count % 2}">                                      <%-- ループ回数が偶数か奇数か(余りが0か1)でクラス名を分ける。row1かrow0か。 --%>
                        <td><c:out value="${employee.code}" /></td>                          <%-- 現在のループのレコードのcodeを表示 --%>
                        <td><c:out value="${employee.name}" /></td>                          <%-- 現在のループのレコードのnameを表示 --%>
                        <td>
                            <c:choose>
                                <c:when test="${employee.delete_flag == 1}">                 <%-- もしレコードを削除してれば「削除済み」と表示 --%>
                                    （削除済み）
                                </c:when>
                                <c:otherwise>
                                    <a href="<c:url value='/employees/show?id=${employee.id}' />">詳細を表示</a>    <%-- そうでなければ、選んだidのEmploeeShowサーブレットに遷移 --%>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <div id="pagination">
            （全 ${employees_count} 件）<br />
            <c:forEach var="i" begin="1" end="${((employees_count - 1) / 15) + 1}" step="1">     <%-- forEachで繰り返しの記述をする。表示は「1」～「(全データ数÷15)+1」。つまりページ数。 --%>
                <c:choose>
                    <c:when test="${i == page}">          <%-- もしループ回数"i"が、Indexサーブレットから送ってきた"page"と同じであれば、特になし。そのままのページ数を表示 --%>
                        <c:out value="${i}" />&nbsp;
                    </c:when>
                    <c:otherwise>
                        <a href="<c:url value='/employees/index?page=${i}' />"><c:out value="${i}" /></a>&nbsp;     <%-- もしループ回数"i"が、Indexサーブレットから送ってきた"page"と異なっていれば、Indexサーブレットのそのページに遷移 --%>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        </div>
        <p><a href="<c:url value='/employees/new' />">新規従業員の登録</a></p>

    </c:param>
</c:import>