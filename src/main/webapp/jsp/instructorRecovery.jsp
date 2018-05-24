<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/course" prefix="course" %>
<c:set var="jsIncludes">
  <script type="text/javascript" src="/js/instructorRecovery.js"></script>
</c:set>
<ti:instructorPage title="Recycle Bin" jsIncludes="${jsIncludes}">
  <t:statusMessage statusMessagesToUser="${data.statusMessagesToUser}" />
  <br>

  <div id="recoveryCoursesList" class="align-center">
    <c:if test="${data.usingAjax}">
      <course:activeCoursesTable activeCourses="${data.activeCourses}"/>
      <br>
      <br>
      <c:if test="${empty data.activeCourses.rows}">
        No records found. <br>
        <br>
      </c:if>
    </c:if>
  </div>
</ti:instructorPage>
