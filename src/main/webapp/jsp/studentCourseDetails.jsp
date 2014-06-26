<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="teammates.common.util.Const" %>
<%@ page import="teammates.common.datatransfer.StudentAttributes" %>
<%@ page import="teammates.common.datatransfer.InstructorAttributes" %>
<%@ page import="teammates.ui.controller.PageData"%>
<%@ page import="teammates.ui.controller.StudentCourseDetailsPageData"%>
<%
    StudentCourseDetailsPageData data = (StudentCourseDetailsPageData)request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
    <link rel="shortcut icon" href="/favicon.png">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>TEAMMATES - Student</title>
    <link rel="stylesheet" href="/bootstrap/css/bootstrap.min.css" type="text/css"/>
    <link rel="stylesheet" href="/bootstrap/css/bootstrap-theme.min.css" type="text/css"/>
    <link rel="stylesheet" href="/stylesheets/teammatesCommon.css" type="text/css"/>
   
    <script type="text/javascript" src="/js/googleAnalytics.js"></script>
    <script type="text/javascript" src="/js/jquery-minified.js"></script>
    <script type="text/javascript" src="/js/common.js"></script>
    <script type="text/javascript" src="/bootstrap/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="/js/student.js"></script>
    <jsp:include page="../enableJS.jsp"></jsp:include>   

    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]--> 
</head>

<body>

    <!-- Common Header -->
    <jsp:include page="<%=Const.ViewURIs.STUDENT_HEADER%>" />

    <!-- Main Body -->
    
    <div class = "container" id="frameBodyWrapper">   
        <div id="topOfPage"></div>
        
        <div id="headerOperation">
            <h1>Team Details for <%=data.courseDetails.course.id%></h1>
        </div>
        <br>
        
        <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
        
        <br>
        <div class="well well-plain">
            <div class="form-horizontal">
                <!-- Course ID -->
                <div class="form-group">
                    <label class="col-sm-3 control-label">Course ID:</label>
                    <div class="col-sm-9">
                        <p class="form-control-static" id="<%=Const.ParamsNames.COURSE_ID%>">
                            <%=data.courseDetails.course.id%>
                        </p>
                    </div>
                </div> 
                <!-- Course Name -->
                <div class="form-group">
                    <label class="col-sm-3 control-label">Course Name:</label>
                    <div class="col-sm-9">
                        <p class="form-control-static" id="<%=Const.ParamsNames.COURSE_NAME%>">
                            <%=PageData.sanitizeForHtml(data.courseDetails.course.name)%>
                        </p>
                    </div>
                </div>  
                <!-- Instructors Names -->
                <div class="form-group">
                    <label class="col-sm-3 control-label">Instructors:</label>
                    <div class="col-sm-9">
                        <p class="form-control-static" id="<%=Const.ParamsNames.INSTRUCTOR_NAME%>">
                           <%
                            for (int i = 0; i < data.instructors.size(); i++) {
                                InstructorAttributes instructor = data.instructors.get(i);
                                if (instructor.isDisplayedToStudents) {
                                    String displayedName = instructor.displayedName + ": ";
                                    String instructorInfo = instructor.name + " (" + instructor.email + ")";
                            %>
                                <%=displayedName%><a href = "mailto:<%=instructor.email%>"><%=instructorInfo%></a><br>
                            <%
                                }
                            }
                            %>
                        </p>
                    </div>
                </div>
                <!-- Team Name -->
                <div class="form-group">
                    <label class="col-sm-3 control-label">Your team:</label>
                    <div class="col-sm-9">
                        <p class="form-control-static" id="<%=Const.ParamsNames.TEAM_NAME%>">
                            <%=PageData.sanitizeForHtml(data.student.team)%>
                        </p>
                    </div>
                </div>
                <!-- Student Name -->
                <div class="form-group">
                    <label class="col-sm-3 control-label">Your name:</label>
                    <div class="col-sm-9">
                        <p class="form-control-static" id="<%=Const.ParamsNames.STUDENT_NAME%>">
                            <%=PageData.sanitizeForHtml(data.student.name)%>
                        </p>
                    </div>
                </div>
                <!-- Student Email -->
                <div class="form-group">
                    <label class="col-sm-3 control-label">Your e-mail:</label>
                    <div class="col-sm-9">
                        <p class="form-control-static" id="<%=Const.ParamsNames.STUDENT_EMAIL%>">
                            <%=data.student.email%>
                        </p>
                    </div>
                </div>
                 <!-- Student Teammates -->
                <div class="form-group">
                    <label class="col-sm-3 control-label">Your teammates:</label>
                    <div class="col-sm-9">
                        <p class="form-control-static" id="<%=Const.ParamsNames.TEAMMATES%>">
                            <%
                                if(data.team==null || data.team.students.size()==1){
                            %>
                            <span style="font-style: italic;">
                                You have no team members or you are not registered in any team
                            </span>
                            <%
                                } else {
                            %>
                            
                            <%
                                for(StudentAttributes student: data.team.students){
                            %>
                            <%
                                if(!student.email.equals(data.student.email)) {
                            %>
                                <a href = "mailto:<%=student.email%>">
                                    <%=PageData.sanitizeForHtml(student.name)%>
                                </a><br>
                            <%
                                }
                            %>
                            <%
                                }
                            %>
                            <%
                                }
                            %>
                        </p>
                    </div>
                </div> 
            </div>  
        </div>
        <br>
        <br>
        <br>        
    </div>

    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />

</body>
</html>