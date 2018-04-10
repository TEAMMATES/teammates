<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page import="teammates.common.util.FrontEndLibrary" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/course" prefix="course" %>
<c:set var="jsIncludes">
  <script type="text/javascript" src="<%= FrontEndLibrary.MOMENT %>"></script>
  <script type="text/javascript" src="/data/moment-timezone-with-data-2013-2023.min.js"></script>
  <script type="text/javascript" src="/js/instructorCourses.js"></script>
</c:set>

<ti:instructorPage title="Add New Course" jsIncludes="${jsIncludes}">
  <c:if test="${!data.usingAjax}">
    <course:addCoursePanel courseIdToShow="${data.courseIdToShow}"
        courseNameToShow="${data.courseNameToShow}"
        googleId="${data.account.googleId}"
        sessionToken="${data.sessionToken}"/>
    <course:loadCoursesTableByAjaxForm />
  </c:if>

  <br>
  <t:statusMessage statusMessagesToUser="${data.statusMessagesToUser}"/>
  <br>

  <div id="coursesList" class="align-center">
    <c:if test="${data.usingAjax}">
      <course:activeCoursesTable activeCourses="${data.activeCourses}"/>
      <br>
      <br>
      <c:if test="${empty data.activeCourses.rows}">
        No records found. <br>
        <br>
      </c:if>
      <br>
      <br>

      <c:if test="${not empty data.archivedCourses.rows}">
        <course:archivedCoursesTable archivedCourses="${data.archivedCourses}"
            activeCourses="${data.activeCourses}"/>
        <br>
        <br>
        <br>
        <br>
      </c:if>
    </c:if>
  </div>
</ti:instructorPage>
