<%@ tag description="Feedback Response Comment" %>
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
<c:choose>
    <c:when test="${not empty firstIndex && not empty secondIndex && not empty thirdIndex && not empty fourthIndex && not empty frcIndex}">
        <c:set var="divId" value="${fourthIndex}-${firstIndex}-${secondIndex}-${thirdIndex}-${frcIndex}" />
        <c:set var="divIdAsJsParams" value="${firstIndex},${secondIndex},${thirdIndex},${frcIndex}, { sectionIndex: ${fourthIndex} }" />
    </c:when>
    <c:when test="${not empty firstIndex && not empty secondIndex && not empty thirdIndex && not empty frcIndex}">
        <c:set var="divId" value="${firstIndex}-${secondIndex}-${thirdIndex}-${frcIndex}" />
        <c:set var="divIdAsJsParams" value="${firstIndex},${secondIndex},${thirdIndex},${frcIndex}" />
    </c:when>
    <c:otherwise>
        <c:set var="divId" value="${frc.commentId}" />
        <c:set var="divIdAsJsParams" value="" />
    </c:otherwise>
</c:choose>

<li class="list-group-item list-group-item-warning${frc.extraClass}" id="responseCommentRow-${divId}">
    <div id="commentBar-${divId}">
        <span class="text-muted">
            From: ${fn:escapeXml(frc.giverDisplay)} [${frc.createdAt}] ${frc.editedAt}
        </span>
        <c:if test="${frc.withVisibilityIcon}">
            <span class="glyphicon glyphicon-eye-open"
                  data-toggle="tooltip"
                  data-placement="top"
                  style="margin-left: 5px;"
                  title="This response comment is visible to ${frc.whoCanSeeComment}"></span>
        </c:if>
        <c:if test="${frc.withNotificationIcon}">
            <span class="glyphicon glyphicon-bell"
                  data-toggle="tooltip"
                  data-placement="top"
                  title="This comment is pending to notify recipients"></span>
        </c:if>
        <c:if test="${frc.withLinkToCommentsPage}">
            <a type="button"
               href="${frc.linkToCommentsPage}"
               target="_blank" rel="noopener noreferrer"
               class="btn btn-default btn-xs icon-button pull-right"
               data-toggle="tooltip"
               data-placement="top"
               title="Edit comment in the Comments page"
               style="display:none;">
                <span class="glyphicon glyphicon-new-window glyphicon-primary"></span>
            </a>
        </c:if>
        <c:if test="${frc.editDeleteEnabled}">
            <form class="responseCommentDeleteForm pull-right">
                <a href="<%= Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESPONSE_COMMENT_DELETE %>"
                   type="button"
                   id="commentdelete-${divId}"
                   class="btn btn-default btn-xs icon-button"
                   data-toggle="tooltip"
                   data-placement="top"
                   title="<%= Const.Tooltips.COMMENT_DELETE %>"
                   <c:if test="${frc.editDeleteEnabledOnlyOnHover}">style="display: none;"</c:if>
                   <c:if test="${not frc.instructorAllowedToDelete}">disabled</c:if>>
                    <span class="glyphicon glyphicon-trash glyphicon-primary"></span>
                </a>
                <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_SESSION_INDEX %>" value="${firstIndex}">
                <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_RESPONSE_ID %>" value="${frc.feedbackResponseId}">
                <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID %>" value="${frc.commentId}">
                <input type="hidden" name="<%= Const.ParamsNames.COURSE_ID %>" value="${frc.courseId}">
                <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="${frc.feedbackSessionName}">
                <input type="hidden" name="<%= Const.ParamsNames.USER_ID %>" value="${data.account.googleId}">
            </form>
            <a type="button"
               id="commentedit-${divId}"
               class="btn btn-default btn-xs icon-button pull-right"
               onclick="showResponseCommentEditForm(${divIdAsJsParams})"
               data-toggle="tooltip"
               data-placement="top"
               title="<%= Const.Tooltips.COMMENT_EDIT %>"
               <c:if test="${frc.editDeleteEnabledOnlyOnHover}">style="display: none;"</c:if>
               <c:if test="${not frc.instructorAllowedToEdit}">disabled</c:if>>
                <span class="glyphicon glyphicon-pencil glyphicon-primary"></span>
            </a>
        </c:if>
    </div>
    <%-- Do not add whitespace between the opening and closing tags --%>
    <div id="plainCommentText-${divId}" style="margin-left: 15px;">${frc.commentText}</div>
    <c:if test="${frc.editDeleteEnabled}">
        <c:set var="textAreaId"><%= Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT %></c:set>
        <c:set var="submitLink"><%= Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESPONSE_COMMENT_EDIT %></c:set>
        <shared:feedbackResponseCommentForm fsIndex="${firstIndex}"
                                            frc="${frc}"
                                            divId="${divId}"
                                            divIdAsJsParams="${divIdAsJsParams}"
                                            formType="Edit"
                                            textAreaId="${textAreaId}"
                                            submitLink="${submitLink}"
                                            buttonText="Save" />
    </c:if>
</li>
