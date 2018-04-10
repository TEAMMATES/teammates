<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="displayDetails.tag - Displays teammates list on student course details page" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:choose>
  <c:when test="${(empty data.studentCourseDetailsPanel.teammates)
      or (fn:length(data.studentCourseDetailsPanel.teammates) eq 1)}">
    <span style="font-style: italic;">
      You have no team members or you are not registered in any team
    </span>
  </c:when>

  <c:otherwise>
    <table>
      <tbody>
        <c:forEach items="${data.studentCourseDetailsPanel.teammates}" var="student">
          <c:if test="${not (student.email eq data.studentCourseDetailsPanel.studentEmail)}">
            <tr>
              <td class="team-members-photo-cell" title="${student.name}" data-toggle="tooltip" data-placement="top">
                <img id="profilePic" src="${student.publicProfilePictureUrl}" class="profile-pic" data-toggle="modal">
              </td>
              <td class="team-members-details-cell">
                <label>Name:</label>
                <c:out value=" ${student.name}" /> <br>
                <label>Email:</label>
                <a href="mailto:${student.email}">
                  <c:out value="${student.email}"/>
                </a>
              </td>
            </tr>
          </c:if>
        </c:forEach>
      </tbody>
    </table>
  </c:otherwise>
</c:choose>
