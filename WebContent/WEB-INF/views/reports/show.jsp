<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:import url="/WEB-INF/views/layout/app.jsp">
    <c:param name="content">
        <c:choose>
            <c:when test="${report != null}">
                <h2>日報　詳細ページ</h2>

                <table>
                    <tbody>
                        <tr>
                            <th>氏名</th>
                            <td><c:out value="${report.employee.name}" /></td>
                        </tr>
                        <tr>
                            <th>日付</th>
                            <td><fmt:formatDate value="${report.report_date}" pattern="yyyy-MM-dd" /></td>
                        </tr>
                        <tr>
                            <th>内容</th>
                            <td>
                                <pre><c:out value="${report.content}" /></pre>
                            </td>
                        </tr>
                        <tr>
                            <th>登録日時</th>
                            <td>
                                <fmt:formatDate value="${report.created_at}" pattern="yyyy-MM-dd HH:mm:ss" />
                            </td>
                        </tr>
                        <tr>
                            <th>更新日時</th>
                            <td>
                                <fmt:formatDate value="${report.updated_at}" pattern="yyyy-MM-dd HH:mm:ss" />
                            </td>
                        </tr>
                        <c:if test="${sessionScope.login_employee.id == report.employee.id}">
                            <tr>
                                <th>いいね数</th>
                                <td>
                                    <c:out value="${report.like_count}" />
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
                <c:choose>
                   <c:when test="${sessionScope.login_employee.id == report.employee.id}">         <%-- test属性には、trueかfalseを必ず入れる必要がある。そのため、if文やwhen文で使われる。 --%>
                                                                                              <%-- TopPageIndexサーブレットで、セッションスコープに入れた"login_employee"の"id"を参照。この"report"と繋げた"employee"の"id"と等しければ --%>
                       <p><a href="<c:url value="/reports/edit?id=${report.id}" />">この日報を編集する</a></p>   <%-- 「この日報を編集する。」を表示。"id"という名前で、ここで開いている"report"の"id"を引数としてEditサーブレットに送る --%>
                  </c:when>

                  <c:otherwise>
                  <br /><br />
                    <form method="POST" action="<c:url value='/reports/likecount?id=${report.id}&report_name=${report.employee.name}&report_tittle=${report.title} ' />">
                        <input type="hidden" name="like_count" value="${report.like_count}" />
                        <button type="submit">この日報にいいねをする</button>
                    </form>
                  </c:otherwise>
                </c:choose>
            </c:when>
            <c:otherwise>
                <h2>お探しのデータは見つかりませんでした。</h2>
            </c:otherwise>
        </c:choose>

        <p><a href="<c:url value="/reports/index" />">一覧に戻る</a></p>
    </c:param>
</c:import>