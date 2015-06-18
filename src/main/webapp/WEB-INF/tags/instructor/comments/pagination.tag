<%@ tag description="Course Pagination" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="previousPageLink" required="true" %>
<%@ attribute name="coursePaginationList" type="java.util.Collection" required="true" %>
<%@ attribute name="viewingDraft" required="true" %>
<%@ attribute name="currentCourseId" required="true" %>
<%@ attribute name="instructorCommentsLink" required="true" %>
<%@ attribute name="nextPageLink" required="true" %>
<ul class="pagination">
    <li><a href="${previousPageLink}">«</a></li>
    <c:forEach items="${coursePaginationList}" var="courseId">
        <li
            class="${!viewingDraft && courseId == currentCourseId ? 'active' : ''}">
            <a href="${instructorCommentsLink}&courseid=${courseId}">${courseId}</a>
        </li>
    </c:forEach>
    <li><a href="${nextPageLink}">»</a></li>
</ul>