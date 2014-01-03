<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="teammates.common.util.Const" %>
<%@ page import="teammates.common.datatransfer.CourseAttributes"%>
<%@ page import="teammates.common.datatransfer.CourseDetailsBundle"%>
<%@ page import="teammates.ui.controller.AdminAccountDetailsPageData"%>

<%
	AdminAccountDetailsPageData data = (AdminAccountDetailsPageData)request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
    <link rel="shortcut icon" href="/favicon.png">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>TEAMMATES - Administrator Account Details</title>
    <link rel="stylesheet" href="/stylesheets/adminAccountDetails.css" type="text/css">
    <link rel="stylesheet" href="/stylesheets/common.css" type="text/css">

    <script type="text/javascript" src="/js/googleAnalytics.js"></script>
    <script type="text/javascript" src="/js/jquery-minified.js"></script>
    <script type="text/javascript" src="/js/tooltip.js"></script>
    <script type="text/javascript" src="/js/common.js"></script>
    <script type="text/javascript" src="/js/administrator.js"></script>
    <jsp:include page="../enableJS.jsp"></jsp:include>
</head>

<body>
    <div id="dhtmltooltip"></div>
    <div id="frameTop">
    <jsp:include page="<%=Const.ViewURIs.ADMIN_HEADER%>" />
    </div>
    <div id="frameBody">
        <div id="frameBodyWrapper">
            <div id="topOfPage"></div>
            <div id="headerOperation">
            <h1>Instructor Account Details</h1>
            </div>
            <table class="inputTable">
                <tr>
                    <td class="label leftalign bold" width="30%">Google ID: </td>
                    <td><%=data.accountInformation.googleId%></td>
                </tr>
                <tr>
                    <td class="label leftalign bold" width="30%">Name: </td>
                    <td><%=data.accountInformation.name%></td>
                </tr>
                <tr>
                    <td class="label leftalign bold" width="30%">Email: </td>
                    <td><%=data.accountInformation.email%></td>
                </tr>
                <tr>
                    <td class="label leftalign bold" width="30%">Institute: </td>
                    <td><%=data.accountInformation.institute%></td>
                </tr>
            </table>
            <br>
             <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
            <br>
            <br>
            <h2>Instructor For:</h2>
            <p class="courseCount rightalign bold">Total Courses: <%=data.instructorCourseList != null ? data.instructorCourseList.size() : 0%></p>
            <table class="dataTable">
                <tr>
                    <th class="bold" width="70%">Course</th>
                    <th class="bold">Options</th>
                </tr>
                <%
                	if(data.instructorCourseList != null && data.instructorCourseList.size() != 0){
                                                                   	                    for(CourseDetailsBundle courseDetails : data.instructorCourseList){
                                                                   	                        out.print("<tr>");
                                                                   	                        out.print("<td>[]" + courseDetails.course.id + "] " + courseDetails.course.name + "</td>");
                                                                   	                        out.print("<td><a id=\"instructor_" + courseDetails.course.id + "\" href=\"" + data.getAdminDeleteInstructorFromCourseLink(data.accountInformation.googleId, courseDetails.course.id)+ "\">Remove From Course</a></td>");
                                                                   	                        out.print("</tr>");
                                                                   	                    }
                                                                                    } else {
                                                                                        out.print("<tr><td colspan=\"2\" class=\"bold\">No Courses found for this Account.</td></tr>");
                                                                                    }
                %>
            </table>
            <br>
            <br>
            <h2>Student For:</h2>
            <p class="courseCount rightalign bold">Total Courses: <%=data.studentCourseList != null ? data.studentCourseList.size() : 0%></p>
            <table class="dataTable">
                <tr>
                    <th class="bold" width="70%">Course</th>
                    <th class="bold">Options</th>
                </tr>
                <%
                	if(data.studentCourseList != null){
                                                                                        for(CourseAttributes course : data.studentCourseList){
                                                                                            out.print("<tr>");
                                                                                            out.print("<td>[" + course.id + "] " + course.name + "</td>");
                                                                                            out.print("<td><a id=\"student_" + course.id + "\" href=\"" + data.getAdminDeleteStudentFromCourseLink(data.accountInformation.googleId, course.id)+ "\">Remove From Course</a></td>");
                                                                                            out.print("</tr>");
                                                                                        }
                                                                                    } else {
                                                                                        out.print("<tr><td colspan=\"2\" class=\"bold\">This Account is not a Student.</td></tr>");
                                                                                    }
                %>
            </table>
            <br>
            <br>
            <br>
        </div>
    </div>

    <div id="frameBottom">
        <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
    </div>
</body>
</html>