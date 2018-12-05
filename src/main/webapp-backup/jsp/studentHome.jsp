<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/student" prefix="ts" %>
<%@ taglib tagdir="/WEB-INF/tags/student/home" prefix="home" %>
<%@ page import="teammates.common.util.Const" %>
<c:set var="jsIncludes">
  <script type="text/javascript" src="/js/studentHome.js"></script>
</c:set>
<ts:studentPage title="Student Home" jsIncludes="${jsIncludes}">
  <t:statusMessage statusMessagesToUser="${data.statusMessagesToUser}" />
  <br>
  <c:forEach items="${data.courseTables}" var="courseTable">
    <home:coursePanel courseTable="${courseTable}">
      <home:courseTable sessionRows="${courseTable.rows}" />
    </home:coursePanel>
    <br><br>
  </c:forEach>
</ts:studentPage>
