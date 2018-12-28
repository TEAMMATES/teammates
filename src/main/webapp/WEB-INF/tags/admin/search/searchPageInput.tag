<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="adminSearch.jsp - input panel" pageEncoding="UTF-8" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="searchKey" required="true" %>

<div class="well well-plain">
  <form class="form-horizontal" method="get" action="" id="activityLogFilter" role="form">
    <div class="panel-heading" id="filterForm">
      <div class="form-group">
        <div class="row">
          <div class="col-md-12">
            <span class="help-block">
              Tips: Surround key word to search a whole string or string contains punctuation like "-" "."
            </span>

            <div class="input-group">
              <input type="text" class="form-control" id="filterQuery"
                  name="<%=Const.ParamsNames.ADMIN_SEARCH_KEY%>"
                  value="${searchKey}">

              <span class="input-group-btn">
                <button class="btn btn-default" type="submit"
                    name="<%=Const.ParamsNames.ADMIN_SEARCH_BUTTON_HIT%>"
                    id="searchButton" value="true">Search</button>
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </form>
</div>
