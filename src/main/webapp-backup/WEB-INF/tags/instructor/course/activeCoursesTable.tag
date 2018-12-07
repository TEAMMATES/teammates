<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorCourse - Course table" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="activeCourses" type="teammates.ui.template.ActiveCoursesTable" required="true" %>

<h2>Active courses</h2>
<table class="table table-bordered table-striped" id="tableActiveCourses">
  <thead class="fill-primary">
    <tr>
      <th id="button_sortcourseid" class="button-sort-none toggle-sort">
        Course ID<span class="icon-sort unsorted"></span>
      </th>
      <th id="button_sortcoursename" class="button-sort-none toggle-sort">
        Course Name<span class="icon-sort unsorted"></span>
      </th>
      <th id="button_sortcoursecreateddate" data-toggle-sort-comparator="sortDate" data-toggle-sort-extractor="dateStampExtractor" class="button-sort-none toggle-sort">
        Creation Date<span class="icon-sort unsorted"></span>
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
  <c:forEach items="${activeCourses.rows}" var="activeCourse" varStatus="i">
    <tr>
      <td id="courseid${i.index}">${activeCourse.courseId}</td>
      <td id="coursename${i.index}">${activeCourse.courseName}</td>
      <td
        id="coursecreateddate${i.index}"
        data-date-stamp="${activeCourse.createdAtDateStamp}"
        data-toggle="tooltip"
        data-original-title="${activeCourse.createdAtFullDateTimeString}">
          ${activeCourse.createdAtDateString}
      </td>
      <td id="course-stats-sectionNum-${i.index}">
        <a class="course-stats-link-${i.index}" oncontextmenu="return false;" href="${activeCourse.href}">Show</a>
      </td>
      <td id="course-stats-teamNum-${i.index}">
        <a class="course-stats-link-${i.index}" oncontextmenu="return false;" href="${activeCourse.href}">Show</a>
      </td>
      <td id="course-stats-totalStudentNum-${i.index}">
        <a class="course-stats-link-${i.index}" oncontextmenu="return false;" href="${activeCourse.href}">Show</a>
      </td>
      <td id="course-stats-unregisteredStudentNum-${i.index}">
        <a class="course-stats-link-${i.index}" oncontextmenu="return false;" href="${activeCourse.href}">Show</a>
      </td>
      <td class="align-center no-print">
        <c:forEach items="${activeCourse.actions}" var="button">
          <a ${button.attributesToString}>
            ${button.content}
          </a>
        </c:forEach>
      </td>
    </tr>
  </c:forEach>

  <c:if test="${empty activeCourses.rows}">
    <tr>
      <td></td>
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
