<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorCourses - form which is currently used to load the courses table by ajax." pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.util.FieldValidator" %>

<form style="display:none;" id="ajaxForCourses" class="ajaxForCoursesForm"
    action="<%= Const.ActionURIs.INSTRUCTOR_COURSES_PAGE %>">
  <input type="hidden"
      name="<%= Const.ParamsNames.USER_ID %>"
      value="${data.account.googleId}">
  <input type="hidden"
      name="<%= Const.ParamsNames.IS_USING_AJAX %>"
      value="on">
</form>
