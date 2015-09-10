<%@ tag description="Comments - Course pagination" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="coursePagination" type="teammates.ui.template.CoursePagination" required="true" %>

<ul class="pagination">
    <li><a href="${coursePagination.previousPageLink}">«</a></li>
    <c:forEach items="${coursePagination.coursePaginationList}" var="courseId">
        <li class="${courseId == coursePagination.activeCourse ? coursePagination.activeCourseClass : ''}">
            <a href="${coursePagination.userCommentsLink}&courseid=${courseId}">${courseId}</a>
        </li>
    </c:forEach>
    <li><a href="${coursePagination.nextPageLink}">»</a></li>
</ul>