<%@ tag description="instructorSearch / instructorStudentList - Student List" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="courseId" required="true" %>
<%@ attribute name="courseIndex" required="true" %>
<%@ attribute name="hasSection" required="true" %>
<%@ attribute name="sections" type="java.util.Collection" required="true" %>
<%@ attribute name="fromStudentListPage" %>
<%@ attribute name="fromCourseDetailsPage" %>
<c:choose>
  <c:when test="${fromCourseDetailsPage}">
    <c:set var="tableHeaderClass" value="fill-primary" />
  </c:when>
  <c:otherwise>
    <c:set var="tableHeaderClass" value="background-color-medium-gray text-color-gray font-weight-normal" />
  </c:otherwise>
</c:choose>
<c:set var="STUDENT_COURSE_STATUS_YET_TO_JOIN" value="<%= Const.STUDENT_COURSE_STATUS_YET_TO_JOIN %>" />
<table class="table table-bordered table-striped<c:if test="${not fromCourseDetailsPage}"> table-responsive margin-0</c:if>">
  <c:choose>
    <c:when test="${not empty sections}">
      <thead class="${tableHeaderClass}">
        <tr id="resultsHeader-${courseIndex}">
          <th>Photo</th>
          <th id="button_sortsection-${courseIndex}" class="toggle-sort button-sort-none<c:if test="${not hasSection}"> hidden</c:if>">
            Section <span class="icon-sort unsorted"></span>
          </th>
          <th id="button_sortteam-${courseIndex}" class="button-sort-none toggle-sort">
            Team <span class="icon-sort unsorted"></span>
          </th>
          <th id="button_sortstudentname-${courseIndex}" class="button-sort-none toggle-sort">
            Student Name <span class="icon-sort unsorted"></span>
          </th>
          <th id="button_sortstudentstatus" class="button-sort-none toggle-sort">
            Status <span class="icon-sort unsorted"></span>
          </th>
          <th id="button_sortemail-${courseIndex}" class="button-sort-none toggle-sort">
            Email <span class="icon-sort unsorted"></span>
          </th>
          <th>Action(s)</th>
        </tr>
      </thead>
      <tbody>
        <c:set var="teamIndex" value="${-1}" />
        <c:set var="studentIndex" value="${-1}" />
        <c:forEach items="${sections}" var="section" varStatus="sectionIdx">
          <c:set var="sectionIndex" value="${sectionIdx.index}" />
          <c:if test="${fromStudentListPage}">
            <%-- generated here but to be appended to #sectionChoices in instructorStudentList.jsp
                will be transported via JavaScript in instructorStudentList.js --%>
            <div class="checkbox section-to-be-transported">
              <input id="section_check-${courseIndex}-${sectionIndex}" type="checkbox" checked class="section_check">
              <label for="section_check-${courseIndex}-${sectionIndex}">
                [${courseId}] : <c:out value="${section.sectionName}"/>
              </label>
            </div>
          </c:if>
          <c:forEach items="${section.teams}" var="team">
            <c:set var="teamIndex" value="${teamIndex + 1}" />
            <c:if test="${fromStudentListPage}">
              <%-- generated here but to be appended to #teamChoices in instructorStudentList.jsp
                  will be transported via JavaScript in instructorStudentList.js --%>
              <div class="checkbox team-to-be-transported">
                <input id="team_check-${courseIndex}-${sectionIndex}-${teamIndex}" type="checkbox" checked class="team_check">
                <label for="team_check-${courseIndex}-${sectionIndex}-${teamIndex}">
                  [${courseId}] : <c:out value="${team.teamName}"/>
                </label>
              </div>
            </c:if>
            <c:forEach items="${team.students}" var="student" varStatus="studentIdx">
              <c:set var="studentIndex" value="${studentIndex + 1}" />
              <c:if test="${fromStudentListPage}">
                <%-- generated here but to be appended to #teamChoices in instructorStudentList.jsp
                    will be transported via JavaScript in instructorStudentList.js --%>
                <div class="email-to-be-transported" id="student_email-c${courseIndex}.${studentIndex}">
                  ${student.studentEmail}
                </div>
              </c:if>
              <tr class="student_row" id="student-c${courseIndex}.${studentIndex}">
                <td id="studentphoto-c${courseIndex}.${studentIndex}">
                  <div class="profile-pic-icon-click align-center" data-link="${student.photoUrl}">
                    <a class="student-profile-pic-view-link btn-link">View Photo</a>
                    <img src="" alt="No Image Given" class="hidden">
                  </div>
                </td>
                <td id="studentsection-c${courseIndex}.${sectionIndex}"
                    <c:if test="${not hasSection}">class="hidden"</c:if>>
                  <c:out value="${section.sectionName}"/>
                </td>
                <td id="studentteam-c${courseIndex}.${sectionIndex}.${teamIndex}">
                  <c:out value="${team.teamName}"/>
                </td>
                <td id="studentname-c${courseIndex}.${studentIndex}">
                  <c:out value="${student.studentName}"/>
                </td>
                <td class="align-center">
                  ${student.studentStatus}
                </td>
                <td id="studentemail-c${courseIndex}.${studentIndex}">
                  <c:out value="${student.studentEmail}"/>
                </td>
                <td class="no-print align-center">
                  <c:set var="viewButtonEnabled" value="${section.allowedToViewStudentInSection}" />
                  <a class="btn btn-default btn-xs margin-bottom-7px<c:if test="${not viewButtonEnabled}"> disabled mouse-hover-only</c:if>"
                      <c:choose>
                        <c:when test="${not viewButtonEnabled}">
                          title="<%= Const.Tooltips.ACTION_NOT_ALLOWED %>"
                          href="javascript:;"
                        </c:when>
                        <c:otherwise>
                          title="<%= Const.Tooltips.COURSE_STUDENT_DETAILS %>"
                          href="${student.courseStudentDetailsLink}"
                          target="_blank" rel="noopener noreferrer"
                        </c:otherwise>
                      </c:choose>
                      data-toggle="tooltip"
                      data-placement="top">
                    View
                  </a>
                  <c:set var="editButtonEnabled" value="${section.allowedToModifyStudent}" />
                  <a class="btn btn-default btn-xs margin-bottom-7px<c:if test="${not editButtonEnabled}"> disabled mouse-hover-only</c:if>"
                      <c:choose>
                        <c:when test="${not editButtonEnabled}">
                          title="<%= Const.Tooltips.ACTION_NOT_ALLOWED %>"
                          href="javascript:;"
                        </c:when>
                        <c:otherwise>
                          title="<%= Const.Tooltips.COURSE_STUDENT_EDIT %>"
                          href="${student.courseStudentEditLink}"
                          target="_blank" rel="noopener noreferrer"
                        </c:otherwise>
                      </c:choose>
                      data-toggle="tooltip"
                      data-placement="top">
                    Edit
                  </a>
                  <c:if test="${fromCourseDetailsPage && student.studentStatus == STUDENT_COURSE_STATUS_YET_TO_JOIN}">
                    <c:set var="remindButtonEnabled" value="${section.allowedToModifyStudent}" />
                    <a class="course-student-remind-link btn btn-default btn-xs margin-bottom-7px<c:if test="${not remindButtonEnabled}"> disabled mouse-hover-only</c:if>"
                        <c:choose>
                          <c:when test="${not remindButtonEnabled}">
                            title="<%= Const.Tooltips.ACTION_NOT_ALLOWED %>"
                            href="javascript:;"
                          </c:when>
                          <c:otherwise>
                            title="<%= Const.Tooltips.COURSE_STUDENT_REMIND %>"
                            href="${student.courseStudentRemindLink}"
                          </c:otherwise>
                        </c:choose>
                        data-toggle="tooltip"
                        data-placement="top">
                      Send Invite
                    </a>
                  </c:if>
                  <c:set var="deleteButtonEnabled" value="${section.allowedToModifyStudent}" />
                  <a class="course-student-delete-link btn btn-default btn-xs margin-bottom-7px<c:if test="${not deleteButtonEnabled}"> disabled mouse-hover-only</c:if>"
                      data-student-name="${student.studentNameForJs}" data-course-id="${student.courseIdForJs}"
                      <c:choose>
                        <c:when test="${not deleteButtonEnabled}">
                          title="<%= Const.Tooltips.ACTION_NOT_ALLOWED %>"
                          href="javascript:;"
                        </c:when>
                        <c:otherwise>
                          title="<%= Const.Tooltips.COURSE_STUDENT_DELETE %>"
                          href="${student.courseStudentDeleteLink}"
                        </c:otherwise>
                      </c:choose>
                      data-toggle="tooltip"
                      data-placement="top">
                    Delete
                  </a>
                  <a class="btn btn-default btn-xs margin-bottom-7px"
                      href="${student.courseStudentRecordsLink}"
                      title="<%= Const.Tooltips.COURSE_STUDENT_RECORDS %>"
                      target="_blank" rel="noopener noreferrer"
                      data-toggle="tooltip"
                      data-placement="top">
                    All Records
                  </a>
                </td>
              </tr>
            </c:forEach>
          </c:forEach>
        </c:forEach>
      </tbody>
    </c:when>
    <c:otherwise>
      <thead class="${tableHeaderClass}">
        <tr>
          <th class="align-center color_white bold">There are no students in this course</th>
        </tr>
      </thead>
    </c:otherwise>
  </c:choose>
</table>
