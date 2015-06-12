<%@ tag description="Comments for students" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.util.TimeHelper" %>
<%@ tag import="teammates.common.datatransfer.CommentParticipantType" %>
<div class="panel panel-primary">
    <div class="panel-heading">
        <strong>${data.isViewingDraft ? 'Comment drafts' : 'Comments for students'}</strong>
    </div>
    <div class="panel-body">
        ${data.isViewingDraft ? 'Your comments that are not finished:' : ''}
        <c:set var="commentIdx" value="0" scope="page" />
        <c:set var="studentIdx" value="0" scope="page" />
        <c:forEach items="${data.comments.keySet}" var="giverEmail"> <%--recipient loop starts--%>
            <c:set var="studentIdx" value="${studentIdx + 1}" scope="page" />
            <div class="panel panel-info student-record-comments ${giverEmail == InstructorCommentsPageData.COMMENT_GIVER_NAME_THAT_COMES_FIRST ? 'giver_display-by-you' : 'giver_display-by-others'}"
                <c:if test="${empty data.comments.get.giverEmail}"> 
                    style="display: none;" 
                </c:if>>
                <div class="panel-heading">
                    From <b>${data.giverName.giverEmail}</b>
                </div>
                <ul class="list-group comments">
                    <c:set var="recipientTypeForThisRecipient" value="${CommentParticipantType.PERSON}" scope="page" /> <%-- default value is PERSON --%>
                    <c:forEach items="${data.comments.get.giverEmail}" var="comments"> <%--student comments loop starts--%>
                        <c:set var="commentIdx" value="${commentIdx + 1}" scope="page" />
                        <c:set var="recipientTypeForThisRecipient" value="${comment.recipientType}" />;
                        <li id="${comment.commentId}"
                            class="list-group-item list-group-item-warning ${not empty comment.showCommentTo ? 'status_display-public' : 'status_display-private'}">
                            <form method="post"
                                action="<%= Const.ActionURIs.INSTRUCTOR_STUDENT_COMMENT_EDIT %>"
                                name="form_commentedit"
                                class="form_comment"
                                id="form_commentedit-${commentIdx}">
                                <div id="commentBar-${commentIdx}">
                                    
                                    <span class="text-muted">To ${data.getRecipientNames.comment.recipients} on
                                    <%--    <%= TimeHelper.formatTime(comment.createdAt) %> ${comment.getEditedAtTextForInstructor(data.getGiverName(giverEmail).equals("Anonymous"))} --%>
                                    </span>
                                    <c:if test="${comment.giverEmail.equals(data.instructorEmail)
                                            || (data.currentInstructor != null 
                                            && data.isInstructorAllowedForPrivilegeOnComment(comment, 
                                                                                             Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS))}"> <%-- comment edit/delete control starts --%>
                                        <a type="button"
                                            id="commentdelete-${commentIdx}"
                                            class="btn btn-default btn-xs icon-button pull-right"
                                            onclick="return deleteComment('${commentIdx}');"
                                            data-toggle="tooltip"
                                            data-placement="top"
                                            title=""
                                            data-original-title="<%= Const.Tooltips.COMMENT_DELETE %>"
                                            style="display: none;">
                                            <span class="glyphicon glyphicon-trash glyphicon-primary"></span>
                                        </a> 
                                        <a type="button"
                                            id="commentedit-${commentIdx}"
                                            class="btn btn-default btn-xs icon-button pull-right"
                                            onclick="return enableEdit('${commentIdx}');"
                                            data-toggle="tooltip"
                                            data-placement="top"
                                            title=""
                                            data-original-title="<%= Const.Tooltips.COMMENT_EDIT %>"
                                            style="display: none;">
                                            <span class="glyphicon glyphicon-pencil glyphicon-primary"></span>
                                        </a>
                                    </c:if> <%-- comment edit/delete control ends --%>
                                    <c:if test="${not empty comment.showCommentTo}">
                                        <c:set var="peopleCanSee" value="${data.typeOfPeopleCanViewComment.comment}" /> 
                                        <span class="glyphicon glyphicon-eye-open" data-toggle="tooltip" style="margin-left: 5px;"
                                            data-placement="top"
                                            title="This comment is visible to ${peopleCanSee}"></span>
                                    </c:if>
                                    <c:if test="${comment.sendingState == CommentSendingState.PENDING}">
                                        <span class="glyphicon glyphicon-bell" data-toggle="tooltip" 
                                            data-placement="top"
                                            title="This comment is pending notification. i.e., you have not sent a notification about this comment yet"></span>
                                    </c:if>
                                </div>
                                <div id="plainCommentText${commentIdx}">${comment.commentText.value}</div>
                                <c:if test="${comment.giverEmail == data.instructorEmail
                                        || data.currentInstructor != null 
                                        && data.isInstructorAllowedForPrivilegeOnComment(comment, 
                                                                                         Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS)}"> <%-- comment edit/delete control starts --%>
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
                                                        <th class="text-center">Can see
                                            your comment</th>
                                                        <th class="text-center">Can see
                                            giver's name</th>
                                                        <th class="text-center">Can see
                                            recipient's name</th>
                                                    </tr>
                                                    <c:if test="${(comment.recipientType == CommentParticipantType.PERSON)}">
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
                                                                    ${comment.showCommentTo.contains(CommentParticipantType.PERSON) ? 'checked=\"checked\"' : ''}>
                                                            </td>
                                                            <td><input 
                                                                    class="visibilityCheckbox giverCheckbox"
                                                                    type="checkbox" value="<%= CommentParticipantType.PERSON %>"
                                                                    ${comment.showGiverNameTo.contains(CommentParticipantType.PERSON) ? 'checked=\"checked\"' : ''}>
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
                                                    <c:if test="${comment.recipientType == CommentParticipantType.PERSON
                                                           || comment.recipientType == CommentParticipantType.TEAM}">
                                                        <tr id="recipient-team${commentIdx}">
                                                            <td class="text-left">
                                                                <div data-toggle="tooltip"
                                                                    data-placement="top" title=""
                                                                    data-original-title="Control what team members of comment recipients can view">
                                                                    ${comment.recipientType == CommentParticipantType.TEAM ? "Recipient Team" : "Recipient's Team"}</div>
                                                            </td>
                                                            <td>
                                                                <input 
                                                                    class="visibilityCheckbox answerCheckbox"
                                                                    type="checkbox"
                                                                    value="<%= CommentParticipantType.TEAM %>"
                                                                    ${comment.showCommentTo.contains(CommentParticipantType.TEAM) ? "checked=\"checked\"" : ""}>
                                                            </td>
                                                            <td>
                                                                <input 
                                                                    class="visibilityCheckbox giverCheckbox"
                                                                    type="checkbox"
                                                                    value="<%= CommentParticipantType.TEAM %>"
                                                                    ${comment.showGiverNameTo.contains(CommentParticipantType.TEAM) ? "checked=\"checked\"" : ""}>
                                                            </td>
                                                            <td>
                                                                <input 
                                                                    class="visibilityCheckbox recipientCheckbox"
                                                                    type="checkbox"
                                                                    value="<%= CommentParticipantType.TEAM %>"
                                                                    ${comment.recipientType == CommentParticipantType.TEAM ? "disabled=\"disabled\"" : ""}
                                                                    ${comment.showRecipientNameTo.contains(CommentParticipantType.TEAM) ? "checked=\"checked\"" : ""}>
                                                            </td>
                                                        </tr>
                                                    </c:if>
                                                    <c:if test="${comment.recipientType != CommentParticipantType.COURSE}">
                                                        <tr id="recipient-section${commentIdx}">
                                                            <td class="text-left">
                                                                <div data-toggle="tooltip"
                                                                    data-placement="top" title=""
                                                                    data-original-title="Control what students in the same section can view">
                                                                    ${comment.recipientType == CommentParticipantType.SECTION ? "Recipient Section" : "Recipient's Section"}</div>
                                                            </td>
                                                            <td>
                                                                <input 
                                                                    class="visibilityCheckbox answerCheckbox"
                                                                    type="checkbox"
                                                                    value="<%= CommentParticipantType.SECTION %>"
                                                                    ${comment.showCommentTo.contains(CommentParticipantType.SECTION) ? "checked=\"checked\"" : ""}>
                                                            </td>
                                                            <td>
                                                                <input 
                                                                    class="visibilityCheckbox giverCheckbox"
                                                                    type="checkbox"
                                                                    value="<%= CommentParticipantType.SECTION %>"
                                                                    ${comment.showGiverNameTo.contains(CommentParticipantType.SECTION) ? "checked=\"checked\"" : ""}>
                                                            </td>
                                                            <td>
                                                                <input 
                                                                    class="visibilityCheckbox recipientCheckbox"
                                                                    type="checkbox"
                                                                    value="<%= CommentParticipantType.SECTION %>"
                                                                    ${comment.recipientType == CommentParticipantType.SECTION ? "disabled=\"disabled\"" : ""}
                                                                    ${comment.showRecipientNameTo.contains(CommentParticipantType.SECTION) ? "checked=\"checked\"" : ""}>
                                                            </td>
                                                        </tr>
                                                    </c:if>
                                                    <tr id="recipient-course${commentIdx}">
                                                        <td class="text-left">
                                                            <div data-toggle="tooltip"
                                                                data-placement="top" title=""
                                                                data-original-title="Control what other students in this course can view">
                                                                ${comment.recipientType == CommentParticipantType.COURSE ? "Students in this course" : "Other students in this course"}</div>
                                                        </td>
                                                        <td>
                                                            <input 
                                                                class="visibilityCheckbox answerCheckbox"
                                                                type="checkbox" value="<%= CommentParticipantType.COURSE %>"
                                                                ${comment.showCommentTo.contains(CommentParticipantType.COURSE) ? "checked=\"checked\"" : ""}>
                                                        </td>
                                                        <td>
                                                            <input 
                                                                class="visibilityCheckbox giverCheckbox"
                                                                type="checkbox" value="<%= CommentParticipantType.COURSE %>"
                                                                ${comment.showGiverNameTo.contains(CommentParticipantType.COURSE) ? "checked=\"checked\"" : ""}>>
                                                        </td>
                                                        <td>
                                                            <input 
                                                                class="visibilityCheckbox recipientCheckbox"
                                                                type="checkbox" value="<%= CommentParticipantType.COURSE %>"
                                                                ${comment.recipientType == CommentParticipantType.COURSE ? "disabled=\"disabled\"" : ""}
                                                                ${comment.showRecipientNameTo.contains(CommentParticipantType.COURSE) ? "checked=\"checked\"" : ""}>
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
                                                                ${comment.showCommentTo.contains(CommentParticipantType.INSTRUCTOR) ? "checked=\"checked\"" : ""}>
                                                        </td>
                                                        <td>
                                                            <input
                                                                class="visibilityCheckbox giverCheckbox"
                                                                type="checkbox" value="<%= CommentParticipantType.INSTRUCTOR %>"
                                                                ${comment.showGiverNameTo.contains(CommentParticipantType.INSTRUCTOR) ? "checked=\"checked\"" : ""}>
                                                        </td>
                                                        <td>
                                                            <input
                                                                class="visibilityCheckbox recipientCheckbox"
                                                                type="checkbox" value="<%= CommentParticipantType.INSTRUCTOR %>"
                                                                ${comment.showRecipientNameTo.contains(CommentParticipantType.INSTRUCTOR) ? "checked=\"checked\"" : ""}>
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
                                                id="commentText${commentIdx}">${comment.commentText.value}</textarea>
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
                                        value="${comment.commentId}">
                                    <input type="hidden"
                                        name=<%= Const.ParamsNames.COURSE_ID %>
                                        value="${data.courseId}">
                                    <input type="hidden"
                                        name=<%= Const.ParamsNames.FROM_COMMENTS_PAGE %>
                                        value="true"> 
                                    <input type="hidden" 
                                        name=<%= Const.ParamsNames.COMMENTS_SHOWCOMMENTSTO %> 
                                        value="${data.removeBracketsForArrayString(comment.showCommentTo.toString())}">
                                    <input type="hidden" 
                                        name=<%= Const.ParamsNames.COMMENTS_SHOWGIVERTO %> 
                                        value="${data.removeBracketsForArrayString(comment.showGiverNameTo.toString())}">
                                    <input type="hidden" 
                                        name=<%= Const.ParamsNames.COMMENTS_SHOWRECIPIENTTO %> 
                                        value="${data.removeBracketsForArrayString(comment.showRecipientNameTo.toString())}">
                                    <input type="hidden"
                                        name="<%= Const.ParamsNames.USER_ID %>"
                                        value="${data.account.googleId}">
                                </c:if> <%--comment edit/delete control ends --%>
                            </form>
                        </li>
                    </c:forEach> <%-- student comments loop ends --%>
                </ul>
            </div>
        </c:forEach> <%-- recipient loop ends --%>
    </div>
</div>