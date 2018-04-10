<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="Feedback Response Comment Form With Visibility Options" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.datatransfer.FeedbackParticipantType" %>
<%@ attribute name="fsIndex" required="true" %>
<%@ attribute name="secondIndex" required="true" %>
<%@ attribute name="thirdIndex" required="true" %>
<%@ attribute name="fourthIndex" %>
<%@ attribute name="frcIndex" %>
<%@ attribute name="frc" type="teammates.ui.template.FeedbackResponseCommentRow" required="true" %>
<%@ attribute name="divId" required="true" %>
<%@ attribute name="formType" required="true" %>
<%@ attribute name="textAreaId" required="true" %>
<%@ attribute name="submitLink" required="true" %>
<%@ attribute name="buttonText" required="true" %>
<%@ attribute name="viewType" %>
<%@ attribute name="isOnQuestionsPage" %>
<c:set var="isEditForm" value="${formType eq 'Edit'}" />
<c:set var="isAddForm" value="${formType eq 'Add'}" />
<form class="responseComment${formType}Form"<c:if test="${isEditForm}"> style="display: none;" id="responseCommentEditForm-${divId}"</c:if>>
  <div class="form-group form-inline">
    <div class="form-group text-muted">
      <p>
        Giver: ${fn:escapeXml(frc.responseGiverName)}
        <br>
        Recipient: ${fn:escapeXml(frc.responseRecipientName)}
      </p>
      You may change comment's visibility using the visibility options on the right hand side.
    </div>
    <a id="frComment-visibility-options-trigger-${divId}"
        class="btn btn-sm btn-info pull-right toggle-visib-${fn:toLowerCase(formType)}-form"
        data-recipientindex="${fsIndex}" data-giverindex="${secondIndex}"
        data-qnindex="${thirdIndex}" data-frcindex="${frcIndex}"
        <c:if test="${not empty fourthIndex}">data-sectionindex="${fourthIndex}"</c:if>
        <c:if test="${not empty viewType}">data-viewtype="${viewType}"</c:if>>
      <span class="glyphicon glyphicon-eye-close"></span>
      Show Visibility Options
    </a>
  </div>
  <div id="visibility-options-${divId}" class="panel panel-default" style="display: none;">
    <div class="panel-heading">
      Visibility Options
    </div>
    <table class="table text-center" style="color: #000;">
      <tbody>
        <tr>
          <th class="text-center">User/Group</th>
          <th class="text-center">Can see this comment</th>
          <th class="text-center">Can see comment giver's name</th>
        </tr>
        <tr id="response-giver-${divId}">
          <td class="text-left">
            <div data-toggle="tooltip"
                data-placement="top"
                title="Control what response giver can view">
              Response Giver
            </div>
          </td>
          <td>
            <input class="visibilityCheckbox answerCheckbox centered"
                name="receiverLeaderCheckbox"
                type="checkbox"
                value="<%= FeedbackParticipantType.GIVER %>"
                <c:if test="${frc.showCommentToResponseGiver}">checked=""</c:if>>
          </td>
          <td>
            <input class="visibilityCheckbox giverCheckbox"
                type="checkbox"
                value="<%= FeedbackParticipantType.GIVER %>"
                <c:if test="${frc.showGiverNameToResponseGiver}">checked=""</c:if>>
          </td>
        </tr>
        <c:if test="${frc.responseVisibleToRecipient}">
          <tr id="response-recipient-${divId}">
            <td class="text-left">
              <div data-toggle="tooltip"
                  data-placement="top"
                  title="Control what response recipient(s) can view">
                Response Recipient(s)
              </div>
            </td>
            <td>
              <input class="visibilityCheckbox answerCheckbox centered"
                  name="receiverLeaderCheckbox"
                  type="checkbox"
                  value="<%= FeedbackParticipantType.RECEIVER %>"
                  <c:if test="${frc.showCommentToResponseRecipient}">checked=""</c:if>>
            </td>
            <td>
              <input class="visibilityCheckbox giverCheckbox"
                  type="checkbox"
                  value="<%= FeedbackParticipantType.RECEIVER %>"
                  <c:if test="${frc.showGiverNameToResponseRecipient}">checked=""</c:if>>
            </td>
          </tr>
        </c:if>
        <c:if test="${frc.responseVisibleToGiverTeam}">
          <tr id="response-giver-team-${divId}">
            <td class="text-left">
              <div data-toggle="tooltip"
                  data-placement="top"
                  title="Control what team members of response giver can view">
                Response Giver's Team Members
              </div>
            </td>
            <td>
              <input class="visibilityCheckbox answerCheckbox"
                  type="checkbox"
                  value="<%= FeedbackParticipantType.OWN_TEAM_MEMBERS %>"
                  <c:if test="${frc.showCommentToResponseGiverTeam}">checked=""</c:if>>
            </td>
            <td>
              <input class="visibilityCheckbox giverCheckbox"
                  type="checkbox"
                  value="<%= FeedbackParticipantType.OWN_TEAM_MEMBERS %>"
                  <c:if test="${frc.showGiverNameToResponseGiverTeam}">checked=""</c:if>>
            </td>
          </tr>
        </c:if>
        <c:if test="${frc.responseVisibleToRecipientTeam}">
          <tr id="response-recipient-team-${divId}">
            <td class="text-left">
              <div data-toggle="tooltip"
                  data-placement="top"
                  title="Control what team members of response recipient(s) can view">
                Response Recipient's Team Members
              </div>
            </td>
            <td>
              <input class="visibilityCheckbox answerCheckbox"
                  type="checkbox"
                  value="<%= FeedbackParticipantType.RECEIVER_TEAM_MEMBERS %>"
                  <c:if test="${frc.showCommentToResponseRecipientTeam}">checked=""</c:if>>
            </td>
            <td>
              <input class="visibilityCheckbox giverCheckbox"
                  type="checkbox"
                  value="<%= FeedbackParticipantType.RECEIVER_TEAM_MEMBERS %>"
                  <c:if test="${frc.showGiverNameToResponseRecipientTeam}">checked=""</c:if>>
            </td>
          </tr>
        </c:if>
        <c:if test="${frc.responseVisibleToStudents}">
          <tr id="response-students-${divId}">
            <td class="text-left">
              <div data-toggle="tooltip"
                  data-placement="top"
                  title="Control what other students in this course can view">
                Other students in this course
              </div>
            </td>
            <td>
              <input class="visibilityCheckbox answerCheckbox"
                  type="checkbox"
                  value="<%= FeedbackParticipantType.STUDENTS %>"
                  <c:if test="${frc.showCommentToStudents}">checked=""</c:if>>
            </td>
            <td>
              <input class="visibilityCheckbox giverCheckbox"
                  type="checkbox"
                  value="<%= FeedbackParticipantType.STUDENTS %>"
                  <c:if test="${frc.showGiverNameToStudents}">checked=""</c:if>>
            </td>
          </tr>
        </c:if>
        <c:if test="${frc.responseVisibleToInstructors}">
          <tr id="response-instructors-${divId}">
            <td class="text-left">
              <div data-toggle="tooltip"
                  data-placement="top"
                  title="Control what instructors can view">
                Instructors
              </div>
            </td>
            <td>
              <input class="visibilityCheckbox answerCheckbox"
                  type="checkbox"
                  value="<%= FeedbackParticipantType.INSTRUCTORS %>"
                  <c:if test="${frc.showCommentToInstructors}">checked=""</c:if>>
            </td>
            <td>
              <input class="visibilityCheckbox giverCheckbox"
                  type="checkbox"
                  value="<%= FeedbackParticipantType.INSTRUCTORS %>"
                  <c:if test="${frc.showGiverNameToInstructors}">checked=""</c:if>>
            </td>
          </tr>
        </c:if>
      </tbody>
    </table>
  </div>
  <div class="form-group">
    <div class="panel panel-default panel-body" id="${textAreaId}-${divId}">
      ${frc.commentText}
    </div>
    <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT %>">
  </div>
  <div class="col-sm-offset-5">
    <a href="${submitLink}"
        type="button"
        class="btn btn-primary"
        id="button_save_comment_for_${fn:toLowerCase(formType)}-${divId}">
      ${buttonText}
    </a>
    <c:if test="${empty isOnQuestionsPage && !isOnQuestionsPage}">
      <input type="button"
          class="btn btn-default hide-frc-${fn:toLowerCase(formType)}-form"
          value="Cancel"
          data-recipientindex="${fsIndex}" data-giverindex="${secondIndex}"
          data-qnindex="${thirdIndex}" data-frcindex="${frcIndex}"
          <c:if test="${not empty fourthIndex}">data-sectionindex="${fourthIndex}"</c:if>
          <c:if test="${not empty viewType}">data-viewtype="${viewType}"</c:if>>
    </c:if>
  </div>
  <c:if test="${isEditForm}"><input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID %>" value="${frc.commentId}"></c:if>
  <c:if test="${isAddForm}"><input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_QUESTION_ID %>" value="${frc.questionId}"></c:if>
  <c:if test="${not empty isOnQuestionsPage && isOnQuestionsPage}">
    <input type="hidden" name="isOnQuestionsPage" value="${isOnQuestionsPage}">
  </c:if>
  <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_SESSION_INDEX %>" value="${fsIndex}">
  <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_RESPONSE_ID %>" value="${fn:escapeXml(frc.feedbackResponseId)}">
  <input type="hidden" name="<%= Const.ParamsNames.COURSE_ID %>" value="${frc.courseId}">
  <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="${frc.feedbackSessionName}">
  <input type="hidden" name="<%= Const.ParamsNames.USER_ID %>" value="${data.account.googleId}">
  <input type="hidden" name="<%= Const.ParamsNames.RESPONSE_COMMENTS_SHOWCOMMENTSTO %>" value="${frc.showCommentToString}">
  <input type="hidden" name="<%= Const.ParamsNames.RESPONSE_COMMENTS_SHOWGIVERTO %>" value="${frc.showGiverNameToString}">
  <input type="hidden" name="<%= Const.ParamsNames.SESSION_TOKEN %>" value="${data.sessionToken}">
</form>
