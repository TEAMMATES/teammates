<%@ tag description="instructorFeedbackResults - by question" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ tag import="teammates.common.util.Const" %>

<%@ taglib tagdir="/WEB-INF/tags/instructor/results" prefix="results" %>

<%@ attribute name="groupByQuestionPanel" type="teammates.ui.template.InstructorFeedbackResultsGroupByQuestionPanel" required="true" %>
<%@ attribute name="shouldCollapsed" type="java.lang.Boolean" required="true" %>
<%@ attribute name="showAll" type="java.lang.Boolean" required="true" %>


<div class="panel panel-primary">
    <div class="panel-heading">
        From: 
            <c:choose>
                <c:when test="${groupByQuestionPanel.emailValid}">
                    <div class="middlealign profile-pic-icon-hover inline panel-heading-text" data-link="${groupByQuestionPanel.profilePictureLink}">
                        <strong>${participantIdentifier}</strong>
                        <img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                        <a class="link-in-dark-bg" href="mailTo:${participantIdentifier}> " ${groupByQuestionPanel.mailtoStyle}>[${participantIdentifier}]</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="inline panel-heading-text">
                        <strong>${participantIdentifier}</strong>
                        <a class="link-in-dark-bg" href="mailTo:${participantIdentifier}" ${mailtoStyleAttr}>[${participantIdentifier}]</a>
                    </div>
                </c:otherwise>
            </c:choose>

        <div class="pull-right">
            <c:if test="${groupByQuestionPanel.moderationButtonDisplayed}">
                <results:moderationsButton moderationButton="${groupByQuestionPanel.moderationButton}" />
            </c:if>
            &nbsp;
            <div class="display-icon" style="display:inline;">
                <span class='glyphicon ${!shouldCollapsed ? "glyphicon-chevron-up" : "glyphicon-chevron-down"} pull-right'></span>
            </div>                
        </div>
    </div>
    <div class='panel-collapse collapse ${shouldCollapsed ? "" : "in"}'>
        <div class="panel-body">
            <c:forEach items="${groupByQuestionPanel.questionTables}" var="questionTable">
                <results:questionPanel showAll="${showAll}" questionPanel="${questionTable}" shouldCollapsed="${shouldCollapsed}"></results:questionPanel>        
            </c:forEach>
        </div>
    </div>
</div>