<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="teammates.common.util.Config" %>
<%@ page import="teammates.common.util.Const" %>
<%@ page import="teammates.common.datatransfer.CourseAttributes"%>
<%@ page import="teammates.common.datatransfer.EvaluationAttributes"%>
<%@ page import="static teammates.ui.controller.PageData.sanitizeForHtml"%>
<%@ page import="teammates.ui.controller.InstructorCourseStudentDetailsEditPageData"%>
<%
    InstructorCourseStudentDetailsEditPageData data = (InstructorCourseStudentDetailsEditPageData)request.getAttribute("data");
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
    <script type="text/javascript" src="/js/instructorCourses.js"></script>
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
            <h1>Edit Student Details</h1>
        </div>
            
        <div class="panel panel-primary">
            <div class="panel-body fill-plain">
                <form action="<%=Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT_SAVE%>" method="post" class="form form-horizontal">
                    <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="<%=data.student.course%>">
                    
                    <div class="form-group">
                         <label class="col-sm-1 control-label">Student Name:</label>
                         <div class="col-sm-11">
                             <input class="form-control" name="<%=Const.ParamsNames.STUDENT_NAME%>" 
                                     id="<%=Const.ParamsNames.STUDENT_NAME%>"
                                     value="<%=sanitizeForHtml(data.student.name)%>">
                         </div>
                     </div>
                     <% if(data.hasSection) { %>
                    <div class="form-group">
                         <label class="col-sm-1 control-label">Section Name:</label>
                         <div class="col-sm-11">
                             <input class="form-control" name="<%=Const.ParamsNames.SECTION_NAME%>" 
                                     id="<%=Const.ParamsNames.SECTION_NAME%>"
                                     value="<%=sanitizeForHtml(data.student.section)%>">
                         </div>
                     </div>
                     <% } %>
                     <div class="form-group">
                         <label class="col-sm-1 control-label">Team Name:</label>
                         <div class="col-sm-11">
                             <input class="form-control" name="<%=Const.ParamsNames.TEAM_NAME%>" 
                                     id="<%=Const.ParamsNames.TEAM_NAME%>"
                                     value="<%=sanitizeForHtml(data.student.team)%>">
                         </div>
                     </div>
                     <div class="form-group">
                         <label class="col-sm-1 control-label">E-mail Address:
                             <input type="hidden" name="<%=Const.ParamsNames.STUDENT_EMAIL%>" 
                                     id="<%=Const.ParamsNames.STUDENT_EMAIL%>"
                                     value="<%=sanitizeForHtml(data.student.email)%>">
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
                                 id="<%=Const.ParamsNames.COMMENTS%>"><%=sanitizeForHtml(data.student.comments)%></textarea>
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