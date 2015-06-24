<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="teammates.common.util.Config" %>
<%@ page import="teammates.common.util.Const" %>
<%@ page import="teammates.common.datatransfer.CourseAttributes"%>
<%@ page import="static teammates.ui.controller.PageData.sanitizeForHtml"%>
<%@ page import="teammates.ui.controller.InstructorCourseStudentDetailsEditPageData"%>
<%
    InstructorCourseStudentDetailsEditPageData data = (InstructorCourseStudentDetailsEditPageData)request.getAttribute("data");
%>

<!DOCTYPE html>
<html>
<head>
    <title>TEAMMATES - Instructor</title>

    <link rel="shortcut icon" href="/favicon.png" />

    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link type="text/css" href="/bootstrap/css/bootstrap.min.css" rel="stylesheet"/>
    <link type="text/css" href="/bootstrap/css/bootstrap-theme.min.css" rel="stylesheet"/>
    <link type="text/css" href="/stylesheets/teammatesCommon.css" rel="stylesheet"/>

    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->

    <script type="text/javascript" src="/js/googleAnalytics.js"></script>
    <script type="text/javascript" src="/js/jquery-minified.js"></script>
    <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.4/jquery-ui.min.js"></script>
    <script type="text/javascript" src="/js/common.js"></script>
    <script type="text/javascript" src="/bootstrap/js/bootstrap.min.js"></script>

    <jsp:include page="../enableJS.jsp"></jsp:include>   

    <script type="text/javascript" src="/js/instructor.js"></script>
    <script type="text/javascript" src="/js/instructorCourses.js"></script>
</head>

<body>
    <jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_HEADER%>" />

    <div class="container" id="mainContent">
        <div id="topOfPage"></div>
        <h1>Edit Student Details</h1>
        <br>
            
        <div class="panel panel-primary">
            <div class="panel-body fill-plain">
                <form action="<%=Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT_SAVE%>" method="post" class="form form-horizontal">
                    <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="<%=data.getStudentInfoTable().getCourse()%>">
                    
                    <div class="form-group">
                         <label class="col-sm-1 control-label">Student Name:</label>
                         <div class="col-sm-11">
                             <input class="form-control" name="<%=Const.ParamsNames.STUDENT_NAME%>" 
                                     id="<%=Const.ParamsNames.STUDENT_NAME%>"
                                     value="<%=sanitizeForHtml(data.getStudentInfoTable().getName())%>">
                         </div>
                     </div>
                     <% if(data.hasSection) { %>
                    <div class="form-group">
                         <label class="col-sm-1 control-label">Section Name:</label>
                         <div class="col-sm-11">
                             <input class="form-control" name="<%=Const.ParamsNames.SECTION_NAME%>" 
                                     id="<%=Const.ParamsNames.SECTION_NAME%>"
                                     value="<%=sanitizeForHtml(data.getStudentInfoTable().getSection())%>">
                         </div>
                     </div>
                     <% } %>
                     <div class="form-group">
                         <label class="col-sm-1 control-label">Team Name:</label>
                         <div class="col-sm-11">
                             <input class="form-control" name="<%=Const.ParamsNames.TEAM_NAME%>" 
                                     id="<%=Const.ParamsNames.TEAM_NAME%>"
                                     value="<%=sanitizeForHtml(data.getStudentInfoTable().getTeam())%>">
                         </div>
                     </div>
                     <div class="form-group">
                         <label class="col-sm-1 control-label">E-mail Address:
                             <input type="hidden" name="<%=Const.ParamsNames.STUDENT_EMAIL%>" 
                                     id="<%=Const.ParamsNames.STUDENT_EMAIL%>"
                                     value="<%=sanitizeForHtml(data.getStudentInfoTable().getEmail())%>">
                         </label>
                         <div class="col-sm-11">
                             <input class="form-control" name="<%=Const.ParamsNames.NEW_STUDENT_EMAIL%>" 
                                     id="<%=Const.ParamsNames.NEW_STUDENT_EMAIL%>"
                                     value="<%=sanitizeForHtml(data.newEmail)%>">
                         </div>
                     </div>
                     <div class="form-group">
                         <label class="col-sm-1 control-label">Comments:</label>
                         <div class="col-sm-11">
                             <textarea class="form-control" rows="6" name="<%=Const.ParamsNames.COMMENTS%>" 
                                 id="<%=Const.ParamsNames.COMMENTS%>"><%=sanitizeForHtml(data.getStudentInfoTable().getComments())%></textarea>
                         </div>
                     </div>
                    
                    <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
                    <br>
                    <div class="align-center">
                        <input type="submit" class="btn btn-primary" id="button_submit" name="submit" value="Save Changes">
                    </div>
                    <br>
                    <br>
                    <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
                </form>
            </div>
        </div>
        <br><br>
    </div>

    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html>