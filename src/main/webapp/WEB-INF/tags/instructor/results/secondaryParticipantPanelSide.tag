<%@ tag description="instructorFeedbackResults - side of the secondary participant panel" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ tag import="teammates.common.util.Const" %>

<%@ taglib tagdir="/WEB-INF/tags/instructor/results" prefix="results" %>

<%@ attribute name="secondaryParticipantPanelBody" type="teammates.ui.template.InstructorFeedbackResultsSecondaryParticipantPanelBody" required="true" %>
<%@ attribute name="primaryParticipantPanel" type="teammates.ui.template.InstructorFeedbackResultsGroupByParticipantPanel" required="true" %>

<div class="col-md-2">
    <div class="col-md-12">
        ${primaryParticipantPanel.giver ? 'To' : 'From'}: 
        
        <c:choose>
            <c:when test="${not empty secondaryParticipantPanelBody.profilePictureLink}">
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
        ${primaryParticipantPanel.giver ? 'From' : 'To' }: 
        <c:choose>
            <c:when test="${primaryParticipantPanel.emailValid}">
                <div class="middlealign profile-pic-icon-hover inline-block" data-link="${primaryParticipantPanel.profilePictureLink}">
                    ${primaryParticipantPanel.name}
                    <img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                </div>               
            </c:when>
            <c:otherwise>
                ${primaryParticipantPanel.name}
            </c:otherwise>
        </c:choose>
    </div>
    <c:if test="${not empty secondaryParticipantPanelBody.moderationButton}">
        <div class="col-md-12">
            <results:moderationButton moderationButton="${secondaryParticipantPanelBody.moderationButton}"/>
        </div>
    </c:if>
</div>