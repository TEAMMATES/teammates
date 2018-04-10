<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorCourseEdit - Panel Heading of Instructor List" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="index" required="true" %>
<%@ attribute name="resendInviteButton" type="teammates.ui.template.ElementTag" required="true" %>
<%@ attribute name="editButton" type="teammates.ui.template.ElementTag" required="true" %>
<%@ attribute name="cancelButton" type="teammates.ui.template.ElementTag" required="true" %>
<%@ attribute name="deleteButton" type="teammates.ui.template.ElementTag" required="true" %>
<%@ attribute name="instructor" type="teammates.common.datatransfer.attributes.InstructorAttributes" required="true" %>

<div class="panel-heading">
  <strong>Instructor ${index}:</strong>
  <div class="pull-right">
    <div class="display-icon" style="display:inline;"></div>

    <c:if test="${not empty resendInviteButton}">
      <a ${resendInviteButton.attributesToString}>
        ${resendInviteButton.content}
      </a>
    </c:if>

    <form style="display:none;" id="edit-${index}" class="editForm" action="<%=Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE%>">
      <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID %>" value="${instructor.courseId}">
      <input type="hidden" name="<%=Const.ParamsNames.INSTRUCTOR_EMAIL%>" value="${instructor.email}">
      <input type="hidden" name="<%=Const.ParamsNames.COURSE_EDIT_MAIN_INDEX%>" value="${index}">
      <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="${data.account.googleId}">
    </form>

    <a ${editButton.attributesToString}>
      ${editButton.content}
    </a>

    <a ${cancelButton.attributesToString}>
      ${cancelButton.content}
    </a>

    <a ${deleteButton.attributesToString}>
      ${deleteButton.content}
    </a>
  </div>
</div>
