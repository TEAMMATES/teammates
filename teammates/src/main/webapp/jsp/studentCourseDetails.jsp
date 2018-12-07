<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page import="teammates.common.util.Const" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/student" prefix="ts" %>
<%@ taglib tagdir="/WEB-INF/tags/student/courseDetails" prefix="courseDetails" %>

<c:set var="jsIncludes">
  <script type="text/javascript" src="/js/studentCourseDetails.js"></script>
</c:set>

<ts:studentPage title="Team Details for ${data.studentCourseDetailsPanel.courseId}" jsIncludes="${jsIncludes}">
  <t:statusMessage statusMessagesToUser="${data.statusMessagesToUser}" />
  <br>
  <div class="well well-plain">
    <div class="form-horizontal">

      <%-- Course ID --%>
      <courseDetails:displayDetails id="<%=Const.ParamsNames.COURSE_ID%>">
        <jsp:attribute name="heading">Course ID:</jsp:attribute>
        <jsp:body>
          ${data.studentCourseDetailsPanel.courseId}
        </jsp:body>
      </courseDetails:displayDetails>

      <%-- Course Name --%>
      <courseDetails:displayDetails id="<%=Const.ParamsNames.COURSE_NAME%>">
        <jsp:attribute name="heading">Course Name:</jsp:attribute>
        <jsp:body>
          <c:out value="${data.studentCourseDetailsPanel.courseName}" />
        </jsp:body>
      </courseDetails:displayDetails>

      <%-- Instructors Names --%>
      <courseDetails:displayDetails id="<%=Const.ParamsNames.INSTRUCTOR_NAME%>">
        <jsp:attribute name="heading">Instructors:</jsp:attribute>
        <jsp:body>
          <courseDetails:displayInstructors/>
        </jsp:body>
      </courseDetails:displayDetails>

      <%-- Team Name --%>
      <courseDetails:displayDetails id="<%=Const.ParamsNames.TEAM_NAME%>">
        <jsp:attribute name="heading">Your team:</jsp:attribute>
        <jsp:body>
          <c:out value="${data.studentCourseDetailsPanel.studentTeam}" />
        </jsp:body>
      </courseDetails:displayDetails>

      <%-- Student Name --%>
      <courseDetails:displayDetails id="<%=Const.ParamsNames.STUDENT_NAME%>">
        <jsp:attribute name="heading">Your name:</jsp:attribute>
        <jsp:body>
          <c:out value="${data.studentCourseDetailsPanel.studentName}" />
        </jsp:body>
      </courseDetails:displayDetails>

      <%-- Student Email --%>
      <courseDetails:displayDetails id="<%=Const.ParamsNames.STUDENT_EMAIL%>">
        <jsp:attribute name="heading">Your e-mail:</jsp:attribute>
        <jsp:body>
          ${data.studentCourseDetailsPanel.studentEmail}
        </jsp:body>
      </courseDetails:displayDetails>
    </div>
  </div>
  <div class="form-horizontal">
    <%-- Student Teammates --%>
    <courseDetails:displayDetails id="<%=Const.ParamsNames.TEAMMATES%>">
      <jsp:attribute name="heading">Your teammates:</jsp:attribute>
      <jsp:body>
        <courseDetails:displayTeammates />
      </jsp:body>
    </courseDetails:displayDetails>
  </div>
  <br>
  <br>
  <br>
</ts:studentPage>
