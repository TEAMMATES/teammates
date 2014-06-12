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
    <jsp:include page="../enableJS.jsp"></jsp:include>   

    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]--> 
</head>

<body onload="readyCourseEditPage();"><jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_HEADER%>" />

    <div class="container theme-showcase" id="frameBodyWrapper">
        <div id="topOfPage"></div>
        <div id="headerOperation">
            <h1>Edit Course Details</h1>
        </div>
            
        <div class="panel panel-primary panel-narrow">
            <div class="panel-heading">
                <strong>Course:</strong>
                <a href="<%=data.getInstructorCourseDeleteLink(data.course.id, false)%>"
                    class="btn btn-primary btn-xs pull-right" id="courseDeleteLink"
                    data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.COURSE_DELETE%>"
                    onclick="return toggleDeleteCourseConfirmation('<%=data.course.id%>');"
                    <% if (!data.instructorPermission.privileges.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE)) {%>
                        style="display: none;"
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
        <br>
        
        <%
            for (int i = 0; i < data.instructorList.size(); i++) {
                InstructorAttributes instructor = data.instructorList.get(i);
                int index = i+1;
        %>
        <div class="panel panel-primary panel-narrow">
            <div class="panel-heading">
                <strong>Instructor <%=index%>:</strong>
                <div class="pull-right">
                    <% if (instructor.googleId == null) { %>
                        <a href="<%=data.getInstructorCourseInstructorRemindLink(instructor.courseId, instructor.email)%>" id="instrRemindLink<%=index%>"
                            class="btn btn-primary btn-xs"
                            data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.COURSE_INSTRUCTOR_REMIND%>"
                            onclick="return toggleSendRegistrationKey('<%=instructor.courseId%>','<%=instructor.email%>);">
                            <span class="glyphicon glyphicon-envelope"></span> Resend Invite</a>&nbsp;
                    <% } else { %>
                        <a href="javascript:;" id="instrEditLink<%=index%>" class="btn btn-primary btn-xs"
                            data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.COURSE_INSTRUCTOR_EDIT%>"
                            onclick="enableEditInstructor(<%=index%>, <%=data.instructorList.size()%>)">
                            <span class="glyphicon glyphicon-pencil"></span> Edit</a>&nbsp;
                    <% } %>
                    <a href="<%=data.getInstructorCourseInstructorDeleteLink(instructor.courseId, instructor.email)%>" id="instrDeleteLink<%=index%>"
                        class="btn btn-primary btn-xs"
                        data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.COURSE_INSTRUCTOR_DELETE%>"
                        onclick="return toggleDeleteInstructorConfirmation('<%=instructor.courseId%>','<%=instructor.email%>', <%=instructor.email.equals(data.account.email)%>);">
                        <span class="glyphicon glyphicon-trash"></span> Delete</a>
                </div>
            </div>

            <div class="panel-body">
                <form method="post" action="<%=Const.ActionURIs.INSTRUCTOR_COURSE_INSTRUCTOR_EDIT_SAVE%>"
                    id="formEditInstructor<%=index%>>" name="formEditInstructors" class="form form-horizontal" >
                    <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="<%=instructor.courseId%>">
                    <input type="hidden" name="<%=Const.ParamsNames.INSTRUCTOR_ID%>" value="<%=instructor.googleId%>">
                    <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
                    
                    <div id="instructorTable<%=index%>">
                        <% if (instructor.googleId != null) { %>
                            <div class="form-group">
                                <label class="col-sm-3 control-label">Google ID:</label>
                                <div class="col-sm-9"><input class="form-control" type="text"
                                    name="<%=Const.ParamsNames.INSTRUCTOR_ID%>" id="<%=Const.ParamsNames.INSTRUCTOR_ID+index%>"
                                    value="<%=instructor.googleId%>"
                                    data-toggle="tooltip" data-placement="top" title="Enter the google id of the instructor."
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
                                disabled="disabled">
                            </div>
                        </div>
                        <div id="accessControlEditDivForInstr<%=index%>" style="display: none;">
                            <div class="form-group">
                                <div class="col-sm-3">
                                    <label class="control-label pull-right">Access-level</label>
                                </div>
                                <div class="col-sm-9">
                                    <input type="radio" name="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>" id="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>forinstructor<%=index%>"
                                     value="<%=Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER%>">&nbsp;Co-owner: can do everything<br>
                                    <input type="radio" name="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>" id="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>forinstructor<%=index%>"
                                     value="<%=Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER%>">&nbsp;Manager: same as Co-owner except for deleting the course<br>
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-offset-3 col-sm-9"><input id="btnSaveInstructor<%=index%>" type="submit" class="btn btn-primary"
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
                onclick="showNewInstructorForm()">
        </div>
        
        <div class="panel panel-primary panel-narrow" id="panelAddInstructor">
            <div class="panel-heading">
                <strong>Instructors <%=data.instructorList.size()+1%>:</strong>
            </div>

            <div class="panel-body fill-plain">
                <form method="post" action="<%=Const.ActionURIs.INSTRUCTOR_COURSE_INSTRUCTOR_ADD%>" name="formAddInstructor" class="form form-horizontal" 
                    id="formAddInstructor">
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
                                <div class="col-sm-3">
                                    <label class="control-label pull-right">Access-level</label>
                                </div>
                                <div class="col-sm-9">
                                    <input type="radio" name="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>" id="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>forinstructor<%=data.instructorList.size()+1%>"
                                     value="<%=Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER%>">&nbsp;Co-owner: can do everything<br>
                                    <input type="radio" name="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>" id="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>forinstructor<%=data.instructorList.size()+1%>"
                                     value="<%=Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER%>">&nbsp;Manager: same as Co-owner except for deleting the course<br>
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-offset-3 col-sm-9"><input id="btnAddInstructor" type="submit" class="btn btn-primary"
                                value="Add Instructor" tabindex="10">
                            </div>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <br><br>
    </div>

    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html>