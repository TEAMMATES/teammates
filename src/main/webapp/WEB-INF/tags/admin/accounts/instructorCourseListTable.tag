<%@ tag description="Course List Table for an instructor in Account Details Page" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="instructorCourseList" ţype="java.util.Collection" required="true" %>

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
            <c:forEach items="${instructorCourseList}" var="courseDetails">
                <tr>
                    <td>[${courseDetails.course.id}] ${courseDetails.course.name}</td>
                    <td></td>
                </tr>
            </c:forEach>
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