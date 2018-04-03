<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/home" prefix="home" %>
<c:set var="jsIncludes">
  <script type="text/javascript" src="/js/instructorHome.js"></script>
</c:set>
<ti:instructorPage title="Home" jsIncludes="${jsIncludes}">
  <home:search />
  <br>
  <t:statusMessage statusMessagesToUser="${data.statusMessagesToUser}" />
  <ti:remindParticularStudentsModal remindParticularStudentsLink="${data.remindParticularStudentsLink}" />
  <ti:resendPublishedEmailModal sessionResendPublishedEmailLink="${data.sessionResendPublishedEmailLink}" />
  <c:if test="${data.account.instructor}">
    <home:sort isSortButtonsDisabled="${data.sortingDisabled}"/>
    <br>
    <c:forEach items="${data.courseTables}" var="courseTable" varStatus="i">
      <home:coursePanel courseTable="${courseTable}" index="${i.index}" />
    </c:forEach>
    <ti:copyModal editCopyActionLink="${data.editCopyActionLink}" />
  </c:if>
</ti:instructorPage>
