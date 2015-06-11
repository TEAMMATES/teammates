<%@ tag description="instructorCourse - Course table" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="title" type="String" required="true" %>

<h2>${title}</h2>
<table class="table table-bordered table-striped">
    <thead class="fill-primary">
        <tr>
            <th onclick="toggleSort(this,1);" id="button_sortcourseid" class="button-sort-none">
                Course ID<span class="icon-sort unsorted"></span>
            </th>
            <th onclick="toggleSort(this,2);" id="button_sortcoursename" class="button-sort-none">
                Course Name<span class="icon-sort unsorted"></span>
            </th>
            <th>
                Sections
            </th>
            <th>
                Teams
            </th>
            <th>
                Total Students
            </th>
            <th>
                Total Unregistered
            </th>
            <th class="align-center no-print">
                Action(s)
            </th>
        </tr>
    </thead>
    <c:forEach items="${data.activeCourses.rows}" var="activeCourse" varStatus="i">
        <tr>
            <td id="courseid${i.index}">${activeCourse.courseId}</td>
            <td id="coursename${i.index}">${activeCourse.courseName}</td>
            <td class="align-center">${activeCourse.sectionNum}</td>
            <td class="t_course_teams align-center">${activeCourse.teamNum}</td>
            <td class="align-center">${activeCourse.totalStudentNum}</td>
            <td class="align-center">${activeCourse.unregisteredStudentNum}</td>
            <td class="align-center no-print">${activeCourse.actions}</td>
        </tr>
    </c:forEach>
    
    <c:if test="${empty data.activeCourses.rows}">
        <tr>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
        </tr>
    </c:if>
</table>

<br>
<br>
<c:if test="${empty data.activeCourses.rows}">
    No records found. <br>
    <br>
</c:if>
<br>
<br>