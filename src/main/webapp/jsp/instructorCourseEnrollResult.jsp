<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page import="teammates.common.util.Const" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>

<c:set var="jsIncludes">
  <script type="text/javascript" src="/js/instructorCourseEnrollResult.js"></script>
</c:set>

<c:set var="COURSE_ID">
  <%=Const.ParamsNames.COURSE_ID%>
</c:set>

<c:set var="STUDENTS_ENROLLMENT_INFO">
  <%=Const.ParamsNames.STUDENTS_ENROLLMENT_INFO%>
</c:set>

<ti:instructorPage title="Enrollment Results for ${data.courseId}" jsIncludes="${jsIncludes}">
  <form name='goBack' action="${data.instructorCourseEnrollLink}" method="post" role="form">
    <div class = "alert alert-success">
      Enrollment Successful. Summary given below. Click <a id="edit_enroll" href="javascript:;">here</a> to do further changes to the student list.
      <input type="hidden" name="${COURSE_ID}" value="${data.courseId}">
      <input type="hidden" name="${STUDENTS_ENROLLMENT_INFO}" value="${fn:escapeXml(data.enrollStudents)}">
    </div>
  </form>

  <c:forEach items="${data.enrollResultPanelList}" var="enrollResultPanel">
    <c:if test="${not empty enrollResultPanel.studentList}">
      <div class="panel ${enrollResultPanel.panelClass}">
        <div class="panel-heading">
          ${enrollResultPanel.messageForEnrollmentStatus}
        </div>
        <table class="table table-striped table-bordered">
          <tr>
            <c:if test="${data.hasSection}">
              <th>Section</th>
            </c:if>
            <th>Team</th>
            <th>Student Name</th>
            <th>E-mail address</th>
            <th>Comments</th>
          </tr>
          <c:forEach items="${enrollResultPanel.studentList}" var="student">
            <tr>
              <c:if test="${data.hasSection}">
                <td><c:out value="${student.section}"/></td>
              </c:if>
              <td><c:out value="${student.team}"/></td>
              <td><c:out value="${student.name}"/></td>
              <td><c:out value="${student.email}"/></td>
              <td><c:out value="${student.comments}"/></td>
            </tr>
          </c:forEach>
        </table>
      </div>
      <br>
      <br>
    </c:if>
  </c:forEach>
  <div id="instructorCourseEnrollmentButtons"></div>
</ti:instructorPage>
