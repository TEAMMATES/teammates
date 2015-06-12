<%@ tag description="displayDetails.tag - Displays instructor list on student course details page" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:forEach items="${data.studentCourseDetailsPanel.instructors}" var="instructor">
    <c:if test="${instructor.displayedToStudents}">
        ${instructor.displayedName}:
        <a href="mailto:${instructor.email}">
            ${instructor.name} (${instructor.email})
        </a>
        <br>
    </c:if>
</c:forEach>