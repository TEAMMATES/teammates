<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorFeedbackResults - participant > participant > question" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ tag import="teammates.common.util.Const" %>

<%@ taglib tagdir="/WEB-INF/tags/instructor/results" prefix="results" %>

<%@ attribute name="groupByParticipantPanel" type="teammates.ui.template.InstructorFeedbackResultsGroupByParticipantPanel" required="true" %>

<div class="panel ${not empty groupByParticipantPanel.secondaryParticipantPanels ? 'panel-primary' : 'panel-default'}">
  <div class="panel-heading">
    ${groupByParticipantPanel.giver ? 'From' : 'To'}:
    <c:choose>
      <c:when test="${groupByParticipantPanel.emailValid}">
        <div class="middlealign profile-pic-icon-hover inline panel-heading-text" data-link="${groupByParticipantPanel.profilePictureLink}">
          <strong>${fn:escapeXml(groupByParticipantPanel.name)}</strong>
          <img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
          <a <c:if test="${not empty groupByParticipantPanel.secondaryParticipantPanels}">class="link-in-dark-bg"</c:if> href="mailto:${groupByParticipantPanel.participantIdentifier}">[${groupByParticipantPanel.participantIdentifier}]</a>
        </div>
      </c:when>
      <c:otherwise>
        <div class="inline panel-heading-text">
          <strong>${fn:escapeXml(groupByParticipantPanel.name)}</strong>
        </div>
      </c:otherwise>
    </c:choose>
    <div class="pull-right">
      <c:if test="${not empty groupByParticipantPanel.moderationButton}">
        <results:moderationButton moderationButton="${groupByParticipantPanel.moderationButton}" />
      </c:if>
      &nbsp;
      <div class="display-icon" style="display:inline;">
        <span class='glyphicon glyphicon-chevron-up pull-right'></span>
      </div>
    </div>
  </div>
  <div class="panel-collapse collapse in">
    <div class="panel-body">
      <c:choose>
        <c:when test="${not empty groupByParticipantPanel.secondaryParticipantPanels}">
          <c:forEach items="${groupByParticipantPanel.secondaryParticipantPanels}" var="secondaryParticipantPanel" varStatus="i">
            <results:secondaryParticipantPanel secondaryParticipantPanelBody="${secondaryParticipantPanel}"
                primaryParticipantPanel="${groupByParticipantPanel}"
                secondaryParticipantIndex="${i.index}"/>
          </c:forEach>
        </c:when>
        <c:otherwise>
          <i>There are no responses ${groupByParticipantPanel.giver? 'given' : 'received'} by this user or you may not have the permission to see the response</i>
        </c:otherwise>
      </c:choose>
    </div>
  </div>
</div>
