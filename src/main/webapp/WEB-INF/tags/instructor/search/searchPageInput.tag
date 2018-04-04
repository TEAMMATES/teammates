<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorSearch.jsp - Instructor search page input" pageEncoding="UTF-8" %>
<%@ tag import="teammates.common.util.Const"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div>
  <form method="get" action="${data.instructorSearchLink}" name="search_form">
    <div class="well well-plain">

      <div class="form-group">
        <div class="input-group">
          <input type="text" name="searchkey"
              value="${data.searchKey}"
              title="Search for comment"
              placeholder="Your search keyword"
              class="form-control" id="searchBox">

          <span class="input-group-btn">
            <button class="btn btn-primary" type="submit"
                value="Search" id="buttonSearch">
              Search
            </button>
          </span>
        </div>

        <input type="hidden" name="user" value="${data.account.googleId}">
      </div>

      <div class="form-group">
        <ul class="list-inline">
          <li>
            <span data-toggle="tooltip" title="Tick the checkboxes to limit your search to certain categories"
                class="glyphicon glyphicon-info-sign">
            </span>
          </li>
          <li>
            <input id="students-check" type="checkbox"
                name="<%=Const.ParamsNames.SEARCH_STUDENTS%>" value="true"
                <c:if test="${data.searchForStudents || !data.searchFeedbackSessionData}">checked=""</c:if>>
            <label for="students-check">
              Students
            </label>
          </li>
          <li>
            <input id="search-feedback-sessions-data-check" type="checkbox"
                name="<%=Const.ParamsNames.SEARCH_FEEDBACK_SESSION_DATA%>" value="true"
                <c:if test="${data.searchFeedbackSessionData}">checked</c:if>>
            <label for="search-feedback-sessions-data-check">
              Questions, responses, comments on responses
            </label>
          </li>
        </ul>
      </div>
    </div>
  </form>
</div>
