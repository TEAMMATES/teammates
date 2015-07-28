<%@ tag description="instructorFeedbackResults - panel body inside participant panels" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ tag import="teammates.common.util.Const" %>

<%@ taglib tagdir="/WEB-INF/tags/instructor/results" prefix="results" %>

<%@ attribute name="secondaryParticipantPanelBody" type="teammates.ui.template.InstructorFeedbackResultsSecondaryParticipantPanelBody" required="true" %>
<%@ attribute name="shouldCollapsed" type="java.lang.Boolean" required="true" %>
<%@ attribute name="showAll" type="java.lang.Boolean" required="true" %>
<%@ attribute name="primaryParticipantPanel" type="teammates.ui.template.InstructorFeedbackResultsGroupByParticipantPanel" required="true" %>
<%@ attribute name="secondaryParticipantIndex" type="java.lang.Integer" required="true" %>



<div class="row ${secondaryParticipantIndex == 0 ? '': 'border-top-gray'}">
    <%-- Side of the panel --%>
    <div class="col-md-2">
        <div class="col-md-12">
            ${primaryParticipantPanel.giver? 'To' : 'From'}: 
            
            <c:choose>
                <c:when test="${secondaryParticipantPanelBody.emailValid}">
                    <div class="middlealign profile-pic-icon-hover inline-block" data-link="${secondaryParticipantPanelBody.profilePictureLink}">
                        <strong>${secondaryParticipantPanelBody.secondaryParticipantDisplayableName}</strong>
                        <img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                    </div>
                </c:when>
                <c:otherwise>
                    <strong>${secondaryParticipantPanelBody.secondaryParticipantDisplayableName}</strong>
                </c:otherwise>
            </c:choose>
                
        </div>
        
        <div class="col-md-12 text-muted small"><br>
            To:
            
            
            <c:choose>
                <c:when test="${primaryParticipantPanel.emailValid}">
                    <div class="middlealign profile-pic-icon-hover inline-block" data-link="${primaryParticipantPanel.profilePictureLink}">
                        <strong>${primaryParticipantPanel.name}</strong>
                        <img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                    </div>               
                </c:when>
                <c:otherwise>
                    <strong>${primaryParticipantPanel.name}</strong>
                </c:otherwise>
            </c:choose>
        </div>
        &nbsp;
        <div class="col-md-12">
            <c:if test="${secondaryParticipantPanelBody.moderationButtonDisplayed}">
                <results:moderationButton moderationButton="${secondaryParticipantPanelBody.moderationButton}"/>
            </c:if>
        </div>
        &nbsp;
    </div>
    <%-- End of Side of the panel --%>
    
    <div class="col-md-10">
        <c:set var="qnIndx" value="${1}"/>
        <c:forEach var="responsePanel" items="${secondaryParticipantPanelBody.responsePanels}">
            <results:responsePanel responsePanel="${responsePanel}" showAll="${showAll}" shouldCollapsed="${shouldCollapse}"/>
        </c:forEach>
    </div>
</div>
