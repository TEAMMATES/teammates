<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorFeedbackResults - team panel containing participant panels, and optionally, statistics tables" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ tag import="teammates.common.util.Const" %>

<%@ taglib tagdir="/WEB-INF/tags/instructor/results" prefix="results" %>

<%@ attribute name="isShowingAll" type="java.lang.Boolean" required="true" %>
<%@ attribute name="teamIndex" type="java.lang.Integer" required="true" %>
<%@ attribute name="statisticsHeaderText" required="true"%>
<%@ attribute name="detailedResponsesHeaderText" required="true"%>
<%@ attribute name="teamName" required="true" %>
<%@ attribute name="statsTables" type="java.util.List" %>
<%@ attribute name="isTeamHasResponses" type="java.lang.Boolean" required="true" %>
<%@ attribute name="isDisplayingTeamStatistics" type="java.lang.Boolean" required="true" %>
<%@ attribute name="isDisplayingMissingParticipants" type="java.lang.Boolean" required="true" %>
<%@ attribute name="isSecondaryParticipantType" type="java.lang.Boolean" required="true" %>
<%@ attribute name="participantPanels" type="java.util.List" required="true" %>

<div class="panel panel-warning">
  <div class="panel-heading">
    <div class="inline panel-heading-text">
      <strong>${fn:escapeXml(teamName)}</strong>
    </div>
    <div class="pull-right">
      <%-- If team statistics are displayed, then the "Collapse Students" button appears under the team statistics tables --%>
      <c:if test="${!isDisplayingTeamStatistics}">
        <a class="btn btn-warning btn-xs" id="collapse-panels-button-team-${teamIndex}" data-toggle="tooltip" title="Collapse or expand all student panels. You can also click on the panel heading to toggle each one individually.">
          Collapse Students
        </a>
        &nbsp;
      </c:if>
      <span class="glyphicon glyphicon-chevron-up"></span>
    </div>
  </div>

  <div class="panel-collapse collapse in">
    <div class="panel-body background-color-warning">
      <c:if test="${isDisplayingTeamStatistics}">
        <%-- Statistics Tables for entire team --%>
        <div class="resultStatistics">
          <c:if test="${isTeamHasResponses}">
            <h3>${fn:escapeXml(teamName)}${" "}${statisticsHeaderText}</h3>
            <hr class="margin-top-0">
            <c:choose>
              <%-- Not all questions have statistics, so we still need to test for the non-emptiness of statsTable --%>
              <c:when test="${empty statsTables}">
                <p class="text-color-gray">
                  <i>No statistics available.</i>
                </p>
              </c:when>
              <c:otherwise>
                <c:forEach items="${statsTables}" var="statsTable">
                  <results:questionPanel isShowingResponses="${isShowingAll}" questionPanel="${statsTable}"/>
                </c:forEach>
              </c:otherwise>
            </c:choose>
            <div class="row">
              <div class="col-sm-9">
                <h3>${fn:escapeXml(teamName)}${" "}${detailedResponsesHeaderText}</h3>
              </div>
              <div class="col-sm-3 h3">
                <a class="btn btn-warning btn-xs pull-right" id="collapse-panels-button-team-${teamIndex}" data-toggle="tooltip" title="Collapse or expand all student panels. You can also click on the panel heading to toggle each one individually.">
                  Collapse Students
                </a>
              </div>
            </div>
            <hr class="margin-top-0">
          </c:if>
        </div>
      </c:if>

      <c:if test="${isTeamHasResponses || isDisplayingMissingParticipants}">
        <c:forEach items="${participantPanels}" var="participantPanel">
          <results:participantPanel isShowingAll="${isShowingAll}" participantPanel="${participantPanel}"
              isSecondaryParticipantType="${isSecondaryParticipantType}"/>
        </c:forEach>
      </c:if>

    </div>
  </div>
</div>
