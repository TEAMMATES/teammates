<%@ tag description="instructorCourseEdit - Course Info Panel" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.util.FieldValidator" %>
<%@ attribute name="editCourseButton" type="teammates.ui.template.ElementTag" required="true" %>
<%@ attribute name="deleteCourseButton" type="teammates.ui.template.ElementTag" required="true" %>
<%@ attribute name="course" type="teammates.common.datatransfer.attributes.CourseAttributes" required="true" %>

<div class="panel panel-primary">
    <div class="panel-heading">
        <strong>Course:</strong>
        
        <div class="pull-right">
            <a ${editCourseButton.attributesToString}>
                ${editCourseButton.content}
            </a>
            
            <a ${deleteCourseButton.attributesToString}>
                ${deleteCourseButton.content}
            </a>
        </div>
    </div>
    
    <div class="panel-body fill-plain">
        <form action="<%=Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_SAVE%>" method="post" id="formEditcourse" class="form form-horizontal">
            <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="${course.id}">
            <input type="hidden" name="<%=Const.ParamsNames.INSTRUCTOR_ID%>" value="${data.account.googleId}">
            
            <div class="form-group">
                <label class="col-sm-3 control-label">Course ID:</label>
                <div class="col-sm-3">
                    <input type="text" class="form-control"
                            name="<%=Const.ParamsNames.COURSE_ID%>" id="<%=Const.ParamsNames.COURSE_ID%>"
                            value="${course.id}"
                            data-toggle="tooltip" data-placement="top" title="Identifier of the course, e.g.CS3215-Sem1."
                            maxlength="<%=FieldValidator.COURSE_ID_MAX_LENGTH%>" tabindex="1" disabled>
                </div>
            </div>
            
            <div class="form-group">
                <label class="col-sm-3 control-label">Course Name:</label>
                <div class="col-sm-9">
                    <input type="text" class="form-control"
                            name="<%=Const.ParamsNames.COURSE_NAME%>" id="<%=Const.ParamsNames.COURSE_NAME%>"
                            value="${course.name}"
                            data-toggle="tooltip" data-placement="top" title="The name of the course, e.g. Software Engineering."
                            maxlength="<%=FieldValidator.COURSE_NAME_MAX_LENGTH%>" tabindex="2" disabled>
                </div>
            </div>
            
            <div class="form-group">
                <label class="col-sm-3 control-label">Time Zone:</label>
                <div class="col-sm-6">
                    <select class="form-control"
                            name="<%=Const.ParamsNames.COURSE_TIME_ZONE%>" id="<%=Const.ParamsNames.COURSE_TIME_ZONE%>"
                            data-toggle="tooltip" data-placement="top" title="The time zone for the course, e.g. Asia/Singapore."
                            tabindex="3" disabled>
                        <option value="">Select a time zone...</option>
                    </select>
                </div>
                <div class="col-sm-1">
                    <input type="button" class="btn btn-primary" id="auto-detect-time-zone" disabled value="Auto-Detect Time Zone">
                </div>
            </div>

            <div class="form-group">
                <div class=" col-sm-12 align-center">
                    <input type="submit" class="btn btn-primary" id="btnSaveCourse" name="btnSaveCourse"
                            style="display:none;" value="Save Changes">
                </div>
            </div>
            
            <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="${data.account.googleId}">
        </form>
    </div>
</div>