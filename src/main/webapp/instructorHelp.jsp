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
    <p>
      Have questions about how to use TEAMMATES? This page answers some frequently asked questions.<br>
      If you are new to TEAMMATES, our <a href="/gettingStarted.jsp">Getting Started</a> guide will introduce you to the basic functions of TEAMMATES.<br>
      If you have any remaining questions, don't hesitate to <a href="mailto:teammates@comp.nus.edu.sg">email us</a>. We respond within 24 hours.
    </p>
    <p>
      Browse questions by topic:
    </p>
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
