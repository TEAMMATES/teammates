<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorRecovery - form which is currently used to load deleted courses table by ajax." pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>

<form style="display:none;" id="ajaxForCourses" class="ajaxForCoursesForm"
    action="<%= Const.ActionURIs.INSTRUCTOR_RECOVERY_PAGE %>">
  <input type="hidden"
      name="<%= Const.ParamsNames.USER_ID %>"
      value="${data.account.googleId}">
  <input type="hidden"
      name="<%= Const.ParamsNames.IS_USING_AJAX %>"
      value="on">
</form>
