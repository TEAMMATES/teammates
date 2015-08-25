<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="teammates.common.util.Const" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="form-group">
    <label for="<%= Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME %>" class="control-label">
        Name for copied sessions
    </label>
    <input class="form-control"
           id="<%= Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME %>"
           type="text"
           name="<%= Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME %>"
           value="${data.fsName}">
</div>

<c:forEach items="${data.courses}" var="course">
    <div class="checkbox">
        <label>
            <input type="checkbox"
                   name="<%= Const.ParamsNames.COPIED_COURSES_ID %>"
                   value="${course.id}">
            <c:choose>
                <c:when test="${course.id == data.courseId}">
	                [<span class="text-color-red">${course.id}</span>] : ${course.name}
	                <br>
	                <span class="text-color-red small">{Session currently in this course}</span> 
                </c:when>
                <c:otherwise>
                    [${course.id}] : ${course.name}
                </c:otherwise>
            </c:choose>
        </label>
    </div>
</c:forEach>

<input type="hidden" name="<%= Const.ParamsNames.COURSE_ID %>" value="${data.courseId}">
<input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="${data.fsName}">
<input type="hidden" name="<%= Const.ParamsNames.CURRENT_PAGE %>" value="${data.currentPage}">
