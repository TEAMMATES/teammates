<%@ tag description="instructorFeedbacks and instructorFeedbackEdit - feedback session form header" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.util.FieldValidator" %>

<%@ attribute name="fsForm" type="teammates.ui.template.FeedbackSessionsForm" required="true"%>

<c:if test="${fsForm.feedbackSessionTypeEditable}">
    <div class="row">
        <h4 class="label-control col-md-2 text-md">Create new </h4>
        <div class="col-md-5">
            <div class="col-xs-10 tablet-no-padding" title="Select a session type here."
                data-toggle="tooltip" data-placement="top">
                <select class="form-control"
                    name="<%= Const.ParamsNames.FEEDBACK_SESSION_TYPE %>"
                    id="<%= Const.ParamsNames.FEEDBACK_SESSION_TYPE %>">
                    <c:forEach items="${fsForm.feedbackSessionTypeOptions}" var="option">
                        <option ${option.attributesToString}>
                            ${option.content}
                        </option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-xs-1">
                <h5>
                    <a href="/instructorHelp.jsp#fbSetupSession" target="_blank" rel="noopener noreferrer">
                        <span class="glyphicon glyphicon-info-sign"></span>
                    </a>
                </h5>
            </div>
        </div>
        <h4 class="label-control col-xs-12 col-md-1 text-md">Or: </h4>
        <div class="col-xs-12 col-md-3">
            <a id="button_copy" class="btn btn-info" style="vertical-align:middle;">Loading...</a>
        </div>
    </div>
</c:if>

<c:if test="${fsForm.editFsButtonsVisible}">
    <div class="row">
        <div class="col-sm-12">
            <span class="pull-right">
                <a class="btn btn-primary btn-sm" id="fsEditLink"
                    title="<%= Const.Tooltips.FEEDBACK_SESSION_EDIT %>"
                    data-toggle="tooltip" data-placement="top"
                    ${fsForm.editButtonTags.attributesToString}>
                    Edit
                </a>
                <button type="submit" id="fsSaveLink" style="display:none;" class="btn btn-primary btn-sm" onclick="return checkEditFeedbackSession(this.form);">
                    Save Changes
                </button>
                <a href="${fsForm.fsDeleteLink}"
                    data-course-id="${fsForm.courseId}"
                    data-feedback-session-name="${fsForm.fsName}"
                    title="<%= Const.Tooltips.FEEDBACK_SESSION_DELETE %>"
                    data-toggle="tooltip" data-placement="top"
                    class="btn btn-primary btn-sm" id="fsDeleteLink">
                    Delete
                </a>
                <span data-toggle="tooltip" title="Copy this feedback session to other courses" data-placement="top">
                    <a class="btn btn-primary btn-sm" href="#"
                        data-actionlink="${fsForm.copyToLink}"
                        data-courseid="${fsForm.courseId}"
                        data-fsname="${fsForm.fsName}"
                        data-target="#fsCopyModal"
                        data-placement="top" id="button_fscopy"
                        data-toggle="modal">
                        Copy
                    </a>
                </span>
            </span>
        </div>
    </div>
</c:if>