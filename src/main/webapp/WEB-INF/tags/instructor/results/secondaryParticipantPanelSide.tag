<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorFeedbackResults - side of the secondary participant panel" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ tag import="teammates.common.util.Const" %>

<%@ taglib tagdir="/WEB-INF/tags/instructor/results" prefix="results" %>

<%@ attribute name="secondaryParticipantPanelBody" type="teammates.ui.template.InstructorFeedbackResultsSecondaryParticipantPanelBody" required="true" %>
<%@ attribute name="primaryParticipantPanel" type="teammates.ui.template.InstructorFeedbackResultsGroupByParticipantPanel" required="true" %>

<div class="col-md-2">
  <div class="col-md-12 tablet-margin-10px tablet-no-padding">
    ${primaryParticipantPanel.giver ? 'To' : 'From'}:

    <c:choose>
      <c:when test="${not empty secondaryParticipantPanelBody.profilePictureLink}">
        <div class="tablet-bottom-align profile-pic-icon-hover inline-block" data-link="${secondaryParticipantPanelBody.profilePictureLink}">
          <strong>${fn:escapeXml(secondaryParticipantPanelBody.secondaryParticipantDisplayableName)}</strong>
          <img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
        </div>
      </c:when>
      <c:otherwise>
        <strong>${fn:escapeXml(secondaryParticipantPanelBody.secondaryParticipantDisplayableName)}</strong>
      </c:otherwise>
    </c:choose>

  </div>

  <div class="col-md-12 tablet-margin-10px tablet-no-padding text-muted small"><br class="hidden-xs hidden-sm">
    ${primaryParticipantPanel.giver ? 'From' : 'To' }:
    <c:choose>
      <c:when test="${primaryParticipantPanel.emailValid}">
        <div class="tablet-bottom-align profile-pic-icon-hover inline-block" data-link="${primaryParticipantPanel.profilePictureLink}">
          ${fn:escapeXml(primaryParticipantPanel.name)}
          <img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
        </div>
      </c:when>
      <c:otherwise>
        ${fn:escapeXml(primaryParticipantPanel.name)}
      </c:otherwise>
    </c:choose>
  </div>
  <c:if test="${not empty secondaryParticipantPanelBody.moderationButton}">
    <div class="col-md-12 margin-bottom-10px tablet-margin-10px tablet-no-padding">
      <results:moderationButton moderationButton="${secondaryParticipantPanelBody.moderationButton}"/>
    </div>
  </c:if>
</div>
