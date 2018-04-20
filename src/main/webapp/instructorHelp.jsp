<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="jsIncludes">
  <script type="text/javascript" src="/js/instructorHelp.js"></script>
</c:set>
<t:helpPage jsIncludes="${jsIncludes}">
  <h1>Help for Instructors</h1>
  <a name="#top"></a>
  <div id="contentHolder">
    <ul>
      <li>
        <a href="#students">Students</a>
      </li>
      <li>
        <a href="#courses">Courses</a>
      </li>
      <li>
        <a href="#sessions">Sessions</a>
      </li>
      <li>
        <a href="#questions">Questions</a>
      </li>
    </ul>
  </div>
  <div class="separate-content-holder">
    <hr>
  </div>
  <jsp:include page="partials/instructorHelpStudents.jsp"/>
  <jsp:include page="partials/instructorHelpCourses.jsp"/>
  <jsp:include page="partials/instructorHelpSessions.jsp"/>
  <jsp:include page="partials/instructorHelpQuestions.jsp"/>
</t:helpPage>
