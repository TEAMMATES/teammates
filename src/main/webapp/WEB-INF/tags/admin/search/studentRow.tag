<%@ tag description="studentResultsTable.tag - student results row" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="student" type="teammates.ui.template.AdminSearchStudentRow" required="true" %>

<tr id="${student.id}" class="studentRow">
    <%-- Institute --%>
    <td>${empty student.institute ? "" : fn:escapeXml(student.institute)}</td> <%-- also checks if it is null --%>

    <%-- Course [Section] (Team) --%>
    <c:choose>
        <c:when test="${not empty student.courseName}">
            <td data-toggle="tooltip" data-placement="top" title="${student.courseName}">
                ${student.courseId}<br><c:out value="${student.section}"/><br><c:out value="${student.team}"/>
            </td>
        </c:when>
        <c:otherwise>
            <td>${student.courseId}<br><c:out value="${student.section}"/><br><c:out value="${student.team}"/></td>
        </c:otherwise>
    </c:choose>

    <%-- Name --%>
    <c:choose>
        <c:when test="${not empty student.links.detailsPageLink}">
            <td>
                <a class="detailsPageLink" href="${student.links.detailsPageLink}" target="_blank" rel="noopener noreferrer"><c:out value="${student.name}"/></a>
            </td>
        </c:when>
        <c:otherwise>
            <td><c:out value="${student.name}"/></td>
        </c:otherwise>
    </c:choose>

    <%-- Google ID [Details] --%>
    <td>
        <a href="${student.links.homePageLink}" target="_blank" rel="noopener noreferrer" class="homePageLink">
            ${empty student.links.homePageLink ? "" : student.googleId} <%-- also checks if it is null --%>
        </a>
    </td>

    <%-- Comments --%>
    <td><c:out value="${student.comments}"/></td>

    <%-- Options --%>
    <td>
        <%-- View recent actions --%>
        <c:if test="${not empty student.viewRecentActionsId}">
            <form method="post" target="_blank" action="<%=Const.ActionURIs.ADMIN_ACTIVITY_LOG_PAGE%>">
                <button type="submit" id="${student.viewRecentActionsId}_recentActions"
                        class="btn btn-link btn-xs recentActionButton">

                    <span class="glyphicon glyphicon-zoom-in"></span>View Recent Actions
                </button>

                <input type="hidden" name="filterQuery" value="${student.viewRecentActionsId}">
                <input type="hidden" name="courseId" value="${student.courseId}">
            </form>
        </c:if>

        <%-- Reset Google ID --%>
        <c:if test="${not empty student.googleId}">
            <button type="button" id="${student.googleId}_resetGoogleId"
                    onclick="submitResetGoogleIdAjaxRequest('${student.courseId}' , '${student.email}', '${student.googleId}', this);"
                    class="btn btn-link btn-xs resetGoogleIdButton">

                <span class="glyphicon glyphicon-refresh"></span>Reset Google Id
            </button>
        </c:if>
    </td>
</tr>

<tr class="has-danger list-group fslink fslink_student fslink${student.id}" style="display: none;">
    <td colspan="5">
        <ul class="list-group">
            <%-- Email --%>
            <c:if test="${not empty student.email}">
                <li class="list-group-item list-group-item-success has-success">
                    <strong>Email</strong>
                    <input name="studentEmail" value="${student.email}" readonly class="form-control">
                </li>
            </c:if>

            <%-- Course join link --%>
            <li class="list-group-item list-group-item-info">
                <form class="openMailEditorWithDefaults">
                    <strong>Course Join Link</strong>
                    <button type="submit"
                            class="btn btn-link btn-xs openMailEditorWithDefaults">
                            <span class="glyphicon glyphicon-send"></span>Send to student
                    </button>
                    <input type="hidden" value="${student.courseName}" class="courseName">
                    <input type="hidden" value="${student.courseId}" class="Id">
                    <input type="hidden" value="${student.name}" class="studentName">
                    <input type="hidden" value="Invitation to join course" class="subjectType">
                    <input type="hidden" value="" class="status">
                    <input value="${student.links.courseJoinLink}" readonly class="form-control url">
                </form>
            </li>

            <%-- Open feedback sessions --%>
            <c:if test="${not empty student.openFeedbackSessions}">
                <c:forEach items="${student.openFeedbackSessions}" var="session">
                    <li class="list-group-item list-group-item-warning">
                        <form class="openMailEditorWithDefaults">
                            <strong>${session.fsName}</strong>
                            <button type="submit"
                                class="btn btn-link btn-xs">
                                <span class="glyphicon glyphicon-send"></span>Send to student
                            </button>
                            <input type="hidden" value="${student.courseName}" class="courseName">
                            <input type="hidden" value="${session.fsName}" class="Id">
                            <input type="hidden" value="${student.name}" class="studentName">
                            <input type="hidden" value="Feedback session now open" class="subjectType">
                            <input type="hidden" value="Open" class="status">
                            <input value="${session.link}" readonly class="form-control url">
                        </form>
                    </li>
                </c:forEach>
            </c:if>

            <%-- Closed feedback sessions --%>
            <c:if test="${not empty student.closedFeedbackSessions}">
                <c:forEach items="${student.closedFeedbackSessions}" var="session">
                    <li class="list-group-item list-group-item-danger">
                        <form class="openMailEditorWithDefaults">
                            <strong>${session.fsName}</strong>
                            <button type="submit"
                                class="btn btn-link btn-xs">
                                <span class="glyphicon glyphicon-send"></span>Send to student
                            </button>
                            <input type="hidden" value="${student.courseName}" class="courseName">
                            <input type="hidden" value="${session.fsName}" class="Id">
                            <input type="hidden" value="${student.name}" class="studentName">
                            <input type="hidden" value="Feedback session closed" class="subjectType">
                            <input type="hidden" value="Closed" class="status">
                            <input value="${session.link}" readonly class="form-control url">
                        </form>
                    </li>
                </c:forEach>
            </c:if>

            <%-- Published feedback sessions --%>
            <c:if test="${not empty student.publishedFeedbackSessions}">
                <c:forEach items="${student.publishedFeedbackSessions}" var="session">
                    <li class="list-group-item list-group-item-success">
                        <form class="openMailEditorWithDefaults">
                            <strong>${session.fsName}</strong>
                            <button type="submit"
                                class="btn btn-link btn-xs">
                                <span class="glyphicon glyphicon-send"></span>Send to student
                            </button>
                            <input type="hidden" value="${student.courseName}" class="courseName">
                            <input type="hidden" value="${session.fsName}" class="Id">
                            <input type="hidden" value="${student.name}" class="studentName">
                            <input type="hidden" value="Feedback session results published" class="subjectType">
                            <input type="hidden" value="Published" class="status">
                            <input value="${session.link}" readonly class="url">
                        </form>
                    </li>
                </c:forEach>
            </c:if>
        </ul>
    </td>
</tr>
