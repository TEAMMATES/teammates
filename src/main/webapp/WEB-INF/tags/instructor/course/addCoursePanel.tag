<%@ tag description="Add New Course Panel of Course Page" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.util.FieldValidator" %>
<%@ attribute name="googleId" required="true" %>
<%@ attribute name="courseIdToShow" required="true" %>
<%@ attribute name="courseNameToShow" required="true" %>

<div class="panel panel-primary">
    <div class="panel-body fill-plain">
        <form method="get" action="<%=Const.ActionURIs.INSTRUCTOR_COURSE_ADD%>" name="form_addcourse" class="form form-horizontal">
            <input type="hidden" id="<%=Const.ParamsNames.INSTRUCTOR_ID%>" name="<%=Const.ParamsNames.INSTRUCTOR_ID%>" value="${googleId}">
            <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="${googleId}">
            <div class="form-group">
                <label class="col-sm-3 control-label">Course ID:</label>
                <div class="col-sm-3">
                    <input class="form-control" type="text"
                        name="<%=Const.ParamsNames.COURSE_ID%>" id="<%=Const.ParamsNames.COURSE_ID%>"
                        value="${courseIdToShow}" data-toggle="tooltip" data-placement="top" 
                        title="Enter the identifier of the course, e.g.CS3215-2013Semester1."
                        maxlength=<%=FieldValidator.COURSE_ID_MAX_LENGTH%> tabindex="1"
                        placeholder="e.g. CS3215-2013Semester1" />
                </div>
            </div>
            <div class="form-group">
                <label class="col-sm-3 control-label">Course Name:</label>
                <div class="col-sm-9">
                    <input class="form-control" type="text"
                        name="<%=Const.ParamsNames.COURSE_NAME%>" id="<%=Const.ParamsNames.COURSE_NAME%>"
                        value="${courseNameToShow}" data-toggle="tooltip" data-placement="top" 
                        title="Enter the name of the course, e.g. Software Engineering."
                        maxlength=<%=FieldValidator.COURSE_NAME_MAX_LENGTH%> tabindex=2
                        placeholder="e.g. Software Engineering" />
                </div>
            </div>
            <div class="form-group">
                <div class="col-sm-offset-3 col-sm-9">
                    <input id="btnAddCourse" type="submit" class="btn btn-primary"
                        onclick="return verifyCourseData();" value="Add Course" tabindex="3">
                </div>
            </div>
        </form>
    </div>
</div>