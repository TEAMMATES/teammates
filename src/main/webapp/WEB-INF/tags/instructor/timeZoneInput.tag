<%@ tag description="instructorCourses / instructorCourseEdit / instructorFeedbackSessions / instructorFeedbackEdit - Time Zone Input" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="nameId" required="true" %>
<%@ attribute name="selectedTimeZone" %>
<%@ attribute name="tooltip" %>
<%@ attribute name="isDisabled" %>

<div class="input-group">
  <select class="form-control"
      name="${nameId}" id="${nameId}"
      <c:if test="${not empty selectedTimeZone}">data-time-zone="${selectedTimeZone}"</c:if>
      <c:if test="${not empty tooltip}">data-toggle="tooltip" data-placement="top" title="${tooltip}"</c:if>
      <c:if test="${not empty isDisabled}">disabled</c:if>>
  </select>
  <span class="input-group-btn">
    <input type="button" class="btn btn-primary" id="auto-detect-time-zone" value="Auto-Detect"
        <c:if test="${not empty isDisabled}">disabled</c:if>>
  </span>
</div>
