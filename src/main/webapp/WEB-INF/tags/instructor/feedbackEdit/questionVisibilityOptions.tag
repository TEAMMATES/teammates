<%@ tag description="instructorFeedbacks - feedback question settings common to all types" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.util.FieldValidator" %>
<%@ tag import="teammates.logic.core.Emails.EmailType" %>
<%@ tag import="teammates.common.datatransfer.FeedbackParticipantType" %>

<%@ attribute name="fqForm" type="teammates.ui.template.FeedbackQuestionEditForm" required="true"%>


<div class="col-sm-12 padding-15px background-color-light-green">
    <div class="col-sm-12 padding-0">
        <b>Visibility</b> (Who can see the responses?)
    </div>
    <div class="col-sm-6 btn-group" data-toggle="buttons">
        <label class="btn btn-xs btn-info visibilityOptionsLabel"
            id="visibilityOptionsLabel${fqForm.questionNumberSuffix}"
            onchange="toggleVisibilityOptions(this)">
            <input type="radio">
            <span class="glyphicon glyphicon-pencil"></span> Edit Visibility
        </label>
        <label class="btn btn-xs btn-info active visibilityMessageButton" id="visibilityMessageButton${fqForm.questionNumberSuffix}" onchange="toggleVisibilityMessage(this)">
            <input type="radio">
            <span class="glyphicon glyphicon-eye-open"></span> Preview Visibility
        </label>
    </div>
</div>
<div class="col-sm-12 background-color-light-green">
    <div class="col-sm-12 text-muted visibilityMessage" id="visibilityMessage${fqForm.questionNumberSuffix}">
        This is the visibility as seen by the feedback giver.
        <ul class="background-color-warning">
        <c:forEach items="${fqForm.visibilitySettings.visibilityMessages}" var="msg">
            <li>${msg}</li>
        </c:forEach>
        </ul>
    </div>
</div>
<div class="col-sm-12 margin-bottom-15px background-color-light-green">
    <div class="visibilityOptions" id="visibilityOptions${fqForm.questionNumberSuffix}">
        <table class="dataTable participantTable table table-striped text-center background-color-white">
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
                    <input class="visibilityCheckbox answerCheckbox${fqForm.question.questionNumber} centered"
                        name="receiverLeaderCheckbox" type="checkbox"
                        value="<%= FeedbackParticipantType.RECEIVER %>" <c:if test="${!fqForm.editable}">disabled="disabled"</c:if>
                        <c:if test="${fqForm.visibilitySettings.responseVisibleFor['RECEIVER']}"> checked="checked"</c:if> >
                </td>
                <td>
                    <input class="visibilityCheckbox giverCheckbox${fqForm.question.questionNumber}"
                        type="checkbox" value="<%= FeedbackParticipantType.RECEIVER %>" <c:if test="${!fqForm.editable}">disabled="disabled"</c:if>
                        <c:if test="${fqForm.visibilitySettings.giverNameVisibleFor['RECEIVER']}"> checked="checked"</c:if> >
                </td>
                <td>
                    <input class="visibilityCheckbox recipientCheckbox${fqForm.question.questionNumber}"
                        name="receiverFollowerCheckbox" type="checkbox"
                        value="<%= FeedbackParticipantType.RECEIVER %>" disabled="disabled"
                        <c:if test="${fqForm.visibilitySettings.recipientNameVisibleFor['RECEIVER']}"> checked="checked"</c:if> >
                </td>
            </tr>
            <tr>
                <td class="text-left">
                    <div data-toggle="tooltip" data-placement="top" title="<%= Const.Tooltips.VISIBILITY_OPTIONS_GIVER_TEAM_MEMBERS %>">
                        Giver's Team Members
                    </div>
                </td>
                <td>
                    <input class="visibilityCheckbox answerCheckbox${fqForm.question.questionNumber}"
                        type="checkbox" value="<%= FeedbackParticipantType.OWN_TEAM_MEMBERS %>" <c:if test="${!fqForm.editable}">disabled="disabled"</c:if>
                        <c:if test="${fqForm.visibilitySettings.responseVisibleFor['OWN_TEAM_MEMBERS']}"> checked="checked"</c:if> >
                </td>
                <td>
                    <input class="visibilityCheckbox giverCheckbox${fqForm.question.questionNumber}"
                        type="checkbox" value="<%= FeedbackParticipantType.OWN_TEAM_MEMBERS %>" <c:if test="${!fqForm.editable}">disabled="disabled"</c:if>
                        <c:if test="${fqForm.visibilitySettings.giverNameVisibleFor['OWN_TEAM_MEMBERS']}"> checked="checked"</c:if> >
                </td>
                <td>
                    <input class="visibilityCheckbox recipientCheckbox${fqForm.question.questionNumber}"
                        type="checkbox" value="<%= FeedbackParticipantType.OWN_TEAM_MEMBERS %>" <c:if test="${!fqForm.editable}">disabled="disabled"</c:if>
                        <c:if test="${fqForm.visibilitySettings.recipientNameVisibleFor['OWN_TEAM_MEMBERS']}"> checked="checked"</c:if> >
                </td>
            </tr>
            <tr>
                <td class="text-left">
                    <div data-toggle="tooltip" data-placement="top" title="<%= Const.Tooltips.VISIBILITY_OPTIONS_RECIPIENT_TEAM_MEMBERS %>">
                        Recipient's Team Members
                    </div>
                </td>
                <td>
                    <input class="visibilityCheckbox answerCheckbox${fqForm.question.questionNumber}" type="checkbox" value="<%= FeedbackParticipantType.RECEIVER_TEAM_MEMBERS %>" <c:if test="${!fqForm.editable}">disabled="disabled"</c:if>
                    <c:if test="${fqForm.visibilitySettings.responseVisibleFor['RECEIVER_TEAM_MEMBERS']}"> checked="checked"</c:if> >
                </td>
                <td>
                    <input class="visibilityCheckbox giverCheckbox${fqForm.question.questionNumber}" type="checkbox" value="<%= FeedbackParticipantType.RECEIVER_TEAM_MEMBERS %>" <c:if test="${!fqForm.editable}">disabled="disabled"</c:if>
                    <c:if test="${fqForm.visibilitySettings.giverNameVisibleFor['RECEIVER_TEAM_MEMBERS']}"> checked="checked"</c:if> >
                </td>
                <td>
                    <input class="visibilityCheckbox recipientCheckbox${fqForm.question.questionNumber}" type="checkbox" value="<%= FeedbackParticipantType.RECEIVER_TEAM_MEMBERS %>" <c:if test="${!fqForm.editable}">disabled="disabled"</c:if>
                    <c:if test="${fqForm.visibilitySettings.recipientNameVisibleFor['RECEIVER_TEAM_MEMBERS']}"> checked="checked"</c:if> >
                </td>
            </tr>
            <tr>
                <td class="text-left">
                    <div data-toggle="tooltip" data-placement="top" title="<%= Const.Tooltips.VISIBILITY_OPTIONS_OTHER_STUDENTS %>">
                        Other students
                    </div>
                </td>
                <td>
                    <input class="visibilityCheckbox answerCheckbox${fqForm.question.questionNumber}"
                    type="checkbox" value="<%= FeedbackParticipantType.STUDENTS %>" <c:if test="${!fqForm.editable}">disabled="disabled"</c:if>
                    <c:if test="${fqForm.visibilitySettings.responseVisibleFor['STUDENTS']}"> checked="checked"</c:if> >
                </td>
                <td>
                    <input class="visibilityCheckbox giverCheckbox${fqForm.question.questionNumber}"
                    type="checkbox" value="<%= FeedbackParticipantType.STUDENTS %>" <c:if test="${!fqForm.editable}">disabled="disabled"</c:if>
                    <c:if test="${fqForm.visibilitySettings.giverNameVisibleFor['STUDENTS']}"> checked="checked"</c:if> >
                </td>
                <td>
                    <input class="visibilityCheckbox recipientCheckbox${fqForm.question.questionNumber}"
                    type="checkbox" value="<%= FeedbackParticipantType.STUDENTS %>" <c:if test="${!fqForm.editable}">disabled="disabled"</c:if>
                    <c:if test="${fqForm.visibilitySettings.recipientNameVisibleFor['STUDENTS']}"> checked="checked"</c:if> >
                </td>
            </tr>
            <tr>
                <td class="text-left">
                    <div data-toggle="tooltip" data-placement="top" title="<%= Const.Tooltips.VISIBILITY_OPTIONS_INSTRUCTORS %>">
                        Instructors
                    </div>
                </td>
                <td>
                    <input class="visibilityCheckbox answerCheckbox${fqForm.question.questionNumber}"
                        type="checkbox" value="<%= FeedbackParticipantType.INSTRUCTORS %>" <c:if test="${!fqForm.editable}">disabled="disabled"</c:if>
                    <c:if test="${fqForm.visibilitySettings.responseVisibleFor['INSTRUCTORS']}"> checked="checked"</c:if> >
                </td>
                <td>
                    <input class="visibilityCheckbox giverCheckbox${fqForm.question.questionNumber}"
                        type="checkbox" value="<%= FeedbackParticipantType.INSTRUCTORS %>" <c:if test="${!fqForm.editable}">disabled="disabled"</c:if>
                    <c:if test="${fqForm.visibilitySettings.giverNameVisibleFor['INSTRUCTORS']}"> checked="checked"</c:if> >
                </td>
                <td>
                    <input class="visibilityCheckbox recipientCheckbox${fqForm.question.questionNumber}"
                        type="checkbox" value="<%= FeedbackParticipantType.INSTRUCTORS %>" <c:if test="${!fqForm.editable}">disabled="disabled"</c:if>
                    <c:if test="${fqForm.visibilitySettings.recipientNameVisibleFor['INSTRUCTORS']}"> checked="checked"</c:if> >
                </td>
            </tr>
        </table>
    </div>
</div>