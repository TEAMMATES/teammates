<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/admin" prefix="ta" %>
<%@ taglib tagdir="/WEB-INF/tags/admin/sessions" prefix="adminSessions" %>
<c:set var="jsIncludes">
  <script type="text/javascript" src="/js/adminSessions.js"></script>
</c:set>
<ta:adminPage title="Ongoing Sessions" jsIncludes="${jsIncludes}">
  <h1>
    <small>
      Total: ${data.totalOngoingSessions} &nbsp; &nbsp;
      Opened: ${data.totalOpenStatusSessions} &nbsp; &nbsp;
      Closed: ${data.totalClosedStatusSessions} &nbsp; &nbsp;
      Waiting To Open: ${data.totalWaitToOpenStatusSessions} &nbsp; &nbsp;
      Institutions: ${data.totalInstitutes} &nbsp; &nbsp;
      <br>
      ${data.rangeStartString}&nbsp;&nbsp;
      <span class="glyphicon glyphicon-resize-horizontal"></span>&nbsp;&nbsp;${data.rangeEndString}
      &nbsp;${data.timeZoneAsString}
    </small>
    <br>
    <a href="javascript:;" class="btn btn-info" id="btn-open-all-sections">Open All</a>
    <a href="javascript:;" class="btn btn-warning" id="btn-close-all-sections">Collapse All</a>
  </h1>
  <br>
  <adminSessions:filter filter="${data.filter}"/>
  <t:statusMessage statusMessagesToUser="${data.statusMessagesToUser}" />
  <c:forEach items="${data.institutionPanels}" var="institutionPanel" varStatus="i">
    <adminSessions:institutionPanel institutionPanel="${institutionPanel}" tableIndex="${i.count}" showAll="${data.showAll}" />
  </c:forEach>
  <a href="javascript:;" class="back-to-top-left"><span class="glyphicon glyphicon-arrow-up"></span>&nbsp;Top</a>
  <a href="javascript:;" class="back-to-top-right">Top&nbsp;<span class="glyphicon glyphicon-arrow-up"></span></a>
</ta:adminPage>
