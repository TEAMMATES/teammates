<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/admin" prefix="ta" %>
<%@ taglib tagdir="/WEB-INF/tags/admin/accounts" prefix="accounts" %>

<c:set var="jsIncludes">
  <script type="text/javascript" src="/js/adminAccountDetails.js"></script>
</c:set>

<ta:adminPage title="Instructor Account Details" jsIncludes="${jsIncludes}">
  <accounts:accountDetailsForInstructorPanel accountInformation="${data.accountInformation}"/>
  <t:statusMessage statusMessagesToUser="${data.statusMessagesToUser}" />

  <div class="page-header">
    <h2>
      Instructor For <small class="course-count rightalign bold">${fn:length(data.instructorCourseListTable)} Courses</small>
    </h2>
  </div>

  <c:choose>
    <c:when test="${not empty data.instructorCourseListTable}">
      <accounts:courseListForInstructorTable instructorCourseListTable="${data.instructorCourseListTable}" />
    </c:when>
    <c:otherwise>
      <div class="alert alert-warning">
        <span class="glyphicon glyphicon-exclamation-sign"></span>
        No Courses found for this Account
      </div>
    </c:otherwise>
  </c:choose>

  <div class="page-header">
    <h2>
      Student For <small class="course-count rightalign bold">${fn:length(data.studentCourseListTable)} Courses</small>
    </h2>
  </div>

  <c:choose>
    <c:when test="${not empty data.studentCourseListTable}">
      <accounts:courseListForStudentTable studentCourseListTable="${data.studentCourseListTable}" />
    </c:when>
    <c:otherwise>
      <div class="alert alert-warning">
        <span class="glyphicon glyphicon-exclamation-sign"></span>
        This Account is not a Student
      </div>
    </c:otherwise>
  </c:choose>

  <br> <br> <br>

</ta:adminPage>
