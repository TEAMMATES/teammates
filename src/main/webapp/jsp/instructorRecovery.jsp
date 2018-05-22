<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/recovery" prefix="recovery" %>
<c:set var="jsIncludes">
  <script type="text/javascript" src="/js/instructorRecovery.js"></script>
</c:set>
<ti:instructorPage title="Recycle Bin" jsIncludes="${jsIncludes}">
  <t:statusMessage statusMessagesToUser="${data.statusMessagesToUser}" />
  <c:if test="${data.account.instructor}">
    <c:forEach items="${data.courseTables}" var="courseTable" varStatus="i">
      <recovery:coursePanel courseTable="${courseTable}" index="${i.index}" />
    </c:forEach>
    <ti:copyModal editCopyActionLink="${data.editCopyActionLink}" />
  </c:if>
</ti:instructorPage>
