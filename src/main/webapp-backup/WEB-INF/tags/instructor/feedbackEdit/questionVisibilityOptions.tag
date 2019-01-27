<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorFeedbackEdit - feedback question settings for response visibility" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.util.FieldValidator" %>
<%@ tag import="teammates.common.datatransfer.FeedbackParticipantType" %>

<%@ attribute name="fqForm" type="teammates.ui.template.FeedbackQuestionEditForm" required="true"%>

<c:set var="FEEDBACK_RECEIVER"><%=FeedbackParticipantType.RECEIVER.name()%></c:set>
<c:set var="FEEDBACK_OWN_TEAM_MEMBERS"><%=FeedbackParticipantType.OWN_TEAM_MEMBERS.name()%></c:set>
<c:set var="FEEDBACK_RECEIVER_TEAM_MEMBERS"><%=FeedbackParticipantType.RECEIVER_TEAM_MEMBERS.name()%></c:set>
<c:set var="FEEDBACK_STUDENTS"><%=FeedbackParticipantType.STUDENTS.name()%></c:set>
<c:set var="FEEDBACK_INSTRUCTORS"><%=FeedbackParticipantType.INSTRUCTORS.name()%></c:set>

<div class="col-sm-12 margin-bottom-15px padding-15px <%= fqForm.isQuestionHasResponses() ? "alert alert-danger" : "background-color-light-green" %>">
  <div class="margin-bottom-7px">
    <c:if test="${fqForm.questionHasResponses}">
      <h4>Changing the visibility after collecting responses is not recommended.</h4>
      <p>Reason: The existing responses were submitted under the 'promise' of a certain visibility and changing the visibility later 'breaks' that promise.</p>
      <br/>
    </c:if>
    <b class="visibility-title">Visibility</b> (Who can see the responses?)
  </div>
  <div class="visibility-options-dropdown btn-group margin-bottom-10px">
    <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
      ${fqForm.visibilitySettings.dropdownMenuLabel}
    </button>
    <ul class="dropdown-menu">
      <li class="dropdown-header">Common visibility options</li>
      <c:forEach items="<%= Const.FeedbackQuestion.COMMON_VISIBILITY_OPTIONS %>" var="visibilityOption">
        <li>
          <a class="visibility-options-dropdown-option" href="javascript:;" data-option-name="${visibilityOption.key}">${visibilityOption.value}</a>
        </li>
      </c:forEach>
      <li role="separator" class="divider"></li>
      <li><a class="visibility-options-dropdown-option" href="javascript:;" data-option-name="OTHER">Custom visibility options...</a></li>
    </ul>
  </div>
  <div class="visibilityOptions overflow-hidden" id="visibilityOptions-${fqForm.questionIndex}" style="display:none;">
    <table class="data-table participantTable table table-striped text-center background-color-white margin-bottom-10px">
      <tr>
        <th class="text-center">User/Group</th>
        <th class="text-center">Can see answer</th>
        <th class="text-center">Can see giver's name</th>
        <th class="text-center">Can see recipient's name</th>
      </tr>
      <tr>
        <td class="text-left">
          <div data-toggle="tooltip" data-placement="top" title="<%= Const.Tooltips.VISIBILITY_OPTIONS_RECIPIENT %>">
            Recipient(s)
          </div>
        </td>
        <td>
          <input class="visibilityCheckbox answerCheckbox centered"
              name="receiverLeaderCheckbox" type="checkbox"
              value="<%= FeedbackParticipantType.RECEIVER %>" <c:if test="${!fqForm.editable}">disabled</c:if>
              <c:if test="${fqForm.visibilitySettings.responseVisibleFor[FEEDBACK_RECEIVER]}"> checked=""</c:if> >
        </td>
        <td>
          <input class="visibilityCheckbox giverCheckbox"
              type="checkbox" value="<%= FeedbackParticipantType.RECEIVER %>" <c:if test="${!fqForm.editable}">disabled</c:if>
              <c:if test="${fqForm.visibilitySettings.giverNameVisibleFor[FEEDBACK_RECEIVER]}"> checked=""</c:if> >
        </td>
        <td>
          <input class="visibilityCheckbox recipientCheckbox"
              name="receiverFollowerCheckbox" type="checkbox"
              value="<%= FeedbackParticipantType.RECEIVER %>" disabled
              <c:if test="${fqForm.visibilitySettings.recipientNameVisibleFor[FEEDBACK_RECEIVER]}"> checked=""</c:if> >
        </td>
      </tr>
      <tr>
        <td class="text-left">
          <div data-toggle="tooltip" data-placement="top" title="<%= Const.Tooltips.VISIBILITY_OPTIONS_GIVER_TEAM_MEMBERS %>">
            Giver's Team Members
          </div>
        </td>
        <td>
          <input class="visibilityCheckbox answerCheckbox"
              type="checkbox" value="<%= FeedbackParticipantType.OWN_TEAM_MEMBERS %>" <c:if test="${!fqForm.editable}">disabled</c:if>
              <c:if test="${fqForm.visibilitySettings.responseVisibleFor[FEEDBACK_OWN_TEAM_MEMBERS]}"> checked=""</c:if> >
        </td>
        <td>
          <input class="visibilityCheckbox giverCheckbox"
              type="checkbox" value="<%= FeedbackParticipantType.OWN_TEAM_MEMBERS %>" <c:if test="${!fqForm.editable}">disabled</c:if>
              <c:if test="${fqForm.visibilitySettings.giverNameVisibleFor[FEEDBACK_OWN_TEAM_MEMBERS]}"> checked=""</c:if> >
        </td>
        <td>
          <input class="visibilityCheckbox recipientCheckbox"
              type="checkbox" value="<%= FeedbackParticipantType.OWN_TEAM_MEMBERS %>" <c:if test="${!fqForm.editable}">disabled</c:if>
              <c:if test="${fqForm.visibilitySettings.recipientNameVisibleFor[FEEDBACK_OWN_TEAM_MEMBERS]}"> checked=""</c:if> >
        </td>
      </tr>
      <tr>
        <td class="text-left">
          <div data-toggle="tooltip" data-placement="top" title="<%= Const.Tooltips.VISIBILITY_OPTIONS_RECIPIENT_TEAM_MEMBERS %>">
            Recipient's Team Members
          </div>
        </td>
        <td>
          <input class="visibilityCheckbox answerCheckbox" type="checkbox" value="<%= FeedbackParticipantType.RECEIVER_TEAM_MEMBERS %>" <c:if test="${!fqForm.editable}">disabled</c:if>
              <c:if test="${fqForm.visibilitySettings.responseVisibleFor[FEEDBACK_RECEIVER_TEAM_MEMBERS]}"> checked=""</c:if> >
        </td>
        <td>
          <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="<%= FeedbackParticipantType.RECEIVER_TEAM_MEMBERS %>" <c:if test="${!fqForm.editable}">disabled</c:if>
              <c:if test="${fqForm.visibilitySettings.giverNameVisibleFor[FEEDBACK_RECEIVER_TEAM_MEMBERS]}"> checked=""</c:if> >
        </td>
        <td>
          <input class="visibilityCheckbox recipientCheckbox" type="checkbox" value="<%= FeedbackParticipantType.RECEIVER_TEAM_MEMBERS %>" <c:if test="${!fqForm.editable}">disabled</c:if>
              <c:if test="${fqForm.visibilitySettings.recipientNameVisibleFor[FEEDBACK_RECEIVER_TEAM_MEMBERS]}"> checked=""</c:if> >
        </td>
      </tr>
      <tr>
        <td class="text-left">
          <div data-toggle="tooltip" data-placement="top" title="<%= Const.Tooltips.VISIBILITY_OPTIONS_OTHER_STUDENTS %>">
            Other students
          </div>
        </td>
        <td>
          <input class="visibilityCheckbox answerCheckbox"
              type="checkbox" value="<%= FeedbackParticipantType.STUDENTS %>" <c:if test="${!fqForm.editable}">disabled</c:if>
              <c:if test="${fqForm.visibilitySettings.responseVisibleFor[FEEDBACK_STUDENTS]}"> checked=""</c:if> >
        </td>
        <td>
          <input class="visibilityCheckbox giverCheckbox"
              type="checkbox" value="<%= FeedbackParticipantType.STUDENTS %>" <c:if test="${!fqForm.editable}">disabled</c:if>
              <c:if test="${fqForm.visibilitySettings.giverNameVisibleFor[FEEDBACK_STUDENTS]}"> checked=""</c:if> >
        </td>
        <td>
          <input class="visibilityCheckbox recipientCheckbox"
              type="checkbox" value="<%= FeedbackParticipantType.STUDENTS %>" <c:if test="${!fqForm.editable}">disabled</c:if>
              <c:if test="${fqForm.visibilitySettings.recipientNameVisibleFor[FEEDBACK_STUDENTS]}"> checked=""</c:if> >
        </td>
      </tr>
      <tr>
        <td class="text-left">
          <div data-toggle="tooltip" data-placement="top" title="<%= Const.Tooltips.VISIBILITY_OPTIONS_INSTRUCTORS %>">
            Instructors
          </div>
        </td>
        <td>
          <input class="visibilityCheckbox answerCheckbox"
              type="checkbox" value="<%= FeedbackParticipantType.INSTRUCTORS %>" <c:if test="${!fqForm.editable}">disabled</c:if>
              <c:if test="${fqForm.visibilitySettings.responseVisibleFor[FEEDBACK_INSTRUCTORS]}"> checked=""</c:if> >
        </td>
        <td>
          <input class="visibilityCheckbox giverCheckbox"
              type="checkbox" value="<%= FeedbackParticipantType.INSTRUCTORS %>" <c:if test="${!fqForm.editable}">disabled</c:if>
              <c:if test="${fqForm.visibilitySettings.giverNameVisibleFor[FEEDBACK_INSTRUCTORS]}"> checked=""</c:if> >
        </td>
        <td>
          <input class="visibilityCheckbox recipientCheckbox"
              type="checkbox" value="<%= FeedbackParticipantType.INSTRUCTORS %>" <c:if test="${!fqForm.editable}">disabled</c:if>
              <c:if test="${fqForm.visibilitySettings.recipientNameVisibleFor[FEEDBACK_INSTRUCTORS]}"> checked=""</c:if> >
        </td>
      </tr>
    </table>
  </div>
  <!-- Fix for collapsing margin problem. Reference: http://stackoverflow.com/questions/6204670 -->
  <div class="visibility-message overflow-hidden" id="visibilityMessage-${fqForm.questionIndex}">
    This is the visibility hint as seen by the feedback giver:
    <ul class="text-muted background-color-warning">
      <c:forEach items="${fqForm.visibilitySettings.visibilityMessages}" var="msg">
        <li>${msg}</li>
      </c:forEach>
    </ul>
  </div>
</div>
