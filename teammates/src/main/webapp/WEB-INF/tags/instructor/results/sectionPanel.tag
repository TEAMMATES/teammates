<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorFeedbackResults - section Panel that nests team panels/participant panels" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ tag import="teammates.common.util.Config" %>
<%@ tag import="teammates.common.util.Const" %>

<%@ taglib tagdir="/WEB-INF/tags/instructor/results" prefix="results" %>

<%@ attribute name="isShowingAll" type="java.lang.Boolean" required="true" %>
<%@ attribute name="sectionIndex" type="java.lang.Integer" required="true" %>
<%@ attribute name="teamIndexOffset" type="java.lang.Integer" required="true" %>
<%@ attribute name="isGroupedByQuestion" type="java.lang.Boolean" required="true" %>
<%@ attribute name="sectionPanel" type="teammates.ui.template.InstructorFeedbackResultsSectionPanel" required="true" %>
<%@ attribute name="courseId" required="true" %>
<%@ attribute name="feedbackSessionName" required="true" %>
<%@ attribute name="isGroupedByTeam" type="java.lang.Boolean" required="true" %>

<div class="panel ${sectionPanel.panelClass}">
  <c:choose>
    <c:when test="${!sectionPanel.loadSectionResponsesByAjax}">
      <div class="panel-heading">
        <div class="row">
          <div class="col-sm-9 panel-heading-text">
            <strong>${fn:escapeXml(sectionPanel.sectionNameForDisplay)}</strong>
          </div>
          <div class="col-sm-3">
            <div class="pull-right">
              <a class="btn btn-success btn-xs collapse" id="collapse-panels-button-section-${sectionIndex}" data-toggle="tooltip" title="Collapse or expand all ${isGroupedByTeam? 'team' : 'student'} panels. You can also click on the panel heading to toggle each one individually.">
                Collapse ${isGroupedByTeam ? 'Teams' : 'Students'}
              </a>
              &nbsp;
              <div class="display-icon" style="display:inline;">
                <span class="glyphicon glyphicon-chevron-up"></span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </c:when>
    <c:otherwise>
      <div class="panel-heading ajax_auto">
        <div class="row">
          <div class="col-sm-9 panel-heading-text">
            <strong>${fn:escapeXml(sectionPanel.sectionNameForDisplay)}</strong>
          </div>
          <div class="col-sm-3">
            <div class="pull-right">
              <a class="btn btn-success btn-xs collapse" id="collapse-panels-button-section-${sectionIndex}" data-toggle="tooltip" title="Collapse or expand all ${isGroupedByTeam? 'team' : 'student'} panels. You can also click on the panel heading to toggle each one individually.">
                Collapse ${isGroupedByTeam ? 'Teams' : 'Students'}
              </a>
              &nbsp;
              <div class="display-icon" style="display:inline;">
                <span class="glyphicon glyphicon-chevron-down"></span>
              </div>
            </div>
          </div>
        </div>

        <form style="display:none;" id="seeMore-${sectionIndex}" class="seeMoreForm-${sectionIndex}" action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE%>">
          <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="${courseId}">
          <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>" value="${feedbackSessionName}">
          <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTION%>" value="${fn:escapeXml(sectionPanel.sectionName)}">
          <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="${data.account.googleId}">
          <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYTEAM%>" value="${data.groupByTeam}">
          <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE%>" value="${data.sortType}">
          <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_SHOWSTATS%>" value="on" id="showStats-${sectionIndex}">
          <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_INDICATE_MISSING_RESPONSES%>" value="${data.missingResponsesShown}">
          <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_MAIN_INDEX%>" value="-1" id="mainIndex-${sectionIndex}">
        </form>
      </div>
    </c:otherwise>
  </c:choose>

  <div class="panel-collapse collapse<c:if test="${!sectionPanel.loadSectionResponsesByAjax}"> in</c:if>">
    <div class="panel-body" id="sectionBody-${sectionIndex}">
      <c:set var="teamIndex" value="${teamIndexOffset}"/>
      <c:choose>
        <c:when test="${!sectionPanel.ableToLoadResponses}">
          Sorry, we could not retrieve results.
          Please try again in a few minutes. If you continue to see this message, it could be because the report you are trying to display contains too much data to display in one page. e.g. more than 2,500 entries.
          <ul><li>If that is the case, you can still use the 'By question' report to view responses. You can also download the results as a spreadsheet. If you would like to see the responses in other formats (e.g. 'Group by - Giver'), you can try to divide the course into smaller sections so that we can display responses one section at a time.</li>
          <li>If you believe the report you are trying to view is unlikely to have more than 2,500 entries, please contact us at <a href='mailto:<%= Config.SUPPORT_EMAIL %>'><%= Config.SUPPORT_EMAIL %></a> so that we can investigate.</li></ul>
        </c:when>
        <c:when test="${isGroupedByTeam}">
          <c:forEach var="teamPanel" items="${sectionPanel.participantPanels}">
            <results:teamPanel teamName="${teamPanel.key}" teamIndex="${teamIndex}"
                isShowingAll="${isShowingAll}"
                statsTables="${sectionPanel.teamStatisticsTable[teamPanel.key]}"
                detailedResponsesHeaderText="${sectionPanel.detailedResponsesHeaderText}"
                statisticsHeaderText="${sectionPanel.statisticsHeaderText}"
                isTeamHasResponses="${sectionPanel.isTeamWithResponses[teamPanel.key]}"
                isDisplayingTeamStatistics="${sectionPanel.isDisplayingTeamStatistics[teamPanel.key]}"
                isDisplayingMissingParticipants="${sectionPanel.displayingMissingParticipants}"
                participantPanels="${teamPanel.value}"
                isSecondaryParticipantType="${!isGroupedByQuestion}"/>
            <c:set var="teamIndex" value="${teamIndex + 1}"/>
          </c:forEach>
        </c:when>
        <c:otherwise>
          <c:forEach var="participantPanel" items="${sectionPanel.participantPanelsInSortedOrder}">
            <results:participantPanel isShowingAll="${isShowingAll}"
                participantPanel="${participantPanel}"
                isSecondaryParticipantType="${!isGroupedByQuestion}"/>
          </c:forEach>
        </c:otherwise>
      </c:choose>
    </div>
  </div>
</div>
