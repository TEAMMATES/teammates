<%@ tag description="instructorFeedbacks - feedback sessions preview form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>

<%@ attribute name="previewForm" type="teammates.ui.template.FeedbackSessionPreviewForm" required="true"%>


<div class="container">
    <div class="well well-plain inputTable" id="questionPreviewTable">
        <div class="row">
            <form class="form-horizontal">
                <label class="control-label col-sm-2 text-right">
                    Preview Session:
                </label>
            </form>
            <div class="col-sm-5" data-toggle="tooltip" data-placement="top" title="<%= Const.Tooltips.FEEDBACK_PREVIEW_ASSTUDENT %>">
                <form method="post" action="<%= Const.ActionURIs.INSTRUCTOR_FEEDBACK_PREVIEW_ASSTUDENT %>"
                    name="form_previewasstudent" class="form_preview" target="_blank">
                    
                    <div class="col-sm-6">
                        <select class="form-control" name="<%= Const.ParamsNames.PREVIEWAS %>">
                            <c:forEach items="${previewForm.studentToPreviewAsOptions}" var="option">
                                <option ${option.attributesToString}>
                                    ${option.content}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="${previewForm.fsName}">
                    <input type="hidden" name="<%= Const.ParamsNames.COURSE_ID %>" value="${previewForm.courseId}">
                    <div class="col-sm-6">
                        <input id="button_preview_student" type="submit" class="btn btn-primary" value="Preview as Student"
                            <c:if test="${empty previewForm.studentToPreviewAsOptions}"> disabled="disabled" style="background: #66727A;"
                            </c:if>
                        >
                    </div>
                    <input type="hidden" name="<%= Const.ParamsNames.USER_ID %>" value="${data.account.googleId}">
                </form>
            </div>
            <div class="col-sm-5" data-toggle="tooltip" data-placement="top" title="<%= Const.Tooltips.FEEDBACK_PREVIEW_ASINSTRUCTOR %>">
                <form method="post" action="<%= Const.ActionURIs.INSTRUCTOR_FEEDBACK_PREVIEW_ASINSTRUCTOR %>"
                    name="form_previewasinstructor" class="form_preview" target="_blank">
                    <div class="col-sm-6">
                        <select class="form-control" name="<%= Const.ParamsNames.PREVIEWAS %>">
                            <c:forEach items="${previewForm.instructorToPreviewAsOptions}" var="option">
                                <option ${option.attributesToString}>
                                    ${option.content}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="${previewForm.fsName}">
                    <input type="hidden" name="<%= Const.ParamsNames.COURSE_ID %>" value="${previewForm.courseId}">
                    <div class="col-sm-6">
                        <input id="button_preview_instructor" type="submit" class="btn btn-primary" value="Preview as Instructor">
                    </div>
                    <input type="hidden" name="<%= Const.ParamsNames.USER_ID %>" value="${data.account.googleId}">
                </form>
            </div>
        </div>
    </div>
</div>
