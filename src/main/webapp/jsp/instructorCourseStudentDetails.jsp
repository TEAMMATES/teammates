<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/courseStudentDetails" prefix="csd" %>
<c:set var="jsIncludes">
  <script type="text/javascript" src="/js/instructorCourseStudentDetails.js"></script>
</c:set>
<ti:instructorPage title="${fn:escapeXml(data.studentInfoTable.name)}" jsIncludes="${jsIncludes}">
  <t:statusMessage statusMessagesToUser="${data.statusMessagesToUser}" />
  <c:if test="${not empty data.studentProfile}">
    <csd:studentProfile student="${data.studentProfile}"/>
  </c:if>
  <csd:studentInformationTable studentInfoTable="${data.studentInfoTable}" />
  <c:if test="${not empty data.studentProfile}">
    <ti:moreInfo student="${data.studentProfile}" />
  </c:if>
</ti:instructorPage>
