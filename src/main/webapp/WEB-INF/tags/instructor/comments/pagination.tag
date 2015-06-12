<%@ tag description="Course Pagination" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<%@ tag import="teammates.common.util.Const" %>
<ul class="pagination">
    <li><a href="${data.previousPageLink}">«</a></li>
    <%--<li class="<%= data.isViewingDraft ? "active" : "" %>"><a
        href="<%= data.getInstructorCommentsLink() %>">Drafts</a></li>--%>
    <c:forEach items="${data.coursePaginationList}" var="courseId">
        <li
            class="${!data.viewingDraft && courseId == data.courseId ? 'active' : ''}">
            <a href="${data.instructorCommentsLink}&courseid=${courseId}">${courseId}</a>
        </li>
    </c:forEach>
    <li><a href="${data.nextPageLink}">»</a></li>
</ul>