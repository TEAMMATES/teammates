<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="teammates.common.util.Const" %>
<%@ page import="teammates.common.datatransfer.CourseAttributes"%>
<%@ page import="teammates.common.util.FieldValidator"%>
<%@ page import="teammates.common.datatransfer.InstructorAttributes"%>
<%@ page import="static teammates.ui.controller.PageData.sanitizeForHtml"%>
<%@ page import="teammates.ui.controller.InstructorCourseEditPageData"%>
<%
    InstructorCourseEditPageData data = (InstructorCourseEditPageData)request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
    <link rel="shortcut icon" href="/favicon.png">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>TEAMMATES - Instructor</title>
    <link rel="stylesheet" href="/bootstrap/css/bootstrap.min.css" type="text/css"/>
    <link rel="stylesheet" href="/bootstrap/css/bootstrap-theme.min.css" type="text/css"/>
    <link rel="stylesheet" href="/stylesheets/teammatesCommon.css" type="text/css"/>
   
    <script type="text/javascript" src="/js/googleAnalytics.js"></script>
    <script type="text/javascript" src="/js/jquery-minified.js"></script>
    <script type="text/javascript" src="/js/common.js"></script>
    <script type="text/javascript" src="/bootstrap/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="/js/instructor.js"></script>
    <script type="text/javascript" src="/js/instructorCourseEdit.js"></script>
    <script type="text/javascript" src="/js/instructorCourseEditAjax.js"></script>
    <jsp:include page="../enableJS.jsp"></jsp:include>   

    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]--> 
</head>

<body>
    <jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_HEADER%>" />

    <div class="container theme-showcase" id="frameBodyWrapper">
        <div id="topOfPage"></div>
        <div id="headerOperation">
            <h1>Edit Course Details</h1>
        </div>
            
        <div class="panel panel-primary">
            <div class="panel-heading">
                <strong>Course:</strong>
                <a href="<%=data.getInstructorCourseDeleteLink(data.course.id, false)%>"
                    class="btn btn-primary btn-xs pull-right" id="courseDeleteLink"
                    data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.COURSE_DELETE%>"
                    onclick="return toggleDeleteCourseConfirmation('<%=data.course.id%>');"
                    <% if (!data.currentInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE)) {%>
                        disabled="disabled"
                    <% } %>
                        >
                    <span class="glyphicon glyphicon-trash"></span>
                    Delete</a>
            </div>
            <div class="panel-body fill-plain">
                <form action="<%=Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_SAVE%>" method="post" id="formEditcourse" class="form form-horizontal">
                    <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="<%=data.course.id%>">
                    <input type="hidden" name="<%=Const.ParamsNames.INSTRUCTOR_ID%>" value="<%=data.account.googleId%>">
                    
                    <div class="form-group">
                        <label class="col-sm-3 control-label">Course ID:</label>
                        <div class="col-sm-3"><input type="text" class="form-control"
                            name="<%=Const.ParamsNames.COURSE_ID%>" id="<%=Const.ParamsNames.COURSE_ID%>"
                            value="<%=(data.course.id==null ? "" : sanitizeForHtml(data.course.id))%>"
                            data-toggle="tooltip" data-placement="top" title="Identifier of the course, e.g.CS3215-Sem1."
                            maxlength=<%=FieldValidator.COURSE_ID_MAX_LENGTH%> tabindex="1" disabled="disabled">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">Course Name:</label>
                        <div class="col-sm-9"><input type="text" class="form-control"
                            name="<%=Const.ParamsNames.COURSE_NAME%>" id="<%=Const.ParamsNames.COURSE_NAME%>"
                            value="<%=(data.course.name==null ? "" : sanitizeForHtml(data.course.name))%>"
                            data-toggle="tooltip" data-placement="top" title="The name of the course, e.g. Software Engineering."
                            maxlength=<%=FieldValidator.COURSE_NAME_MAX_LENGTH%> tabindex=2 disabled="disabled">
                        </div>
                    </div>
                    <div class="form-group">
                        <div class=" col-sm-12 align-center">
                            <input type="submit" class="button" id="btnSaveCourse" name="btnSaveCourse"
                            style="display:none;" value="Save Changes" onclick="return verifyCourseData();">
                        </div>
                    </div>
                    <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
                </form>
            </div>
        </div>

        <br>
        <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
        
        <div class="pull-right">
            <a href="../instructorHelp.html#editCourse" class="small" target="_blank">More about configuring access permissions</a>
        </div>
        <br><br>
        
        <%
            for (int i = 0; i < data.instructorList.size(); i++) {
                InstructorAttributes instructor = data.instructorList.get(i);
                int index;
                if(data.index == -1){
                    index = i + 1;
                } else {
                    index = data.index;
                }
        %>
        <div class="panel panel-primary">
            <div class="panel-heading">
                <strong>Instructor <%=index%>:</strong>
                <div class="pull-right">
                    <div class="display-icon" style="display:inline;">
                    
                    </div>
                    <% if (instructor.googleId == null) { %>
                        <a href="<%=data.getInstructorCourseInstructorRemindLink(instructor.courseId, instructor.email)%>" id="instrRemindLink<%=index%>"
                            class="btn btn-primary btn-xs"
                            data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.COURSE_INSTRUCTOR_REMIND%>"
                            onclick="return toggleSendRegistrationKey('<%=instructor.courseId%>','<%=instructor.email%>);"
                            <% if (!data.currentInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR)) {%>
                            disabled="disabled"
                            <% } %>
                            >
                            <span class="glyphicon glyphicon-envelope"></span> Resend Invite</a>
                    <% } %>
                    <form style="display:none;" id="edit-<%=index%>" class="editForm" action="<%=Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE%>">
                        <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID %>" value="<%=instructor.courseId%>">
                        <input type="hidden" name="<%=Const.ParamsNames.INSTRUCTOR_EMAIL%>" value="<%=instructor.email%>">
                        <input type="hidden" name="<%=Const.ParamsNames.COURSE_EDIT_MAIN_INDEX%>" value="<%=index%>">
                        <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId %>">
                    </form>
                    <a  href="javascript:;" id="instrEditLink<%=index%>" class="btn btn-primary btn-xs"
                        data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.COURSE_INSTRUCTOR_EDIT%>"
                        <% if (!data.currentInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR)) {%>
                        disabled="disabled"
                        <% } %>
                        >
                        <span class="glyphicon glyphicon-pencil"></span> Edit</a>
                    <a href="<%=data.getInstructorCourseInstructorDeleteLink(instructor.courseId, instructor.email)%>" id="instrDeleteLink<%=index%>"
                        class="btn btn-primary btn-xs"
                        data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.COURSE_INSTRUCTOR_DELETE%>"
                        onclick="return toggleDeleteInstructorConfirmation('<%=instructor.courseId%>','<%=instructor.email%>', <%=instructor.email.equals(data.account.email)%>);"
                        <% if (!data.currentInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR)) {%>
                        disabled="disabled"
                        <% } %>
                        >
                        <span class="glyphicon glyphicon-trash"></span> Delete</a>
                </div>
            </div>

            <div class="panel-body">
                <form method="post" action="<%=Const.ActionURIs.INSTRUCTOR_COURSE_INSTRUCTOR_EDIT_SAVE%>"
                    id="formEditInstructor<%=index%>>" name="formEditInstructors" class="form form-horizontal" >
                    <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="<%=instructor.courseId%>">
                    <% if (instructor.googleId != null) { %>
                    <input type="hidden" name="<%=Const.ParamsNames.INSTRUCTOR_ID%>" value="<%=instructor.googleId%>">
                    <% } %>
                    <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
                    
                    <div id="instructorTable<%=index%>">
                        <% if (instructor.googleId != null) { %>
                        <div class="form-group">
                            <label class="col-sm-3 control-label">Google ID:</label>
                            <div class="col-sm-9"><input class="form-control immutable" type="text" id="<%=Const.ParamsNames.INSTRUCTOR_ID+index%>"
                                value="<%=instructor.googleId%>"
                                maxlength=<%=FieldValidator.GOOGLE_ID_MAX_LENGTH%> tabindex=3
                                disabled="disabled">
                            </div>
                        </div>
                        <% } %>
                        <div class="form-group">
                            <label class="col-sm-3 control-label">Name:</label>
                            <div class="col-sm-9"><input class="form-control" type="text"
                                name="<%=Const.ParamsNames.INSTRUCTOR_NAME%>" id="<%=Const.ParamsNames.INSTRUCTOR_NAME+index%>"
                                value="<%=instructor.name%>"
                                data-toggle="tooltip" data-placement="top" title="Enter the name of the instructor."
                                maxlength=<%=FieldValidator.PERSON_NAME_MAX_LENGTH%> tabindex=4
                                disabled="disabled">
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label">Email:</label>
                            <div class="col-sm-9"><input class="form-control" type="text"
                                name="<%=Const.ParamsNames.INSTRUCTOR_EMAIL%>" id="<%=Const.ParamsNames.INSTRUCTOR_EMAIL+index%>"
                                value="<%=instructor.email%>"
                                data-toggle="tooltip" data-placement="top" title="Enter the Email of the instructor."
                                maxlength=<%=FieldValidator.EMAIL_MAX_LENGTH%> tabindex=5
                                disabled="disabled"
                                <% if (instructor.googleId == null) { %>
                                    readonly="readonly"
                                <% } %>
                                >
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label">
                                 <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_IS_DISPLAYED_TO_STUDENT%>" value="true"
                                     <% if (instructor.isDisplayedToStudents) { %>
                                     checked="checked"
                                     <% } %>
                                     data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.INSTRUCTOR_DISPLAYED_TO_STUDENT%>"
                                     disabled="disabled">
                                     Display to students as:</label>
                            <div class="col-sm-9">
                                <input class="form-control" type="text" name="<%=Const.ParamsNames.INSTRUCTOR_DISPLAY_NAME%>" 
                                    placeholder="E.g.Co-lecturer, Teaching Assistant" value="<%=instructor.displayedName%>"
                                    data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.INSTRUCTOR_DISPLAYED_AS%>"
                                    disabled="disabled"/>
                            </div>
                        </div>
                        <div id="accessControlInfoForInstr<%=index%>">
                            <div class="form-group">
                                <label class="col-sm-3 control-label">Access Level:</label>
                                <div class="col-sm-9">
                                    <p class="form-control-static">
                                        <span><%=instructor.role%></span>
                                        <% if (!instructor.role.equals(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM)) { %>
                                        <a href="javascript:;" onclick="showInstructorRoleModal('<%=instructor.role%>')">
                                            &nbsp;View Details</a>
                                        <% } %>
                                    </p>
                                </div>
                            </div>
                        </div>
                       
                        <div id="accessControlEditDivForInstr<%=index%>">
                             <% if(data.isAccessControlDisplayed) { %> 
                            <div class="form-group">
                                <div class="col-sm-3">
                                    <label class="control-label pull-right">Access-level</label>
                                </div>
                                <div class="col-sm-9">
                                    <input type="radio" name="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>" id="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>forinstructor<%=index%>"
                                        value="<%=Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER%>">
                                        &nbsp;Co-owner: Can do everything
                                        &nbsp;<a href="javascript:;" 
                                                  onclick="showInstructorRoleModal('<%=Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER%>')">
                                                  View Details</a><br>
                                    <input type="radio" name="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>" id="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>forinstructor<%=index%>"
                                        value="<%=Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER%>">
                                        &nbsp;Manager: Can do everything except for deleting the course
                                        &nbsp;<a href="javascript:;" 
                                                  onclick="showInstructorRoleModal('<%=Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER%>')">
                                                  View Details</a><br>
                                    <input type="radio" name="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>" id="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>forinstructor<%=index%>"
                                        value="<%=Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER%>">
                                        &nbsp;Observer: Can only view information(students, submissions, comments etc.).
                                        &nbsp;Cannot edit/delete/submit anything.
                                        &nbsp;<a href="javascript:;"
                                                  onclick="showInstructorRoleModal('<%=Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER%>')">
                                                  View Details</a><br>
                                    <input type="radio" name="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>" id="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>forinstructor<%=index%>"
                                        value="<%=Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_TUTOR%>">
                                        &nbsp;Tutor: Can view student details, give/view comments, submit/view responses for sessions
                                        &nbsp;<a href="javascript:;"
                                                  onclick="showInstructorRoleModal('<%=Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_TUTOR%>')">
                                                  View Details</a><br>
                                    <input type="radio" name="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>" id="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>forinstructor<%=index%>"
                                        value="<%=Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM%>">
                                        &nbsp;Custom: No access by default. Any access needs to be granted explicitly.<br>
                                </div>
                            </div>
                            <div id="tunePermissionsDivForInstructor<%=index%>" style="display: none;">
                                <div class="form-group">
                                    <div class="col-xs-12">
                                        <div class="panel panel-info">
                                            <div class="panel-heading">
                                                <strong>In general, this instructor can</strong>
                                            </div>
                                            <div class="panel-body">
                                                <div class="col-sm-3">
                                                    <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE%>" value="true" 
                                                    <%if (instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE)) {%>
                                                        checked="checked"
                                                    <%}%>
                                                    /> Edit/Delete Course
                                                </div>
                                                <div class="col-sm-3">
                                                    <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR%>" value="true"
                                                    <%if (instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR)) {%>
                                                        checked="checked"
                                                    <%}%>
                                                    /> Add/Edit/Delete Instructors
                                                </div>
                                                <div class="col-sm-3">
                                                    <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION%>" value="true"
                                                    <%if (instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION)) {%>
                                                        checked="checked"
                                                    <%}%>
                                                    /> Create/Edit/Delete Sessions
                                                </div>
                                                <div class="col-sm-3">
                                                    <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT%>" value="true"
                                                    <%if (instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT)) {%>
                                                        checked="checked"
                                                    <%}%>
                                                    /> Enroll/Edit/Delete Students
                                                </div>
                                                <br><br>
                                                <div class="col-sm-6 border-right-gray">
                                                    <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS%>" value="true"
                                                    <%if (instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS)) {%>
                                                        checked="checked"
                                                    <%}%>
                                                    /> View Students' Details<br>
                                                    <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS%>" value="true"
                                                    <%if (instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS)) {%>
                                                        checked="checked"
                                                    <%}%>
                                                    /> Give Comments for Students<br>
                                                    <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS%>" value="true"
                                                    <%if (instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS)) {%>
                                                        checked="checked"
                                                    <%}%>
                                                    /> View Others' Comments on Students<br>
                                                    <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS%>" value="true"
                                                    <%if (instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS)) {%>
                                                        checked="checked"
                                                    <%}%>
                                                    /> Edit/Delete Others' Comments on Students<br>
                                                </div>
                                                <div class="col-sm-5 col-sm-offset-1">
                                                    <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS%>" value="true"
                                                    <%if (instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS)) {%>
                                                        checked="checked"
                                                    <%}%>
                                                    /> Sessions: Submit Responses and Add Comments<br>
                                                    <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS%>" value="true"
                                                    <%if (instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS)) {%>
                                                        checked="checked"
                                                    <%}%>
                                                    /> Sessions: View Responses and Comments<br>
                                                    <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS%>" value="true"
                                                    <%if (instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS)) {%>
                                                        checked="checked"
                                                    <%}%>
                                                    /> Sessions: Edit/Delete Responses/Comments by Others<br>
                                                </div>
                                            </div>
                                        </div>
                                        <%
                                        	if (!data.sectionNames.isEmpty()) {
                                        %>
                                            <%
                                            	for (int j=0;j<data.sectionNames.size();j++) {
                                            %>
                                            <div id="tuneSectionPermissionsDiv<%=j%>ForInstructor<%=index%>" 
                                                <%if (!instructor.privileges.isSectionSpecial(data.sectionNames.get(j))) {%> 
                                                style="display: none;"
                                                <%}%>
                                                >
                                                <div class="panel panel-info">
                                                    <div class="panel-heading">
                                                        <div class="row">
                                                            <div class="col-sm-2">
                                                                <p><strong>But in section(s)</strong></p>
                                                            </div>
                                                            <div class="col-sm-9">
                                                                <% for (int sectionIdx = 0; sectionIdx<data.sectionNames.size(); sectionIdx++) { %>
                                                                    <% if (sectionIdx%3 == 0) { %>
                                                                    <div class="col-sm-12">
                                                                    <% } %>
                                                                    <div class="col-sm-4">
                                                                        <input type="checkbox" 
                                                                            name="<%=Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + j + Const.ParamsNames.INSTRUCTOR_SECTION + sectionIdx%>"
                                                                            value="<%=data.sectionNames.get(sectionIdx)%>"
                                                                            <% if (sectionIdx == j) { %>
                                                                            checked="checked"
                                                                            <% } %>
                                                                            >
                                                                        <%=data.sectionNames.get(sectionIdx)%>
                                                                        </div>
                                                                    <% if (sectionIdx%3 == 2) { %>
                                                                    </div>
                                                                    <% } %>
                                                                <% } %>
                                                                <% if (data.sectionNames.size()%3 != 0) { %>
                                                                </div>
                                                                <% } %>
                                                            </div>
                                                            <div class="col-sm-1">
                                                            <a href="javascript:;" onclick="hideTuneSectionPermissionsDiv(<%=index%>, <%=j%>)" class="pull-right">
                                                                <span class="glyphicon glyphicon-trash"></span></a>
                                                            </div>
                                                        </div>
                                                        <br>
                                                        <div class="row">
                                                            <div class="col-sm-12">
                                                                <p><strong> the instructor can only,</strong></p>
                                                            </div>
                                                        </div>
                                                        <% String valueForSection = instructor.privileges.isSectionSpecial(data.sectionNames.get(j)) ? "true" : "false";%>
                                                        <input type="hidden" name="is<%=Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + j%>set" value="<%=valueForSection%>"/>
                                                    </div>
                                                    <div class="panel-body">
                                                        <br>
                                                        <div class="col-sm-6 border-right-gray">
                                                            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS + 
                                                            Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + j%>" value="true"
                                                            <%if (instructor.isAllowedForPrivilege(data.sectionNames.get(j), Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS)) {%>
                                                                checked="checked"
                                                            <%}%>
                                                            /> View Students' Details<br>
                                                            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS + 
                                                            Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + j%>" value="true"
                                                            <%if (instructor.isAllowedForPrivilege(data.sectionNames.get(j), Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS)) {%>
                                                                checked="checked"
                                                            <%}%>
                                                            /> Give Comments for Students<br>
                                                            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS + 
                                                            Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + j%>" value="true"
                                                            <%if (instructor.isAllowedForPrivilege(data.sectionNames.get(j), Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS)) {%>
                                                                checked="checked"
                                                            <%}%>
                                                            /> View Others' Comments on Students<br>
                                                            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS + 
                                                            Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + j%>" value="true"
                                                            <%if (instructor.isAllowedForPrivilege(data.sectionNames.get(j), Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS)) {%>
                                                                checked="checked"
                                                            <%}%>
                                                            /> Edit/Delete Others' Comments on Students<br><br>
                                                        </div>
                                                        <div class="col-sm-5 col-sm-offset-1">
                                                            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS + 
                                                            Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + j%>" value="true"
                                                            <%if (instructor.isAllowedForPrivilege(data.sectionNames.get(j), Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS)) {%>
                                                                checked="checked"
                                                            <%}%>
                                                            /> Sessions: Submit Responses and Add Comments<br>
                                                            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS + 
                                                            Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + j%>" value="true"
                                                            <%if (instructor.isAllowedForPrivilege(data.sectionNames.get(j), Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS)) {%>
                                                                checked="checked"
                                                            <%}%>
                                                            /> Sessions: View Responses and Comments<br>
                                                            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS + 
                                                            Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + j%>" value="true"
                                                            <%if (instructor.isAllowedForPrivilege(data.sectionNames.get(j), Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS)) {%>
                                                                checked="checked"
                                                            <%}%>
                                                            /> Sessions: Edit/Delete Responses/Comments by Others<br><br>
                                                        </div>
                                                        <a href="javascript:;" 
                                                            <%if (!instructor.privileges.isSessionsInSectionSpecial(data.sectionNames.get(j))) {%> 
                                                            onclick="showTuneSessionnPermissionsDiv(<%=index%>, <%=j%>)"
                                                            <%} else {%>
                                                            onclick="hideTuneSessionnPermissionsDiv(<%=index%>, <%=j%>)"
                                                            <%}%>
                                                            id="toggleSessionLevelInSection<%=j%>ForInstructor<%=index%>"
                                                            class="small col-sm-5">
                                                            <%
                                                            	if (!instructor.privileges.isSessionsInSectionSpecial(data.sectionNames.get(j))) {
                                                            %> 
                                                            Give different permissions for sessions in this section
                                                            <%
                                                            	} else {
                                                            %>
                                                            Hide session-level permissions
                                                            <%
                                                            	}
                                                            %></a>      
                                                        <div id="tuneSessionPermissionsDiv<%=j%>ForInstructor<%=index%>" class="row"
                                                            <%if (!instructor.privileges.isSessionsInSectionSpecial(data.sectionNames.get(j))) {%>
                                                            style="display: none;"
                                                            <%}%>
                                                            >
                                                            <%
                                                            	if (!instructor.privileges.isSessionsInSectionSpecial(data.sectionNames.get(j))) {
                                                            %> 
                                                            <input type="hidden" name="is<%=Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + j%>sessionsset" value="false"/>
                                                            <%
                                                            	} else {
                                                            %>
                                                            <input type="hidden" name="is<%=Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + j%>sessionsset" value="true"/>
                                                            <%
                                                            	}
                                                            %>
                                                            <table class="table table-striped">
                                                                <thead>
                                                                    <tr>
                                                                        <td>SessionName</td>
                                                                        <td>Submit Responses and Add Comments</td>
                                                                        <td>View Responses and Comments</td>
                                                                        <td>Edit/Delete Responses/Comments by Others</td>
                                                                    </tr>
                                                                </thead>
                                                                <tbody>
                                                                    <% if (data.evalNames.isEmpty() && data.feedbackNames.isEmpty()) {%>
                                                                            <tr>
                                                                                <td colspan="4" class="text-center text-bold">No sessions in this course for you to configure</td>
                                                                            </tr>
                                                                    <% } %>
                                                                    <%
                                                                    	for (String evalName : data.evalNames) {
                                                                    %>
                                                                    <tr>
                                                                        <td><%=evalName%></td>
                                                                        <td class="align-center">
                                                                            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS 
                                                                            + Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + j + "feedback" + Const.EVAL_PREFIX_FOR_INSTRUCTOR_PRIVILEGES + evalName%>" value="true"
                                                                            <%if (instructor.isAllowedForPrivilege(data.sectionNames.get(j),
                                                                            		Const.EVAL_PREFIX_FOR_INSTRUCTOR_PRIVILEGES+evalName,
                                                                            		Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS)) {%>
                                                                            checked="checked"
                                                                            <%}%>
                                                                            />
                                                                        </td>
                                                                        <td class="align-center">
                                                                            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS
                                                                            + Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + j + "feedback" + Const.EVAL_PREFIX_FOR_INSTRUCTOR_PRIVILEGES + evalName%>" value="true"
                                                                            <%if (instructor.isAllowedForPrivilege(data.sectionNames.get(j),
                                                                            		Const.EVAL_PREFIX_FOR_INSTRUCTOR_PRIVILEGES+evalName,
                                                                            		Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS)) {%>
                                                                            checked="checked"
                                                                            <%}%>
                                                                            />
                                                                        </td>
                                                                        <td class="align-center">
                                                                            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS
                                                                            + Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + j + "feedback" + Const.EVAL_PREFIX_FOR_INSTRUCTOR_PRIVILEGES + evalName%>" value="true"
                                                                            <%if (instructor.isAllowedForPrivilege(data.sectionNames.get(j),
                                                                            		Const.EVAL_PREFIX_FOR_INSTRUCTOR_PRIVILEGES+evalName,
                                                                            		Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS)) {%>
                                                                            checked="checked"
                                                                            <%}%>
                                                                            />
                                                                        </td>
                                                                    </tr>
                                                                    <%
                                                                    	}
                                                                    %>
                                                                    <%
                                                                    	for (String feedbackName : data.feedbackNames) {
                                                                    %>
                                                                    <tr>
                                                                        <td><%=feedbackName%></td>
                                                                        <td class="align-center">
                                                                            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS 
                                                                            + Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + j + "feedback" + feedbackName%>" value="true"
                                                                            <%if (instructor.isAllowedForPrivilege(data.sectionNames.get(j),
                                                                                    feedbackName, Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS)) {%>
                                                                            checked="checked"
                                                                            <%}%>
                                                                            />
                                                                        </td>
                                                                        <td class="align-center">
                                                                            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS
                                                                            + Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + j + "feedback" + feedbackName%>" value="true"
                                                                            <%if (instructor.isAllowedForPrivilege(data.sectionNames.get(j),
                                                                                    feedbackName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS)) {%>
                                                                            checked="checked"
                                                                            <%}%>
                                                                            />
                                                                        </td>
                                                                        <td class="align-center">
                                                                            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS
                                                                            + Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + j + "feedback" + feedbackName%>" value="true"
                                                                            <%if (instructor.isAllowedForPrivilege(data.sectionNames.get(j),
                                                                                    feedbackName, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS)) {%>
                                                                            checked="checked"
                                                                            <%}%>
                                                                            />
                                                                        </td>
                                                                    </tr>
                                                                    <%
                                                                    	}
                                                                    %>
                                                                </tbody>
                                                            </table>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <%
                                            	}
                                            %>
                                            <a href="javascript:;" onclick="showTuneSectionPermissionsDiv(<%=index%>, <%=instructor.privileges.numberOfSectionsSpecial()%>)" class="small" 
                                                id="addSectionLevelForInstructor<%=index%>"
                                                <%if (instructor.privileges.numberOfSectionsSpecial() >= data.sectionNames.size()) {%>
                                                style="display: none;"
                                                <%}%>
                                                >Give different permissions for a specific section</a>
                                        <%
                                        	}
                                        %>
                                    </div>
                                </div>
                            </div>
                            <% } %>
                        </div>
                        <div class="form-group">
                            <div class="align-center">
                                <input id="btnSaveInstructor<%=index%>" type="submit" class="btn btn-primary"
                                style="display:none;" value="Save changes" tabindex="6">
                            </div>
                        </div>
                    </div>
                </form>
            </div>
        </div>
        
        <br>
        <br>
        <%
        	}
        %>
        
        <div class="align-center">
            <input id="btnShowNewInstructorForm" class="btn btn-primary" value="Add New Instructor" 
                onclick="showNewInstructorForm()"
                <%if (!data.currentInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR)) {%>
                      disabled="disabled"
                <%}%>>
        </div>
        
        <div class="panel panel-primary" id="panelAddInstructor" style="display: none;">
            <div class="panel-heading">
                <strong>Instructors <%=data.instructorList.size()+1%>:</strong>
            </div>

            <div class="panel-body fill-plain">
                <form method="post" action="<%=Const.ActionURIs.INSTRUCTOR_COURSE_INSTRUCTOR_ADD%>" name="formAddInstructor" 
                    class="form form-horizontal" id="formAddInstructor">
                    <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="<%=data.course.id%>">
                    <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
                    
                    <div id="instructorAddTable">
                        <div class="form-group">
                            <label class="col-sm-3 control-label">Name:</label>
                            <div class="col-sm-9"><input class="form-control" type="text"
                                name="<%=Const.ParamsNames.INSTRUCTOR_NAME%>" id="<%=Const.ParamsNames.INSTRUCTOR_NAME%>"
                                data-toggle="tooltip" data-placement="top" title="Enter the name of the instructor."
                                maxlength=<%=FieldValidator.PERSON_NAME_MAX_LENGTH%> tabindex=8/>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label">Email:</label>
                            <div class="col-sm-9"><input class="form-control" type="text"
                                name="<%=Const.ParamsNames.INSTRUCTOR_EMAIL%>" id="<%=Const.ParamsNames.INSTRUCTOR_EMAIL%>"
                                data-toggle="tooltip" data-placement="top" title="Enter the Email of the instructor."
                                maxlength=<%=FieldValidator.EMAIL_MAX_LENGTH%> tabindex=9/>
                            </div>
                        </div>
                        <div id="accessControlEditDivForInstr<%=data.instructorList.size()+1%>">
                            <div class="form-group">
                                <label class="col-sm-3 control-label">
                                    <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_IS_DISPLAYED_TO_STUDENT%>" value="true" checked="checked"
                                       data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.INSTRUCTOR_DISPLAYED_TO_STUDENT%>">
                                    Display to students as:</label>
                                <div class="col-sm-9">
                                    <input class="form-control" type="text" name="<%=Const.ParamsNames.INSTRUCTOR_DISPLAY_NAME%>" 
                                    placeholder="E.g.Co-lecturer, Teaching Assistant"
                                    data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.INSTRUCTOR_DISPLAYED_AS%>"/>
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="col-sm-3">
                                    <label class="control-label pull-right">Access-level</label>
                                </div>
                                <div class="col-sm-9">
                                    <input type="radio" name="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>" 
                                        id="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>forinstructor<%=data.instructorList.size()+1%>"
                                        value="<%=Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER%>" 
                                        checked="checked">&nbsp;Co-owner: Can do everything
                                        <a href="javascript:;" 
                                            onclick="showInstructorRoleModal('<%=Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER%>')">
                                            View Details</a><br>
                                    <input type="radio" name="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>" 
                                        id="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>forinstructor<%=data.instructorList.size()+1%>"
                                        value="<%=Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER%>"
                                        >&nbsp;Manager: Can do everything except for deleting the course
                                        <a href="javascript:;" 
                                            onclick="showInstructorRoleModal('<%=Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER%>')">
                                            View Details</a><br>
                                    <input type="radio" name="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>" 
                                        id="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>forinstructor<%=data.instructorList.size()+1%>"
                                        value="<%=Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER%>"
                                        >&nbsp;Observer: Can only view information(students, submissions, comments etc.).
                                        &nbsp;Cannot edit/delete/submit anything.
                                        <a href="javascript:;" 
                                            onclick="showInstructorRoleModal(<'<%=Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER%>')">
                                            View Details</a><br>
                                    <input type="radio" name="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>" 
                                        id="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>forinstructor<%=data.instructorList.size()+1%>"
                                        value="<%=Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_TUTOR%>"
                                        >&nbsp;Tutor: Can view student details, give/view comments, submit/view responses for sessions
                                        <a href="javascript:;" 
                                            onclick="showInstructorRoleModal('<%=Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_TUTOR%>')">
                                            View Details</a><br>
                                    <input type="radio" name="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>" 
                                        id="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>forinstructor<%=data.instructorList.size()+1%>"
                                        value="<%=Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM%>"
                                        >&nbsp;Custom: No access by default. Any access needs to be granted explicitly.
                                </div>
                            </div>
                            <div id="tunePermissionsDivForInstructor<%=data.instructorList.size()+1%>" style="display: none;">
                                <div class="form-group">
                                    <div class="col-xs-12">
                                        <div class="panel panel-info">
                                            <div class="panel-heading">
                                                <strong>In general, this instructor can</strong>
                                            </div>
                                            <div class="panel-body">
                                                <div class="col-sm-3">
                                                    <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE%>"
                                                    value="true" checked="checked" /> Edit/Delete Course
                                                </div>
                                                <div class="col-sm-3">
                                                    <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR%>"
                                                    value="true" checked="checked" /> Add/Edit/Delete Instructors
                                                </div>
                                                <div class="col-sm-3">
                                                    <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION%>"
                                                    value="true" checked="checked" /> Create/Edit/Delete Sessions
                                                </div>
                                                <div class="col-sm-3">
                                                    <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT%>"
                                                    value="true" checked="checked" /> Enroll/Edit/Delete Students
                                                </div>
                                                <br><br>
                                                <div class="col-sm-6 border-right-gray">
                                                    <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS%>"
                                                    value="true" checked="checked" /> View Students' Details<br>
                                                    <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS%>"
                                                    value="true" checked="checked" /> Give Comments for Students<br>
                                                    <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS%>"
                                                    value="true" checked="checked" /> View Others' Comments on Students<br>
                                                    <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS%>"
                                                    value="true" checked="checked" /> Edit/Delete Others' Comments on Students<br>
                                                </div>
                                                <div class="col-sm-5 col-sm-offset-1">
                                                    <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS%>"
                                                    value="true" checked="checked" /> Sessions: Submit Responses and Add Comments<br>
                                                    <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS%>"
                                                    value="true" checked="checked" /> Sessions: View Responses and Comments<br>
                                                    <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS%>"
                                                    value="true" checked="checked" /> Sessions: Edit/Delete Responses/Comments by others<br>
                                                </div>
                                            </div>
                                        </div>
                                        <% if (!data.sectionNames.isEmpty()) { %>
                                            <% for (int j=0;j<data.sectionNames.size();j++) { %>
                                            <div id="tuneSectionPermissionsDiv<%=j%>ForInstructor<%=data.instructorList.size()+1%>" style="display: none;">
                                                <div class="panel panel-info">
                                                    <div class="panel-heading">
                                                        <div class="row">
                                                            <div class="col-sm-2">
                                                                <p><strong>But in section(s)</strong></p>
                                                            </div>
                                                            <div class="col-sm-9">
                                                                <% for (int sectionIdx = 0; sectionIdx<data.sectionNames.size(); sectionIdx++) { %>
                                                                    <% if (sectionIdx%3 == 0) { %>
                                                                    <div class="col-sm-12">
                                                                    <% } %>
                                                                    <div class="col-sm-4">
                                                                        <input type="checkbox" 
                                                                            name="<%=Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + j + Const.ParamsNames.INSTRUCTOR_SECTION + sectionIdx%>"
                                                                            value="<%=data.sectionNames.get(sectionIdx)%>"
                                                                            <% if (sectionIdx == j) { %>
                                                                            checked="checked"
                                                                            <% } %>
                                                                            >
                                                                        <%=data.sectionNames.get(sectionIdx)%>
                                                                        </div>
                                                                    <% if (sectionIdx%3 == 2) { %>
                                                                    </div>
                                                                    <% } %>
                                                                <% } %>
                                                                <% if (data.sectionNames.size()%3 != 0) { %>
                                                                </div>
                                                                <% } %>
                                                            </div>
                                                            <div class="col-sm-1">
                                                            <a href="javascript:;" onclick="hideTuneSectionPermissionsDiv(<%=data.instructorList.size()+1%>, <%=j%>)" class="pull-right">
                                                                <span class="glyphicon glyphicon-trash"></span></a>
                                                            </div>
                                                        </div>
                                                        <br>
                                                        <div class="row">
                                                            <div class="col-sm-12">
                                                                <p><strong> the instructor can only,</strong></p>
                                                            </div>
                                                        </div>
                                                        <input type="hidden" name="is<%=Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + j%>set" value="false"/>
                                                    </div>
                                                    <div class="panel-body">
                                                        <br>
                                                        <div class="col-sm-6 border-right-gray">
                                                            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS + 
                                                                Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + j%>" value="true"/> View Students' Details<br>
                                                            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS + 
                                                                Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + j%>" value="true"/> Give Comments for Students<br>
                                                            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS + 
                                                                Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + j%>" value="true"/> View Others' Comments on Students<br>
                                                            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS + 
                                                                Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + j%>" value="true"/> Edit/Delete Others' Comments on Students<br><br>
                                                        </div>
                                                        <div class="col-sm-5 col-sm-offset-1">
                                                            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS + 
                                                            Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + j%>" value="true"/> Sessions: Submit Responses and Add Comments<br>
                                                            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS + 
                                                            Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + j%>" value="true"/> Sessions: View Responses and Comments<br>
                                                            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS + 
                                                            Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + j%>" value="true"/> Sessions: Edit/Delete Responses/Comments by Others<br><br>
                                                        </div>
                                                        <a href="javascript:;" onclick="showTuneSessionnPermissionsDiv(<%=data.instructorList.size()+1%>, <%=j%>)"
                                                            id="toggleSessionLevelInSection<%=j%>ForInstructor<%=data.instructorList.size()+1%>"
                                                            class="small col-sm-5">Give different permissions for sessions in this section</a>      
                                                        <div id="tuneSessionPermissionsDiv<%=j%>ForInstructor<%=data.instructorList.size()+1%>" class="row" style="display: none;">
                                                            <input type="hidden" name="is<%=Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + j%>sessionsset" value="false"/>
                                                            <table class="table table-striped">
                                                                <thead>
                                                                    <tr>
                                                                        <td>SessionName</td>
                                                                        <td>Submit Responses and Add Comments</td>
                                                                        <td>View Responses and Comments</td>
                                                                        <td>Edit/Delete Responses/Comments by Others</td>
                                                                    </tr>
                                                                </thead>
                                                                <tbody>
                                                                    <% if (data.evalNames.isEmpty() && data.feedbackNames.isEmpty()) {%>
                                                                            <tr>
                                                                                <td colspan="4" class="text-center text-bold">No sessions in this course for you to configure</td>
                                                                            </tr>
                                                                    <% } %>
                                                                    <% for (String evalName : data.evalNames) { %>
                                                                    <tr>
                                                                        <td><%=evalName%></td>
                                                                        <td class="align-center">
                                                                            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS 
                                                                                + Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + j + "feedback" + Const.EVAL_PREFIX_FOR_INSTRUCTOR_PRIVILEGES + evalName%>"
                                                                                value="true"/>
                                                                        </td>
                                                                        <td class="align-center">
                                                                            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS
                                                                                + Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + j + "feedback" + Const.EVAL_PREFIX_FOR_INSTRUCTOR_PRIVILEGES + evalName%>"
                                                                                value="true"/>
                                                                        </td>
                                                                        <td class="align-center">
                                                                            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS
                                                                                + Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + j + "feedback" + Const.EVAL_PREFIX_FOR_INSTRUCTOR_PRIVILEGES + evalName%>"
                                                                                value="true"/>
                                                                        </td>
                                                                    </tr>
                                                                    <% } %>
                                                                    <% for (String feedbackName : data.feedbackNames) { %>
                                                                    <tr>
                                                                        <td><%=feedbackName%></td>
                                                                        <td class="align-center">
                                                                            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS 
                                                                                + Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + j + "feedback" + feedbackName%>"
                                                                                value="true"/>
                                                                        </td>
                                                                        <td class="align-center">
                                                                            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS
                                                                                + Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + j + "feedback" + feedbackName%>"
                                                                                value="true"/>
                                                                        </td>
                                                                        <td class="align-center">
                                                                            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS
                                                                                + Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + j + "feedback" + feedbackName%>"
                                                                                value="true"/>
                                                                        </td>
                                                                    </tr>
                                                                    <% } %>
                                                                </tbody>
                                                            </table>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <% } %>
                                        <a href="javascript:;" onclick="showTuneSectionPermissionsDiv(<%=data.instructorList.size()+1%>, 0)" class="small" 
                                            id="addSectionLevelForInstructor<%=data.instructorList.size()+1%>">Give different permissions for a specific section</a>
                                        <% } %>
                                    </div>
                                </div>
                             </div>
                        </div>
                        <div class="form-group">
                            <div class="align-center">
                                <input id="btnAddInstructor" type="submit" class="btn btn-primary"
                                value="Add Instructor" tabindex="10">
                            </div>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <div class="modal fade" id="tunePermissionsDivForInstructorAll" role="dialog" aria-labelledby="instructorRoleModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal">
                            <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
                        </button>
                        <h4 class="model-title" id="instructorRoleModalLabel">Permissions for Co-owner</h4>
                    </div>
                    <div class="modal-body">
                        <div class="row">
                        <div class="col-sm-6">
                            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE%>"
                            value="true" checked="checked" disabled="disabled" /> Edit/Delete Course
                        </div>
                        <div class="col-sm-6">
                            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR%>"
                            value="true" checked="checked" disabled="disabled" /> Add/Edit/Delete Instructors
                        </div>
                        <div class="col-sm-6">
                            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION%>"
                            value="true" checked="checked" disabled="disabled" /> Create/Edit/Delete Sessions
                        </div>
                        <div class="col-sm-6">
                            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT%>"
                            value="true" checked="checked" disabled="disabled" /> Enroll/Edit/Delete Students
                        </div>
                        <div class="col-sm-6">
                            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS%>"
                            value="true" checked="checked" disabled="disabled" /> View Students' Details<br>
                            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS%>"
                            value="true" checked="checked" disabled="disabled" /> Give Comments for Students<br>
                            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS%>"
                            value="true" checked="checked" disabled="disabled" /> View Others' Comments on Students<br>
                            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS%>"
                            value="true" checked="checked" disabled="disabled" /> Edit/Delete Others' Comments on Students<br>
                        </div>
                        <div class="col-sm-6">
                            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS%>"
                            value="true" checked="checked" disabled="disabled" /> Sessions: Submit Responses and Add Comments<br>
                            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS%>"
                            value="true" checked="checked" disabled="disabled" /> Sessions: View Responses and Comments<br>
                            <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS%>"
                            value="true" checked="checked" disabled="disabled" /> Sessions: Edit/Delete Responses/Comments by others<br>
                        </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <br><br>
    </div>

    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html>