<%@ tag description="instructorCourse - Course table" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="archivedCourses" type="teammates.ui.template.ArchivedCoursesTable" required="true" %>
<%@ attribute name="activeCourses" type="teammates.ui.template.ActiveCoursesTable" required="true" %>

<h2 class="text-muted">Archived courses</h2>
<table class="table table-bordered table-striped">
    <thead>
        <tr class="fill-default">
            <th onclick="toggleSort(this,1);" id="button_sortid" class="button-sort-none">
                Course ID<span class="icon-sort unsorted"></span>
            </th>
            <th onclick="toggleSort(this,2);" id="button_sortid" class="button-sort-none">
                Course Name<span class="icon-sort unsorted"></span>
            </th>
            <th class="align-center no-print">Action(s)</th>
        </tr>
    </thead>
    <c:forEach items="${archivedCourses.rows}" var="archivedCourse" varStatus="i">
        <tr>
            <td id="courseid${i.index + fn:length(activeCourses.rows)}">${archivedCourse.courseId}</td>
            <td id="coursename${i.index + fn:length(activeCourses.rows)}">${archivedCourse.courseName}</td>
            <td class="align-center no-print">
                <c:forEach items="${archivedCourse.actions}" var="button">
                    <a ${button.attributesToString}>
                        ${button.content}
                    </a>
                </c:forEach>
            </td>
        </tr>
    </c:forEach>
</table>