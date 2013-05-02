<%@ page import="teammates.common.Common" %>
<%@ page import="teammates.ui.controller.AdminAccountDetailsHelper"%>
<%@ page import="teammates.common.datatransfer.CourseData"%>
<%@ page import="teammates.common.datatransfer.CourseDetailsBundle"%>

<%
	AdminAccountDetailsHelper helper = (AdminAccountDetailsHelper)request.getAttribute("helper");
%>
<!DOCTYPE html>
<html>
<head>
    <link rel="shortcut icon" href="/favicon.png">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Teammates - Administrator Account Details</title>
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
    <jsp:include page="<%=Common.JSP_ADMIN_HEADER%>" />
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
                    <td><%=helper.accountInformation.googleId%></td>
                </tr>
                <tr>
                    <td class="label leftalign bold" width="30%">Name: </td>
                    <td><%=helper.accountInformation.name%></td>
                </tr>
                <tr>
                    <td class="label leftalign bold" width="30%">Email: </td>
                    <td><%=helper.accountInformation.email%></td>
                </tr>
                <tr>
                    <td class="label leftalign bold" width="30%">Institute: </td>
                    <td><%=helper.accountInformation.institute%></td>
                </tr>
            </table>
            <br>
             <jsp:include page="<%=Common.JSP_STATUS_MESSAGE%>" />
            <br>
            <br>
            <h2>Instructor For:</h2>
            <p class="courseCount rightalign bold">Total Courses: <%=helper.instructorCourseList != null ? helper.instructorCourseList.size() : 0%></p>
            <table class="dataTable">
                <tr>
                    <th class="bold" width="70%">Course</th>
                    <th class="bold">Options</th>
                </tr>
                <%
                	if(helper.instructorCourseList != null && helper.instructorCourseList.size() != 0){
                                                	                    for(CourseDetailsBundle courseDetails : helper.instructorCourseList){
                                                	                        out.print("<tr>");
                                                	                        out.print("<td>[]" + courseDetails.course.id + "] " + courseDetails.course.name + "</td>");
                                                	                        out.print("<td><a id=\"instructor_" + courseDetails.course.id + "\" href=\"" + helper.getInstructorCourseDeleteLink(helper.accountInformation.googleId, courseDetails.course.id)+ "\">Remove From Course</a></td>");
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
            <p class="courseCount rightalign bold">Total Courses: <%=helper.studentCourseList != null ? helper.studentCourseList.size() : 0 %></p>
            <table class="dataTable">
                <tr>
                    <th class="bold" width="70%">Course</th>
                    <th class="bold">Options</th>
                </tr>
                <%
                    if(helper.studentCourseList != null){
                        for(CourseData course : helper.studentCourseList){
                            out.print("<tr>");
                            out.print("<td>[]" + course.id + "] " + course.name + "</td>");
                            out.print("<td><a id=\"student_" + course.id + "\" href=\"" + helper.getStudentCourseDeleteLink(helper.accountInformation.googleId, course.id)+ "\">Remove From Course</a></td>");
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
        <jsp:include page="<%= Common.JSP_FOOTER %>" />
    </div>
</body>
</html>