<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorFeedbackResults - participant > question > participant" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ tag import="teammates.common.util.Const" %>

<%@ taglib tagdir="/WEB-INF/tags/instructor/results" prefix="results" %>

<%@ attribute name="groupByQuestionPanel" type="teammates.ui.template.InstructorFeedbackResultsGroupByQuestionPanel" required="true" %>
<%@ attribute name="isShowingAll" type="java.lang.Boolean" required="true" %>

<div class="panel ${not empty groupByQuestionPanel.questionTables ? 'panel-primary' : 'panel-default'}">
  <div class="panel-heading">
    ${groupByQuestionPanel.giver? 'From:' : 'To:'}
    <c:choose>
      <c:when test="${groupByQuestionPanel.emailValid}">
        <div class="middlealign profile-pic-icon-hover inline panel-heading-text" data-link="${groupByQuestionPanel.profilePictureLink}">
          <strong>${fn:escapeXml(groupByQuestionPanel.name)}</strong>
          <img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
          <a <c:if test="${not empty groupByQuestionPanel.questionTables}">class="link-in-dark-bg"</c:if> href="mailto:${groupByQuestionPanel.participantIdentifier}">[${groupByQuestionPanel.participantIdentifier}]</a>
        </div>
      </c:when>
      <c:otherwise>
        <div class="inline panel-heading-text">
          <strong>${fn:escapeXml(groupByQuestionPanel.name)}</strong>
        </div>
      </c:otherwise>
    </c:choose>

    <div class="pull-right">
      <c:if test="${not empty groupByQuestionPanel.moderationButton}">
        <results:moderationButton moderationButton="${groupByQuestionPanel.moderationButton}" />
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
        <c:when test="${not empty groupByQuestionPanel.questionTables}">
          <c:forEach items="${groupByQuestionPanel.questionTables}" var="questionTable">
            <results:questionPanel isShowingResponses="${isShowingAll}" questionPanel="${questionTable}"/>
          </c:forEach>
        </c:when>
        <c:otherwise>
          <i>There are no responses ${groupByQuestionPanel.giver? 'given' : 'received'} by this user or you may not have the permission to see the response</i>
        </c:otherwise>
      </c:choose>
    </div>
  </div>
</div>
