<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page import="teammates.common.util.FrontEndLibrary" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/recovery" prefix="recovery" %>
<c:set var="jsIncludes">
  <script type="text/javascript" src="<%= FrontEndLibrary.MOMENT %>"></script>
  <script type="text/javascript" src="/data/moment-timezone-with-data-2013-2023.min.js"></script>
  <script type="text/javascript" src="/js/instructorRecovery.js"></script>
</c:set>

<ti:instructorPage title="Recycle Bin" jsIncludes="${jsIncludes}">
  <c:if test="${!data.usingAjax}">
    <recovery:loadCoursesTableByAjaxForm />
  </c:if>

  <t:statusMessage statusMessagesToUser="${data.statusMessagesToUser}"/>
  <br>

  <div id="coursesList" class="align-center">
    <c:if test="${data.usingAjax}">
      <recovery:recoveryCoursesTable recoveryCourses="${data.recoveryCourses}"/>
      <c:if test="${empty data.recoveryCourses.rows}">
        No records found.
      </c:if>

      <c:if test="${!empty data.recoveryCourses.rows}">
        <recovery:actionsForAllCourses />
      </c:if>
      <br>
      <br>
      <br>
    </c:if>
  </div>
</ti:instructorPage>
