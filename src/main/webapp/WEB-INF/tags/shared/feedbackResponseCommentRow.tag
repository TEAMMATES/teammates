<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="Feedback Response Comment" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags/shared" prefix="shared" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.datatransfer.FeedbackParticipantType" %>
<%@ attribute name="frc" type="teammates.ui.template.FeedbackResponseCommentRow" required="true" %>
<%@ attribute name="firstIndex" %>
<%@ attribute name="secondIndex" %>
<%@ attribute name="thirdIndex" %>
<%@ attribute name="fourthIndex" %>
<%@ attribute name="frcIndex" %>
<%@ attribute name="viewType" %>
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

<li class="list-group-item list-group-item-warning" id="responseCommentRow-${divId}">
  <div id="commentBar-${divId}" class="row">
    <div class="col-xs-10">
    <span class="text-muted">
      From: ${fn:escapeXml(frc.commentGiverName)} [${frc.createdAt}] ${frc.editedAt}
    </span>
      <c:if test="${frc.withVisibilityIcon}">
        <span class="glyphicon glyphicon-eye-open"
            data-toggle="tooltip"
            data-placement="top"
            style="margin-left: 5px;"
            title="This response comment is visible to ${frc.whoCanSeeComment}"></span>
      </c:if>
    </div>
    <div class="col-xs-2">
      <c:if test="${frc.editDeleteEnabled}">
        <form class="responseCommentDeleteForm pull-right">
          <c:set var="deleteUri" value="<%= Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESPONSE_COMMENT_DELETE %>" />
          <a href="${frc.editDeleteEnabled ? deleteUri : 'javascript:;'}"
              type="button"
              id="commentdelete-${divId}"
              class="btn btn-default btn-xs icon-button<c:if test="${not frc.editDeleteEnabled}"> disabled</c:if>"
              data-toggle="tooltip"
              data-placement="top"
              title="<%= Const.Tooltips.COMMENT_DELETE %>">
            <span class="glyphicon glyphicon-trash glyphicon-primary"></span>
          </a>
          <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_SESSION_INDEX %>" value="${firstIndex}">
          <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_RESPONSE_ID %>" value="${frc.feedbackResponseId}">
          <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID %>" value="${frc.commentId}">
          <input type="hidden" name="<%= Const.ParamsNames.COURSE_ID %>" value="${frc.courseId}">
          <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="${frc.feedbackSessionName}">
          <input type="hidden" name="<%= Const.ParamsNames.USER_ID %>" value="${data.account.googleId}">
          <input type="hidden" name="<%= Const.ParamsNames.SESSION_TOKEN %>" value="${data.sessionToken}">
        </form>
        <a type="button" id="commentedit-${divId}"
            <c:choose>
              <c:when test="${not empty firstIndex && not empty secondIndex && not empty thirdIndex && not empty frcIndex}">
                class="btn btn-default btn-xs icon-button pull-right show-frc-edit-form<c:if test="${not frc.editDeleteEnabled}"> disabled</c:if>"
                data-recipientindex="${firstIndex}" data-giverindex="${secondIndex}"
                data-qnindex="${thirdIndex}" data-frcindex="${frcIndex}"
                <c:if test="${not empty fourthIndex}">data-sectionindex="${fourthIndex}"</c:if>
                <c:if test="${not empty viewType}">data-viewtype="${viewType}"</c:if>
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
    <c:set var="textAreaId"><%= Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT %></c:set>
    <c:set var="submitLink"><%= Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESPONSE_COMMENT_EDIT %></c:set>
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
        buttonText="Save" />
  </c:if>
</li>
