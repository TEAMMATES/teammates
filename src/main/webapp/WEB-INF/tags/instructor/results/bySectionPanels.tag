<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorFeedbackResults - by Giver > Question > Recipient or Recipient > Question > Giver" pageEncoding="UTF-8" %>
<%@ tag import="teammates.common.util.Const" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/results" prefix="results" %>

<%@ attribute name="isShowingAll" type="java.lang.Boolean" required="true" %>
<%@ attribute name="isGroupedByQuestion" type="java.lang.Boolean" required="true" %>
<%@ attribute name="isGroupedByTeam" type="java.lang.Boolean" required="true" %>

<br>
<c:set var="teamIndex" value="${0}"/>
<c:forEach items="${data.sectionPanels}" var="sectionPanel" varStatus="i">
  <results:sectionPanel isShowingAll="${isShowingAll}" sectionPanel="${sectionPanel.value}"
      sectionIndex="${i.index}" teamIndexOffset="${teamIndex}"
      courseId="${data.courseId}" feedbackSessionName="${data.feedbackSessionName}"
      isGroupedByQuestion="${isGroupedByQuestion}"
      isGroupedByTeam="${isGroupedByTeam}"/>
  <c:set var="teamIndex" value="${teamIndex + fn:length(sectionPanel.value.participantPanels)}"/>
</c:forEach>
