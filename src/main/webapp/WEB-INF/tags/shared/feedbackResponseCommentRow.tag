<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="Feedback Response Comment" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags/shared" prefix="shared" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="frc" type="teammates.ui.template.FeedbackResponseCommentRow" required="true" %>
<%@ attribute name="firstIndex" %>
<%@ attribute name="secondIndex" %>
<%@ attribute name="thirdIndex" %>
<%@ attribute name="fourthIndex" %>
<%@ attribute name="frcIndex" %>
<%@ attribute name="viewType" %>
<%@ attribute name="isSessionOpenForSubmission" type="java.lang.Boolean"%>
<%@ attribute name="moderatedPersonEmail" %>
<%@ attribute name="submittable" %>
<%@ attribute name="isPreview" %>
<%@ attribute name="isModeration" %>

<c:choose>
  <c:when test="${not empty firstIndex && not empty secondIndex && not empty thirdIndex && not empty fourthIndex && not empty frcIndex}">
    <c:set var="divId" value="${fourthIndex}-${firstIndex}-${secondIndex}-${thirdIndex}-${frcIndex}" />
  </c:when>
  <c:when test="${not empty firstIndex && not empty secondIndex && not empty thirdIndex && not empty frcIndex && not empty viewType}">
    <c:set var="divId" value="${viewType}-${firstIndex}-${secondIndex}-${thirdIndex}-${frcIndex}" />
  </c:when>
  <c:when test="${not empty firstIndex && not empty secondIndex && not empty thirdIndex && not empty frcIndex}">
    <c:set var="divId" value="${firstIndex}-${secondIndex}-${thirdIndex}-${frcIndex}" />
  </c:when>
  <c:otherwise>
    <c:set var="divId" value="${frc.commentId}" />
  </c:otherwise>
</c:choose>
<c:choose>
  <c:when test="${frc.commentGiverType eq 'instructor'}">
    <c:set var="submitLink"><%= Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESPONSE_COMMENT_ADD %>
    </c:set>
    <c:set var="deleteLink"><%= Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESPONSE_COMMENT_DELETE %>
    </c:set>
  </c:when>
  <c:otherwise>
    <c:set var="deleteLink"><%= Const.ActionURIs.FEEDBACK_PARTICIPANT_FEEDBACK_RESPONSE_COMMENT_DELETE %>
    </c:set>
  </c:otherwise>
</c:choose>
<li class="list-group-item list-group-item-warning" id="responseCommentRow-${divId}">
  <div id="commentBar-${divId}" class="row">
    <div class="col-xs-10">
    <span class="text-muted">
      From: ${fn:escapeXml(frc.commentGiverName)} [${frc.createdAt}] ${frc.editedAt}
    </span>
    <span class="glyphicon glyphicon-eye-open"
        data-toggle="tooltip"
        data-placement="top"
        style="margin-left: 5px;"
        title="This response comment is visible to ${frc.visibilityIconString}"></span>
    </div>
    <div class="col-xs-2">
      <c:if test="${frc.editDeleteEnabled}">
        <c:choose>
          <c:when test="${frc.commentFromFeedbackParticipant}">
            <div class="responseCommentDeleteForm pull-right float-right clearfix">
          </c:when>
          <c:otherwise>
            <form class="responseCommentDeleteForm pull-right">
          </c:otherwise>
        </c:choose>
          <a href="${frc.editDeleteEnabled ? deleteLink : 'javascript:;'}"
              type="button"
              id="commentdelete-${divId}"
              class="btn btn-default btn-xs icon-button<c:if test="${not frc.editDeleteEnabled}"> disabled</c:if>"
              data-toggle="tooltip"
              data-placement="top"
              title="<%= Const.Tooltips.COMMENT_DELETE %>"
             <c:if test="${not frc.editDeleteEnabled}">disabled</c:if>
             <c:if test="${frc.commentFromFeedbackParticipant and (isPreview or (not submittable))}">disabled style="background: #66727A;" </c:if>>
            <span class="glyphicon glyphicon-trash glyphicon-primary"></span>
          </a>
          <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_SESSION_INDEX %>" value="${firstIndex}">
          <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_RESPONSE_ID %>" value="${frc.feedbackResponseId}">
          <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID %>" value="${frc.commentId}">
          <input type="hidden" name="<%= Const.ParamsNames.COURSE_ID %>" value="${frc.courseId}">
          <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="${frc.feedbackSessionName}">
          <input type="hidden" name="<%= Const.ParamsNames.USER_ID %>" value="${data.account.googleId}">
          <input type="hidden" name="<%= Const.ParamsNames.SESSION_TOKEN %>" value="${data.sessionToken}">
          <c:if test="${isModeration}">
            <input name="<%= Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON %>" value="${moderatedPersonEmail}" type="hidden">
          </c:if>
        <c:choose>
          <c:when test="${frc.commentFromFeedbackParticipant}">
            </div>
          </c:when>
          <c:otherwise>
            </form>
          </c:otherwise>
        </c:choose>
        <a type="button"
           id="commentedit-${divId}"
            <c:choose>
              <c:when test="${not empty firstIndex && not empty secondIndex && not empty thirdIndex && not empty frcIndex}">
                class="btn btn-default btn-xs icon-button pull-right show-frc-edit-form<c:if test="${not frc.editDeleteEnabled}"> disabled</c:if>"
                data-recipientindex="${firstIndex}" data-giverindex="${secondIndex}"
                data-qnindex="${thirdIndex}"
                <c:if test="${not empty frcIndex}">data-frcindex="${frcIndex}"</c:if>
                <c:if test="${not empty fourthIndex}">data-sectionindex="${fourthIndex}"</c:if>
                <c:if test="${not empty viewType}">data-viewtype="${viewType}"</c:if>
                <c:if test="${not isSessionOpenForSubmission && frc.commentFromFeedbackParticipant}">disabled</c:if>
              </c:when>
              <c:otherwise>
                class="btn btn-default btn-xs icon-button pull-right<c:if test="${not frc.editDeleteEnabled}"> disabled</c:if>"
              </c:otherwise>
            </c:choose>
            data-toggle="tooltip"
            data-placement="top"
            title="<%= Const.Tooltips.COMMENT_EDIT %>">
          <span class="glyphicon glyphicon-pencil glyphicon-primary"></span>
        </a>
      </c:if>
    </div>
  </div>
  <%-- Do not add whitespace between the opening and closing tags --%>
  <div id="plainCommentText-${divId}" style="margin-left: 15px;">${frc.commentText}</div>
  <c:if test="${frc.editDeleteEnabled}">
    <c:if test="${frc.commentGiverType eq 'instructor'}">
      <c:set var="submitLink"><%= Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESPONSE_COMMENT_EDIT %>
      </c:set>
    </c:if>
    <c:set var="textAreaId"><%= Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT %></c:set>
    <shared:feedbackResponseCommentForm fsIndex="${firstIndex}"
        secondIndex="${secondIndex}"
        thirdIndex="${thirdIndex}"
        fourthIndex="${fourthIndex}"
        frcIndex="${frcIndex}"
        frc="${frc}"
        viewType = "${viewType}"
        divId="${divId}"
        formType="Edit"
        textAreaId="${textAreaId}"
        submitLink="${submitLink}"
        buttonText="Save"
        isModeration="${isModeration}"
        moderatedPersonEmail="${moderatedPersonEmail}"/>
  </c:if>
</li>
