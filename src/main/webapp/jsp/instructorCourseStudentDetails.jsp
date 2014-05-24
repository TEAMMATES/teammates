<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="teammates.common.util.Config" %>
<%@ page import="teammates.common.util.Const" %>
<%@ page import="teammates.common.datatransfer.CourseAttributes"%>
<%@ page import="teammates.common.datatransfer.EvaluationAttributes"%>
<%@ page import="static teammates.ui.controller.PageData.sanitizeForHtml"%>
<%@ page import="teammates.ui.controller.InstructorCourseStudentDetailsPageData"%>
<%
    InstructorCourseStudentDetailsPageData data = (InstructorCourseStudentDetailsPageData)request.getAttribute("data");
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
    <jsp:include page="../enableJS.jsp"></jsp:include>   

    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]--> 
</head>


<body>
    <jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_HEADER%>" />


    <div class="container theme-showcase">
        <div id="topOfPage"></div>
        <div id="headerOperation">
            <h1>Student Details</h1>
        </div>
        
        <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
        
        <div class="form form-horizontal" id="studentInfomationTable">
                <div class="form-group">
                    <label class="col-sm-1 control-label">Student Name:</label>
                    <div class="col-sm-11" id="<%=Const.ParamsNames.STUDENT_NAME%>">
                        <p class="form-control-static"><%=data.student.name%></p>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-1 control-label">Team Name:</label>
                    <div class="col-sm-11" id="<%=Const.ParamsNames.TEAM_NAME%>">
                        <p class="form-control-static"><%=sanitizeForHtml(data.student.team)%></p>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-1 control-label">E-mail Address:</label>
                    <div class="col-sm-11" id="<%=Const.ParamsNames.STUDENT_EMAIL%>">
                        <p class="form-control-static"><%=sanitizeForHtml(data.student.email)%></p>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-1 control-label">Join Link:</label>
                    <div class="col-sm-11" id="<%=Const.ParamsNames.REGKEY%>">
                        <small class="form-control-static"><%=sanitizeForHtml(Config.APP_URL 
                                + Const.ActionURIs.STUDENT_COURSE_JOIN 
                                + "?regkey=" + data.regKey)%></small>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-1 control-label">Comments:</label>
                    <div class="col-sm-11" id="<%=Const.ParamsNames.COMMENTS%>">
                        <p class="form-control-static"><%=sanitizeForHtml(data.student.comments)%></p>
                    </div>
                </div>
        </div>
        <br>
        <br>
    </div>


    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html>