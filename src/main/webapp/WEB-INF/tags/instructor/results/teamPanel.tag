<%@ tag description="instructorFeedbackResults - team panel containing participant panels, and optionally, statistics tables" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ tag import="teammates.common.util.Const" %>

<%@ taglib tagdir="/WEB-INF/tags/instructor/results" prefix="results" %>

<%@ attribute name="showAll" type="java.lang.Boolean" required="true" %>
<%@ attribute name="teamIndex" type="java.lang.Integer" required="true" %>
<%@ attribute name="shouldCollapsed" type="java.lang.Boolean" required="true" %>
<%@ attribute name="statisticsHeaderText" required="true"%>
<%@ attribute name="detailedResponsesHeaderText" required="true"%>
<%@ attribute name="teamName" required="true" %>
<%@ attribute name="statsTables" type="java.util.List" %>
<%@ attribute name="isTeamHasResponses" type="java.lang.Boolean" required="true" %>
<%@ attribute name="isDisplayingTeamStatistics" type="java.lang.Boolean" required="true" %>
<%@ attribute name="isDisplayingMissingParticipants" type="java.lang.Boolean" required="true" %>
<%@ attribute name="isSecondaryParticipantType" type="java.lang.Boolean" required="true" %>
<%@ attribute name="participantPanels" type="java.util.List" required="true" %>

<c:set var="groupByTeamEnabled" value = "${data.groupByTeam != null || data.groupByTeam == 'on'}"/>

<div class="panel panel-warning">
    <div class="panel-heading">
        <div class="inline panel-heading-text">
            <strong>${teamName}</strong>                        
        </div>
        <div class="pull-right">
            <c:if test="${!isDisplayingTeamStatistics}">
                <a class="btn btn-warning btn-xs" id="collapse-panels-button-team-${teamIndex}" data-toggle="tooltip" title="Collapse or expand all student panels. You can also click on the panel heading to toggle each one individually.">
                    ${ shouldCollapsed ? 'Expand' : 'Collapse'} Students
                </a>
                &nbsp;
            </c:if>
            <span class="glyphicon ${!shouldCollapsed ? 'glyphicon-chevron-up' : 'glyphicon-chevron-down'}"></span>
        </div>
    </div>
    
    <div class="panel-collapse collapse<c:if test="${!shouldCollapsed}"> in</c:if>">
        <div class="panel-body background-color-warning">
            <div class="resultStatistics">
                <c:if test="${isDisplayingTeamStatistics && isTeamHasResponses}">
                    <h3>${teamName} ${statisticsHeaderText}</h3>
                    <hr class="margin-top-0">
                    <c:choose>
                        <c:when test="${empty statsTables}">
                            <p class="text-color-gray"><i>No statistics available.</i></p>
                        </c:when>
                        <c:otherwise>
                            <c:forEach items="${statsTables}" var="statsTable">
                                <c:if test="${not empty statsTable.questionStatisticsTable}">
                                    <results:questionPanel showAll="${showAll}" questionPanel="${statsTable}" shouldCollapsed="${shouldCollapse}"/>
                                </c:if>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </c:if>
                <c:if test="${isDisplayingTeamStatistics && (isTeamHasResponses || isDisplayingMissingParticipants)}">
                    <c:if test="${isTeamHasResponses}">
                        <div class="row">
                            <div class="col-sm-9">
                                <h3>${teamName} ${detailedResponsesHeaderText}</h3>
                            </div>
                            <div class="col-sm-3 h3">
                                <a class="btn btn-warning btn-xs pull-right" id="collapse-panels-button-team-${teamIndex}" data-toggle="tooltip" title="Collapse or expand all student panels. You can also click on the panel heading to toggle each one individually.">
                                    ${shouldCollapsed ? 'Expand' : 'Collapse'} Students
                                </a>
                            </div>
                        </div>
                        <hr class="margin-top-0">
                    </c:if>
                </c:if>
            </div>
            <c:if test="${isTeamHasResponses || isDisplayingMissingParticipants}">
                <c:forEach items="${participantPanels}" var="participantPanel">
                    <results:participantPanel showAll="${showAll}" participantPanel="${participantPanel}" 
                            shouldCollapsed="${shouldCollapsed}" isSecondaryParticipantType="${isSecondaryParticipantType}"/>
                </c:forEach>
             </c:if>
            
        </div>
    </div>
</div>
