<%@ tag description="instructorFeedbackResults - participant > participant > question" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ tag import="teammates.common.util.Const" %>

<%@ taglib tagdir="/WEB-INF/tags/instructor/results" prefix="results" %>

<%@ attribute name="groupByParticipantPanel" type="teammates.ui.template.InstructorFeedbackResultsGroupByParticipantPanel" required="true" %>
<%@ attribute name="shouldCollapsed" type="java.lang.Boolean" required="true" %>
<%@ attribute name="showAll" type="java.lang.Boolean" required="true" %>

<div class="panel ${not empty groupByParticipantPanel.secondaryParticipantPanels ? 'panel-primary' : 'panel-default'}">
    <div class="panel-heading">
        To: 
        <c:choose>
            <c:when test="${groupByParticipantPanel.emailValid}">
                <div class="middlealign profile-pic-icon-hover inline panel-heading-text" data-link="${groupByParticipantPanel.profilePictureLink}">
                    <strong>${groupByParticipantPanel.name}</strong>
                    <img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                    <a <c:if test="${not empty groupByParticipantPanel.secondaryParticipantPanels}">class="link-in-dark-bg"</c:if> href="mailto:${groupByParticipantPanel.participantIdentifier}">[${groupByParticipantPanel.participantIdentifier}]</a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="inline panel-heading-text">
                    <strong>${groupByParticipantPanel.name}</strong>
                </div>
            </c:otherwise>
        </c:choose>
        <span class="glyphicon ${!shouldCollapsed ? 'glyphicon-chevron-up' : 'glyphicon-chevron-down'} pull-right"></span>
    </div>
    <div class="panel-collapse collapse ${shouldCollapsed ? '' : 'in'}">
        <div class="panel-body">
            <c:choose>
                <c:when test="${not empty groupByParticipantPanel.secondaryParticipantPanels}">
                    <c:forEach items="${groupByParticipantPanel.secondaryParticipantPanels}" var="secondaryParticipantPanel" varStatus="i">
                        <results:secondaryParticipantPanel secondaryParticipantPanelBody="${secondaryParticipantPanel}" 
                                                           showAll="${showAll}" 
                                                           shouldCollapsed="${shouldCollapsed}"
                                                           primaryParticipantPanel="${groupByParticipantPanel}"
                                                           secondaryParticipantIndex="${i.index}"/>
                     </c:forEach>
                 </c:when>
                 <c:otherwise>
                    <i>There are no responses ${groupByParticipantPanel.giver? 'given' : 'received'} by this user</i>
                 </c:otherwise>
             </c:choose>
        </div>
    </div>
</div>
