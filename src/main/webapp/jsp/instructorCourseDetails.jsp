<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page import="teammates.common.util.FrontEndLibrary" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/course" prefix="course" %>
<c:set var="jsIncludes">
  <script type="text/javascript" src="/js/instructorCourseDetails.js"></script>
</c:set>

<ti:instructorPage title="Course Details" jsIncludes="${jsIncludes}">
  <course:courseInformationContainer courseDetails="${data.courseDetails}"
      instructors="${data.instructors}"
      courseRemindButton="${data.courseRemindButton}"
      courseDeleteAllButton="${data.courseDeleteAllButton}"/>
  <br>
  <t:statusMessage statusMessagesToUser="${data.statusMessagesToUser}"/>
  <input type="hidden" id="show-comment-box" value="false">
  <br>
  <ti:studentList courseId="${data.courseDetails.course.id}" courseIndex="${0}" hasSection="${data.hasSection}" sections="${data.sections}"
      fromCourseDetailsPage="${true}"/>
  <br>
  <br>
  <br>
</ti:instructorPage>
