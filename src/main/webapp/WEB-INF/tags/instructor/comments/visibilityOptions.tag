<%@ tag description="Visibility Options" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.datatransfer.CommentParticipantType" %>
<%@ attribute name="commentIdx" required="true" %>
<%@ attribute name="commentRow" type="teammates.ui.template.CommentRow" required="true" %>
<%@ attribute name="courseId" required="true" %>
<div id="commentTextEdit${commentIdx}"
     style="display: none;">
    <div class="form-group form-inline">
        <div class="form-group text-muted">
            You may change comment's visibility using the visibility options on the right hand side.
        </div>
        <a id="visibility-options-trigger${commentIdx}"
            class="btn btn-sm btn-info pull-right">
            <span class="glyphicon glyphicon-eye-close"></span>
            Show Visibility Options
        </a>
    </div>
    <div id="visibility-options${commentIdx}" class="panel panel-default"
        style="display: none;">
        <div class="panel-heading">Visibility Options</div>
        <table class="table text-center text-color-black">
            <tbody>
                <tr>
                    <th class="text-center">User/Group</th>
                    <th class="text-center">Can see your comment</th>
                    <th class="text-center">Can see giver's name</th>
                    <th class="text-center">Can see recipient's name</th>
                </tr>
                <c:if test="${(commentRow.comment.recipientType == 'PERSON')}">
                    <tr id="recipient-person${commentIdx}">
                        <td class="text-left">
                            <div data-toggle="tooltip"
                                data-placement="top" title=""
                                data-original-title="Control what comment recipient(s) can view">
                                Recipient(s)
                            </div>
                        </td>
                        <td>
                            <input 
                                class="visibilityCheckbox answerCheckbox"
                                type="checkbox" value="<%= CommentParticipantType.PERSON %>"
                                ${commentRow.visibilityCheckboxes.visibilitySettingsForRecipient[0] ? 'checked=\"checked\"' : ''}>
                        </td>
                        <td><input 
                                class="visibilityCheckbox giverCheckbox"
                                type="checkbox" value="<%= CommentParticipantType.PERSON %>"
                                ${commentRow.visibilityCheckboxes.visibilitySettingsForRecipient[1] ? 'checked=\"checked\"' : ''}>
                        </td>
                        <td>
                            <input 
                                class="visibilityCheckbox recipientCheckbox"
                                name="receiverFollowerCheckbox"
                                type="checkbox" value="<%= CommentParticipantType.PERSON %>"
                                disabled="disabled">
                        </td>
                    </tr>
                </c:if>
                <c:if test="${commentRow.comment.recipientType == 'PERSON'
                       || commentRow.comment.recipientType == 'TEAM'}">
                    <tr id="recipient-team${commentIdx}">
                        <td class="text-left">
                            <div data-toggle="tooltip"
                                data-placement="top" title=""
                                data-original-title="Control what team members of comment recipients can view">
                                ${commentRow.comment.recipientType == 'TEAM' ? "Recipient Team" : "Recipient's Team"}</div>
                        </td>
                        <td>
                            <input 
                                class="visibilityCheckbox answerCheckbox"
                                type="checkbox"
                                value="<%= CommentParticipantType.TEAM %>"
                                ${commentRow.visibilityCheckboxes.visibilitySettingsForRecipientTeam[0] ? "checked=\"checked\"" : ""}>
                        </td>
                        <td>
                            <input 
                                class="visibilityCheckbox giverCheckbox"
                                type="checkbox"
                                value="<%= CommentParticipantType.TEAM %>"
                                ${commentRow.visibilityCheckboxes.visibilitySettingsForRecipientTeam[1] ? "checked=\"checked\"" : ""}>
                        </td>
                        <td>
                            <input 
                                class="visibilityCheckbox recipientCheckbox"
                                type="checkbox"
                                value="<%= CommentParticipantType.TEAM %>"
                                ${commentRow.comment.recipientType == 'TEAM' ? "disabled=\"disabled\"" : ""}
                                ${commentRow.visibilityCheckboxes.visibilitySettingsForRecipientTeam[2] ? "checked=\"checked\"" : ""}>
                        </td>
                    </tr>
                </c:if>
                <c:if test="${commentRow.comment.recipientType != 'COURSE'}">
                    <tr id="recipient-section${commentIdx}">
                        <td class="text-left">
                            <div data-toggle="tooltip"
                                data-placement="top" title=""
                                data-original-title="Control what students in the same section can view">
                                ${commentRow.comment.recipientType == 'SECTION' ? "Recipient Section" : "Recipient's Section"}</div>
                        </td>
                        <td>
                            <input 
                                class="visibilityCheckbox answerCheckbox"
                                type="checkbox"
                                value="<%= CommentParticipantType.SECTION %>"
                                ${commentRow.visibilityCheckboxes.visibilitySettingsForRecipientSection[0] ? "checked=\"checked\"" : ""}>
                        </td>
                        <td>
                            <input 
                                class="visibilityCheckbox giverCheckbox"
                                type="checkbox"
                                value="<%= CommentParticipantType.SECTION %>"
                                ${commentRow.visibilityCheckboxes.visibilitySettingsForRecipientSection[1] ? "checked=\"checked\"" : ""}>
                        </td>
                        <td>
                            <input 
                                class="visibilityCheckbox recipientCheckbox"
                                type="checkbox"
                                value="<%= CommentParticipantType.SECTION %>"
                                ${commentRow.comment.recipientType == 'SECTION' ? "disabled=\"disabled\"" : ""}
                                ${commentRow.visibilityCheckboxes.visibilitySettingsForRecipientSection[2] ? "checked=\"checked\"" : ""}>
                        </td>
                    </tr>
                </c:if>
                <tr id="recipient-course${commentIdx}">
                    <td class="text-left">
                        <div data-toggle="tooltip"
                            data-placement="top" title=""
                            data-original-title="Control what other students in this course can view">
                            ${commentRow.comment.recipientType == 'COURSE' ? "Students in this course" : "Other students in this course"}</div>
                    </td>
                    <td>
                        <input 
                            class="visibilityCheckbox answerCheckbox"
                            type="checkbox" value="<%= CommentParticipantType.COURSE %>"
                            ${commentRow.visibilityCheckboxes.visibilitySettingsForCourseStudents[0] ? "checked=\"checked\"" : ""}>
                    </td>
                    <td>
                        <input 
                            class="visibilityCheckbox giverCheckbox"
                            type="checkbox" value="<%= CommentParticipantType.COURSE %>"
                            ${commentRow.visibilityCheckboxes.visibilitySettingsForCourseStudents[1] ? "checked=\"checked\"" : ""}>
                    </td>
                    <td>
                        <input 
                            class="visibilityCheckbox recipientCheckbox"
                            type="checkbox" value="<%= CommentParticipantType.COURSE %>"
                            ${commentRow.comment.recipientType == 'COURSE' ? "disabled=\"disabled\"" : ""}
                            ${commentRow.visibilityCheckboxes.visibilitySettingsForCourseStudents[2] ? "checked=\"checked\"" : ""}>
                    </td>
                </tr>
                <tr>
                    <td class="text-left">
                        <div data-toggle="tooltip"
                            data-placement="top" title=""
                            data-original-title="Control what instructors can view">
                            Instructors
                        </div>
                    </td>
                    <td>
                        <input
                            class="visibilityCheckbox answerCheckbox"
                            type="checkbox" value="<%= CommentParticipantType.INSTRUCTOR %>"
                            ${commentRow.visibilityCheckboxes.visibilitySettingsForInstructors[0] ? "checked=\"checked\"" : ""}>
                    </td>
                    <td>
                        <input
                            class="visibilityCheckbox giverCheckbox"
                            type="checkbox" value="<%= CommentParticipantType.INSTRUCTOR %>"
                            ${commentRow.visibilityCheckboxes.visibilitySettingsForInstructors[1] ? "checked=\"checked\"" : ""}>
                    </td>
                    <td>
                        <input
                            class="visibilityCheckbox recipientCheckbox"
                            type="checkbox" value="<%= CommentParticipantType.INSTRUCTOR %>"
                            ${commentRow.visibilityCheckboxes.visibilitySettingsForInstructors[2] ? "checked=\"checked\"" : ""}>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
    <div class="form-group">
       <!-- Do not add whitespace between the opening and closing tags-->
        <textarea
            class="form-control"
            rows="3"
            placeholder="Your comment about this student"
            name=<%= Const.ParamsNames.COMMENT_TEXT %>
            id="commentText${commentIdx}">${commentRow.comment.commentText}</textarea>
    </div>
    <div class="col-sm-offset-5">
        <input
            id="commentsave-${commentIdx}"
            title="Save comment"
            onclick="return submitCommentForm('${commentIdx}');"
            type="submit"
            class="btn btn-primary"
            value="Save">
        <input type="button"
            class="btn btn-default"
            value="Cancel"
            onclick="return disableComment('${commentIdx}');">
    </div>
</div>
<input type="hidden"
    name=<%= Const.ParamsNames.COMMENT_EDITTYPE %>
    id="<%= Const.ParamsNames.COMMENT_EDITTYPE %>-${commentIdx}"
    value="edit">
<input type="hidden"
    name=<%= Const.ParamsNames.COMMENT_ID %>
    value="${commentRow.comment.commentId}">
<input type="hidden"
    name=<%= Const.ParamsNames.COURSE_ID %>
    value="${courseId}">
<input type="hidden"
    name=<%= Const.ParamsNames.FROM_COMMENTS_PAGE %>
    value="true"> 
<input type="hidden" 
    name=<%= Const.ParamsNames.COMMENTS_SHOWCOMMENTSTO %> 
    value="${commentRow.showCommentsTo}">
<input type="hidden" 
    name=<%= Const.ParamsNames.COMMENTS_SHOWGIVERTO %> 
    value="${commentRow.showGiverNameTo}">
<input type="hidden" 
    name=<%= Const.ParamsNames.COMMENTS_SHOWRECIPIENTTO %> 
    value="${commentRow.showRecipientNameTo}">
<input type="hidden"
    name="<%= Const.ParamsNames.USER_ID %>"
    value="${data.account.googleId}">