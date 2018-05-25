<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/recovery" prefix="recovery" %>
<ti:instructorPage title="Recycle Bin">
  <t:statusMessage statusMessagesToUser="${data.statusMessagesToUser}" />
  <br>

  <div id="recoveryCoursesList" class="align-center">
    <c:if test="${data.usingAjax}">
      <recovery:recoveryCoursesTable recoveryCourses="${data.recoveryCourses}"/>
      <br>
      <br>
      <c:if test="${empty data.recoveryCourses.rows}">
        No records found. <br>
        <br>
      </c:if>
    </c:if>
  </div>
</ti:instructorPage>
