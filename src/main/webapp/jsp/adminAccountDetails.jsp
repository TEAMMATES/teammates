<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.datatransfer.CourseAttributes"%>
<%@ page import="teammates.common.datatransfer.CourseDetailsBundle"%>
<%@ page import="teammates.ui.controller.AdminAccountDetailsPageData"%>

<%
    AdminAccountDetailsPageData data = (AdminAccountDetailsPageData) request
            .getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
<link rel="shortcut icon" href="/favicon.png">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>TEAMMATES - Administrator Account Details</title>
<link href="/bootstrap/css/bootstrap.min.css" rel="stylesheet">
<link href="/bootstrap/css/bootstrap-theme.min.css" rel="stylesheet">
<link href="/stylesheets/teammatesCommon.css" rel="stylesheet">

<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
              <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
              <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
              <![endif]-->

<link rel="stylesheet" href="/stylesheets/adminAccountDetails.css"
    type="text/css">
<script type="text/javascript" src="/js/googleAnalytics.js"></script>
<script type="text/javascript" src="/js/jquery-minified.js"></script>
<script type="text/javascript" src="/js/common.js"></script>
<script type="text/javascript" src="/js/administrator.js"></script>
<script type="text/javascript"
    src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
<script type="text/javascript" src="/bootstrap/js/bootstrap.min.js"></script>
<jsp:include page="../enableJS.jsp"></jsp:include>
</head>

<body>

    <jsp:include page="<%=Const.ViewURIs.ADMIN_HEADER%>" />
    <div class="container theme-showcase" role="main">
        <div id="frameBody">
            <div id="frameBodyWrapper">
                <div id="topOfPage"></div>
                <div id="headerOperation" class="page-header">
                    <h1>Instructor Account Details</h1>
                </div>

                <div class="well well-plain">
                    <form class="form-horizontal" role="form">
                        <div class="panel-heading">
                            <div class="form-group">
                                <label class="col-sm-2 control-label">Google
                                    ID:</label>
                                <div class="col-sm-10">
                                    <p class="form-control-static"><%=data.accountInformation.googleId%></p>
                                </div>

                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">Name:</label>
                                <div class="col-sm-10">
                                    <p class="form-control-static"><%=data.accountInformation.name%></p>
                                </div>

                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">Email:</label>
                                <div class="col-sm-10">
                                    <p class="form-control-static"><%=data.accountInformation.email%></p>
                                </div>

                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">Institute:</label>
                                <div class="col-sm-10">
                                    <p class="form-control-static"><%=data.accountInformation.institute%></p>
                                </div>

                            </div>

                        </div>
                    </form>
                </div>

                <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />

                <div class="page-header">
                    <h2>
                        Instructor For <small
                            class="courseCount rightalign bold">
                            <%=data.instructorCourseList != null ? data.instructorCourseList
                    .size() : 0%> Courses
                        </small>
                    </h2>
                </div>

                <%
                    if (data.instructorCourseList != null
                            && data.instructorCourseList.size() != 0) {
                %>
                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <br>
                    </div>

                    <table class="table table-striped dataTable">
                        <thead>
                            <tr>
                                <th width="70%">Course</th>
                                <th>Options</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                for (CourseDetailsBundle courseDetails : data.instructorCourseList) {
                                        out.print("<tr>");
                                        out.print("<td>[" + courseDetails.course.id + "] "
                                                + courseDetails.course.name + "</td>");
                                        out.print("<td><a id=\"instructor_"
                                                + courseDetails.course.id
                                                + "\" class=\"btn btn-danger btn-sm \" href=\""
                                                + data.getAdminDeleteInstructorFromCourseLink(
                                                        data.accountInformation.googleId,
                                                        courseDetails.course.id)
                                                + "\"><span class=\"glyphicon glyphicon-trash\"></span>"
                                                + "Remove From Course</a></td>");
                                        out.print("</tr>");
                                    }
                            %>
                        </tbody>
                    </table>
                </div>

                <%
                    } else {
                %>

                <div class="alert alert-warning">
                    <span class="glyphicon glyphicon-exclamation-sign"></span>
                    No Courses found for this Account
                </div>

                <%
                    /* out.print("<tr><td colspan=\"2\" class=\"bold\">No Courses found for this Account.</td></tr>"); */
                    }
                %>



                <div class="page-header">
                    <h2>
                        Student For <small
                            class="courseCount rightalign bold">
                            <%=data.studentCourseList != null ? data.studentCourseList
                    .size() : 0%> Courses
                        </small>
                    </h2>
                </div>

                <%
                    if (data.studentCourseList != null) {
                %>
                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <br>
                    </div>

                    <table class="table table-striped dataTable">

                        <thead>
                            <tr>
                                <th width="70%">Course</th>
                                <th>Options</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                for (CourseAttributes course : data.studentCourseList) {
                                        out.print("<tr>");
                                        out.print("<td>[" + course.id + "] " + course.name
                                                + "</td>");
                                        out.print("<td><a id=\"student_"
                                                + course.id
                                                + "\" class=\"btn btn-danger btn-sm \" href=\""
                                                + data.getAdminDeleteStudentFromCourseLink(
                                                        data.accountInformation.googleId, course.id)
                                                + "\"><span class=\"glyphicon glyphicon-trash\"></span>"
                                                + "Remove From Course</a></td>");
                                        out.print("</tr>");
                                    }
                            %>
                        </tbody>
                    </table>
                </div>
                <%
                    } else {
                %>

                <div class="alert alert-warning">
                    <span class="glyphicon glyphicon-exclamation-sign"></span>
                    This Account is not a Student
                </div>

                <%
                    }
                %>
                <br> <br> <br>
            </div>
        </div>
    </div>


    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html>