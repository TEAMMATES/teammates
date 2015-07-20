<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ tag description="Generic Admin Page" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/admin" prefix="ta" %>
<%@ taglib tagdir="/WEB-INF/tags/admin/accounts" prefix="accounts" %>

<c:set var="jsIncludes">
    <link rel="stylesheet" href="/stylesheets/adminAccountDetails.css" type="text/css">
    <script type="text/javascript" src="/js/administrator.js"></script>
</c:set>

<ta:adminPage pageTitle="TEAMMATES - Administrator Account Details" bodyTitle="Instructor Account Details"
              bodyOnload="" jsIncludes="${jsIncludes}">
    <accounts:instructorAccountDetailsPanel accountInformation="${data.accountInformation}"/>
    <t:statusMessage />

    <div class="page-header">
        <h2>
            Instructor For <small class="courseCount rightalign bold">${fn:length(data.instructorCourseList)} Courses</small>
        </h2>
    </div>

    <
</ta:adminPage>

                

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


    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html>