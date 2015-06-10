<%@ tag description="instructorCourse - Course table" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="title" type="String" required="true" %>

<c:if test="${not empty data.archivedCourses.rows}">
	<h2 class="text-muted">${title}</h2>
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
		<c:forEach items="${data.archivedCourses.rows}" var="archivedCourse" varStatus="i">
			<tr>
				<td id="courseid${i.index + data.activeCourses.size}">${archivedCourse.courseId}</td>
				<td id="coursename${i.index + data.activeCourses.size}">${archivedCourse.courseName}</td>
				<td class="align-center no-print">${archivedCourse.actions}</td>
			</tr>
		</c:forEach>
	</table>
	<br>
	<br>
	<br>
	<br>
</c:if>