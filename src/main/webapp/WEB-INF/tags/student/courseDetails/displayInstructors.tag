<%@ tag description="displayDetails.tag - Displays instructor list on student course details page" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ attribute name="instructor" required="true" type="teammates.common.datatransfer.attributes.InstructorAttributes"%>

<c:if test="${instructor.displayedToStudents}">
  ${fn:escapeXml(instructor.displayedName)}:
  <a href="mailto:${instructor.email}">
    ${instructor.name} (${instructor.email})
  </a>
  <br>
</c:if>
