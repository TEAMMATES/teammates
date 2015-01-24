<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="java.util.List" %>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.datatransfer.StudentAttributes"%>
<%@ page import="static teammates.ui.controller.PageData.sanitizeForHtml"%>
<%@ page import="teammates.ui.controller.InstructorCourseEnrollResultPageData"%>
<%
    InstructorCourseEnrollResultPageData data = (InstructorCourseEnrollResultPageData)request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
    <link rel="shortcut icon" href="/favicon.png">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>TEAMMATES - Instructor</title>
    <link rel="stylesheet" href="/bootstrap/css/bootstrap.min.css" type="text/css" >
    <link rel="stylesheet" href="/bootstrap/css/bootstrap-theme.min.css" type="text/css" >
    <link rel="stylesheet" href="/stylesheets/teammatesCommon.css" type="text/css" >
    
    <script type="text/javascript" src="/js/googleAnalytics.js"></script>
    <script type="text/javascript" src="/js/jquery-minified.js"></script>
    <script type="text/javascript"  src="/bootstrap/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="/js/date.js"></script>
    
        <script type="text/javascript" src="/js/common.js"></script>
    
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

    <div class="container" id="frameBodyWrapper">
        <div id="headerOperation">
            <h1>Enrollment Results for <%=sanitizeForHtml(data.courseId)%></h1>
        </div>

        <div class="alert alert-success">
        <form name='goBack' action="<%=data.getInstructorCourseEnrollLink(data.courseId)%>" method="post" role="form"> 
            Enrollment Successful. Summary given below. Click <a id="edit_enroll" href="javascript:document.forms['goBack'].submit()">here</a> to do further changes to the student list.
            
        <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="<%=data.courseId%>">
        <input type="hidden" name="<%=Const.ParamsNames.STUDENTS_ENROLLMENT_INFO%>" value="<%=data.enrollStudents%>">
        </form>
        </div>
        
        <%
            for(int i=0; i < 6; i++){
                List<StudentAttributes> students = data.students[i];
        %>
            <%
                if(students.size()>0){
            %>  
                <% if(i == 0){ %>
                <div class="panel panel-danger">
                <% } else if(i == 1){ %>
                <div class="panel panel-primary">
                <% } else if(i == 2){ %>
                <div class="panel panel-warning">
                <% } else if(i == 3){ %>
                <div class="panel panel-info">
                <% } else if(i == 4){ %>
                <div class="panel panel-default">
                <% } else{ %>
                <div class="panel panel-danger">
                <% } %>
                <div class="panel-heading">
                <%=data.getMessageForEnrollmentStatus(i)%>
                </div>
                <table class="table table-striped table-bordered">
                <tr> 
                    <% if(data.hasSection){ %>
                        <th>Section</th>
                    <% } %>
                    <th>Team</th>
                    <th>Student Name</th>
                    <th>E-mail address</th>
                    <th>Comments</th>
                </tr>
                <%
                    for(StudentAttributes student: students){
                %>
                    <tr>
                        <% if(data.hasSection) { %>
                            <td><%=sanitizeForHtml(student.section)%></td>
                        <% } %>
                        <td><%=sanitizeForHtml(student.team)%></td>
                        <td><%=sanitizeForHtml(student.name)%></td>
                        <td><%=sanitizeForHtml(student.email)%></td>
                        <td><%=sanitizeForHtml(student.comments)%></td>
                    </tr>
                <%
                    }
                %>
                </table>
                </div>
                <br>
                <br>
            <%
                }
            %>
        <%
            }
        %>
        
        <div id="instructorCourseEnrollmentButtons">
        </div>
    </div>

    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />

</body>
</html>