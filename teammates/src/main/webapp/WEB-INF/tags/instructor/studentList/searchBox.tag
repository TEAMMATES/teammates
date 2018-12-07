<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorStudentList - Student search box" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="searchBox" type="teammates.ui.template.InstructorStudentListSearchBox" required="true" %>
<div class="well well-plain">
  <div class="row">
    <div class="col-md-12">
      <form method="get" action="${searchBox.instructorSearchLink}" name="search_form">
        <div class="row">
          <div class="col-md-10">
            <div class="form-group">
              <input type="text" id="searchbox"
                  title="<%= Const.Tooltips.SEARCH_STUDENT %>"
                  name="<%= Const.ParamsNames.SEARCH_KEY %>"
                  class="form-control"
                  data-toggle="tooltip"
                  data-placement="top"
                  placeholder="e.g. Charles Shultz"
                  value="${searchBox.searchKey}">
            </div>
          </div>
          <div class="col-md-2 nav">
            <div class="form-group">
              <button id="buttonSearch" class="btn btn-primary" type="submit" value="Search">
                <span class="glyphicon glyphicon-search"></span> Find students
              </button>
            </div>
          </div>
        </div>
        <input type="hidden" name="<%= Const.ParamsNames.SEARCH_STUDENTS %>" value="true">
        <input type="hidden" name="<%= Const.ParamsNames.SEARCH_FEEDBACK_SESSION_DATA %>" value="false">
        <input type="hidden" name="<%= Const.ParamsNames.USER_ID %>" value="${searchBox.googleId}">
      </form>
    </div>
  </div>
</div>
