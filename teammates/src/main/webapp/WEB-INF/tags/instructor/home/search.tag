<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorHome - Student search bar" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<div class="well well-plain">
  <div class="row">
    <div class="col-md-12">
      <form method="get" action="${data.instructorSearchLink}"
          name="search_form">
        <div class="input-group">
          <input type="text" id="searchbox"
              title="<%= Const.Tooltips.SEARCH_STUDENT %>"
              name="<%= Const.ParamsNames.SEARCH_KEY %>"
              class="form-control"
              data-toggle="tooltip"
              data-placement="top"
              placeholder="e.g. Charles Shultz, charles@gmail.com">
          <span class="input-group-btn">
            <button class="btn btn-default" type="submit" value="Search" id="buttonSearch">
              Search
            </button>
          </span>
        </div>
        <input type="hidden" name="<%= Const.ParamsNames.SEARCH_STUDENTS %>" value="true">
        <input type="hidden" name="<%= Const.ParamsNames.SEARCH_FEEDBACK_SESSION_DATA %>" value="false">
        <input type="hidden" name="<%= Const.ParamsNames.USER_ID %>" value="${data.account.googleId}">
      </form>
    </div>
  </div>
</div>
