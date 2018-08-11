<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="Feedback Response Comment" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags/shared" prefix="shared" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="frc" type="teammates.ui.template.FeedbackResponseCommentRow" required="true" %>
<%@ attribute name="responseIndex" type="java.lang.Integer" %>
<%@ attribute name="qnIndex" type="java.lang.Integer" %>
<%@ attribute name="moderatedPersonEmail" %>
<%@ attribute name="isSubmittable" type="java.lang.Boolean" %>
<%@ attribute name="isPreview" type="java.lang.Boolean" %>
<%@ attribute name="isModeration" type="java.lang.Boolean" %>

<c:set var="divId" value="${qnIndex}-${responseIndex}" />
<c:set var="deleteLink"><%= Const.ActionURIs.FEEDBACK_PARTICIPANT_FEEDBACK_RESPONSE_COMMENT_DELETE %></c:set>

<li class="list-group-item list-group-item-warning" <c:if test="${not empty qnIndex && not empty responseIndex}">id="responseCommentRow-${divId}"</c:if>>
  <div <c:if test="${not empty qnIndex && not empty responseIndex}">id="commentBar-${divId}"</c:if> class="row">
    <div class="col-xs-10">
    <span class="text-muted">
          Comment by response giver.
    </span>
      <span class="glyphicon glyphicon-eye-open"
            data-toggle="tooltip"
            data-placement="top"
            style="margin-left: 5px;"
            title="This response comment is visible to ${frc.visibilityIconString}"></span>
    </div>
    <div class="col-xs-2">
      <c:if test="${frc.editDeleteEnabled}">
        <div class="responseCommentDeleteForm pull-right float-right clearfix">
        <a href="${deleteLink}"
           type="button"
           id="commentdelete-${divId}"
           class="btn btn-default btn-xs icon-button
           <c:if test="${isPreview or (not isSubmittable)}">disabled</c:if>"
           data-toggle="tooltip"
           data-placement="top"
           data-responseindex="${responseIndex}"
           data-qnindex="${qnIndex}"
           title="<%= Const.Tooltips.COMMENT_DELETE %>">
          <span class="glyphicon glyphicon-trash glyphicon-primary"></span>
        </a>
        <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_RESPONSE_ID %>" value="${fn:escapeXml(frc.feedbackResponseId)}">
        <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID %>" value="${frc.commentId}">
        <input type="hidden" name="<%= Const.ParamsNames.COURSE_ID %>" value="${frc.courseId}">
        <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="${frc.feedbackSessionName}">
        <input type="hidden" name="<%= Const.ParamsNames.USER_ID %>" value="${data.account.googleId}">
        <input type="hidden" name="<%= Const.ParamsNames.SESSION_TOKEN %>" value="${data.sessionToken}">
        <c:if test="${isModeration}">
          <input name="<%= Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON %>" value="${moderatedPersonEmail}" type="hidden">
        </c:if>
      </div>
        <a type="button"
           id="commentedit-${divId}"
                class="btn btn-default btn-xs icon-button pull-right show-frc-edit-form
                <c:if test="${isPreview or (not isSubmittable)}"> disabled</c:if>"
                data-responseindex="${responseIndex}" data-qnindex="${qnIndex}"
           data-toggle="tooltip"
           data-placement="top"
           title="<%= Const.Tooltips.COMMENT_EDIT %>">
          <span class="glyphicon glyphicon-pencil glyphicon-primary"></span>
        </a>
      </c:if>
    </div>
  </div>
  <%-- Do not add whitespace between the opening and closing tags --%>
  <div <c:if test="${not empty qnIndex && not empty responseIndex}">id="plainCommentText-${divId}"</c:if> style="margin-left: 15px;">${frc.commentText}</div>
  <c:if test="${frc.editDeleteEnabled}">
    <c:set var="textAreaId"><%= Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT %></c:set>
    <shared:feedbackResponseCommentFormForFeedbackParticipant responseIndex="${responseIndex}" qnIndex="${qnIndex}"
        frc="${frc}" formType="Edit" textAreaId="${textAreaId}"/>
  </c:if>
</li>
