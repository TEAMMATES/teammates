<%@ tag description="StudentComments - Course pagination" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<ul class="pagination">
    <li><a href="${data.previousPageLink}">«</a></li>
    <c:forEach items="${data.coursePaginationList}" var="courseId">
        <li class="${courseId == data.courseId ? 'active' : ''}">
            <a href="${data.studentCommentsLink}&courseid=${courseId}">${courseId}</a>
        </li>
    </c:forEach>
    <li><a href="${data.nextPageLink}">»</a></li>
</ul>