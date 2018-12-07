<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="displayDetails.tag - Displays instructor list on student course details page" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:forEach items="${data.studentCourseDetailsPanel.instructors}" var="instructor">
  <c:if test="${instructor.displayedToStudents}">
    ${fn:escapeXml(instructor.displayedName)}:
    <a href="mailto:${instructor.email}">
      ${instructor.name} (${instructor.email})
    </a>
    <br>
  </c:if>
</c:forEach>
