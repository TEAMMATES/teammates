<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="studentsSearchResults.tag - Display search students table for a course" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<%@ attribute name="studentTable" type="teammates.ui.template.SearchStudentsTable" required="true" %>
<%@ attribute name="courseIdx" required="true" %>

<div class="panel panel-info">
  <div class="panel-heading">
    <strong>[${studentTable.courseId}]</strong>
  </div>

  <div class="panel-body padding-0">
    <ti:studentList courseId="${studentTable.courseId}" courseIndex="${courseIdx}" hasSection="${studentTable.hasSection}"
        sections="${studentTable.sections}" fromStudentListPage="${false}" />
  </div>
</div>
