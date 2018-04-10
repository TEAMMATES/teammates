<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="Admin Search - Form to open email application with default values" pageEncoding="UTF-8" %>
<%@ attribute name="student" type="teammates.ui.template.AdminSearchStudentRow" required="true" %>
<%@ attribute name="linkTitle" required="true" %>
<%@ attribute name="subjectType" required="true" %>
<%@ attribute name="relatedLink" required="true" %>
<%@ attribute name="sessionStatus" %>

<form class="open-email-application-default-values">
  <strong id="feedback-session-name">${linkTitle}</strong>
  <button type="submit" class="btn btn-xs btn-primary margin-left-7px margin-bottom-7px">
    <span class="glyphicon glyphicon-send" aria-hidden="true"></span>
    Send Mail
  </button>
  <input type="hidden" name="courseName" value="${student.courseName}">
  <input type="hidden" name="courseId" value="${student.courseId}">
  <input type="hidden" name="studentName" value="${student.name}">
  <input type="hidden" name="subjectType" value="${subjectType}">
  <input type="hidden" name="sessionStatus" value="${sessionStatus}">
  <input name="relatedLink" value="${relatedLink}" readonly class="form-control">
</form>
