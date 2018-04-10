<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/courseStudentDetailsEdit" prefix="csde" %>
<c:set var="jsIncludes">
  <script type="text/javascript" src="/js/instructorCourseStudentEdit.js"></script>
</c:set>
<ti:instructorPage title="Edit Student Details" jsIncludes="${jsIncludes}">
  <csde:studentInformationTable
      studentInfoTable="${data.studentInfoTable}"
      newEmail="${data.newEmail}"
      openOrPublishedEmailSentForTheCourse="${data.openOrPublishedEmailSentForTheCourse}"
      sessionToken="${data.sessionToken}" />
  <br><br>
</ti:instructorPage>
