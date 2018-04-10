<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="adminEmailLog.jsp - filter panel in Admin Email Log Page" pageEncoding="UTF-8" %>
<%@ attribute name="filterQuery" required="true" %>
<%@ attribute name="queryKeywordsForReceiver" required="true" %>
<%@ attribute name="queryKeywordsForSubject" required="true" %>
<%@ attribute name="queryKeywordsForContent" required="true" %>

<div class="well well-plain">
  <form class="form-horizontal" method="post" action="/admin/adminEmailLogPage"
      id="activityLogFilter" role="form">

    <div class="panel-heading" id="filterForm">

      <div class="form-group">
        <div class="row">
          <div class="col-md-12">
            <div class="input-group">
              <span class="input-group-btn">
                <button class="btn btn-default"
                    type="submit"
                    name="search_submit">Filter</button>
              </span>

              <input type="text"
                  class="form-control"
                  id="filterQuery"
                  name="filterQuery"
                  value="${filterQuery}">
            </div>
          </div>
        </div>
      </div>

      <input type="hidden" id="query-keywords-for-receiver" value="${queryKeywordsForReceiver}">
      <input type="hidden" id="query-keywords-for-subject" value="${queryKeywordsForSubject}">
      <input type="hidden" id="query-keywords-for-content" value="${queryKeywordsForContent}">

      <div class="form-group">
        <a href="javascript:;" class="btn btn-link center-block" id="btn-toggle-reference">
          <span id="referenceText"> Show Reference</span><br>
          <span class="glyphicon glyphicon-chevron-down" id="detailButton"></span>
        </a>
      </div>

      <div id="filterReference">
        <div class="form-group">
          <div class="col-md-12">
            <div class="alert alert-info text-center">
              <span class="glyphicon glyphicon-filter"></span>
              A query is formed by a list of filters. Each filter is in the format
              <strong>&nbsp;[filter label]: [value1, value2, value3....]</strong><br>
            </div>

            <p class="text-center">
              <span class="glyphicon glyphicon-hand-right"></span>
              Combine filters with the <span class="label label-warning">AND</span> keyword or the
              <span class="label label-warning">|</span> separator.
            </p>
          </div>
        </div>
        <small>
          <div class="form-group">
            <div class="col-md-12">
              <div class="form-control-static">
                <strong>Sample Queries:</strong> <br>
                <ul>
                  <li>
                    E.g. receiver: alice@gmail.com AND subject: welcome,TEAMMATES AND after: 15/03/15
                  </li>
                  <li>
                    E.g. after: 13/3/15 AND before: 17/3/15 AND Receiver: teammates@test.com AND info:click,join link
                  </li>
                </ul>
              </div>
            </div>
          </div>

          <div class="form-group">
            <div class="col-md-12">
              <div class="form-control-static">
                <strong>Possible Labels:</strong>
                &nbsp;after, before, receiver, subject, info, version<br>
                <ul>

                  <li>E.g. after: 13/03/15</li>
                  <li>E.g. before: 13/03/15</li>
                  <li>E.g. receiver: alice@gmail.com</li>
                  <li>E.g. subject: Welcome,teammates</li>
                  <li>E.g. info: you can click the link below </li>
                  <li>E.g. info: link,cs1010 (Use "," to search multiple key strings)</li>
                  <li>E.g. version: 4.15,4.16</li>
                  <li>E.g. version: 4-15,4.16 (both "." and "-" are acceptable)</li>

                </ul>
              </div>
            </div>
          </div>
        </small>
      </div>
    </div>
  </form>
</div>
