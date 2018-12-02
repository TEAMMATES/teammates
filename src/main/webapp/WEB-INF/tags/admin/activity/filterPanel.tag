<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="Filter Panel in Admin Activity Log Page" pageEncoding="UTF-8" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="excludedLogRequestURIs" required="true" %>
<%@ attribute name="actionListAsHtml" required="true" %>
<%@ attribute name="shouldShowAllLogs" required="true" %>
<%@ attribute name="shouldShowTestData" required="true" %>
<%@ attribute name="filterQuery" required="true" %>
<%@ attribute name="queryKeywordsForInfo" required="true"%>

<div class="well well-plain">
  <form class="form-horizontal" method="post" action="/admin/adminActivityLogPage" id="activityLogFilter" role="form">
    <div class="panel-heading" id="filterForm">
      <div class="form-group">
        <div class="row">
          <div class="col-md-12">
            <div class="input-group">
              <span class="input-group-btn">
                <button class="btn btn-default" type="submit" name="search_submit">Filter</button>
              </span>
              <input type="text" class="form-control" id="filterQuery" name="filterQuery" value="${filterQuery}">
            </div>
          </div>
        </div>
      </div>

      <input id="query-keywords-default-for-info" type="hidden" value="<%= Const.ACTION_RESULT_FAILURE %>, <%= Const.ACTION_RESULT_SYSTEM_ERROR_REPORT %>">
      <input id="query-keywords-for-info" type="hidden" value="${queryKeywordsForInfo}">

      <div class="form-group">
        <a href="javascript:;" class="btn btn-link center-block" id="btn-toggle-reference">
          <span id="referenceText"> Show Reference</span><br>
          <span class="glyphicon glyphicon-chevron-down" id="detailButton"></span>
        </a>
      </div>

      <div id="filterReference">
        <div class="form-group">
          <div class="col-md-12">
            <h4 class="text-center">
              <span class="label label-primary">
                Append "?all=true" to URL to show all logs, omit to exclude following requests.
              </span>
            </h4>
            <div class="text-center">
              <ul class="list-group">
                <c:forEach items="${excludedLogRequestURIs}" var="uri">
                  <li class="list-group-item">${uri}</li>
                </c:forEach>
              </ul>
            </div>

            <div class="alert alert-info text-center">
              <span class="glyphicon glyphicon-filter"></span>
              A query is formed by a list of filters. Each filter is in the format
              <strong>&nbsp;[filter label]: [value1, value2, value3....]</strong><br>
            </div>

            <p class="text-center">
              <span class="glyphicon glyphicon-hand-right"></span>
              Combine filters with the <span class="label label-warning">AND</span> keyword or the <span class="label label-warning">|</span> separator.
            </p>
          </div>
        </div>

        <small>
          <div class="form-group">
            <div class="col-md-12">
              <div class="form-control-static">
                <strong>Sample Queries:</strong> <br>
                <ul>
                  <li>E.g. role: Instructor AND request: InstructorCourse, InstructorEval AND from: 15/03/13</li>
                  <li>E.g. from: 13/03/13 AND to: 17/03/13 AND person: teammates.test AND response: Pageload, System Error Report, Servlet Action Failure</li>
                </ul>
              </div>
            </div>
          </div>

          <div class="form-group">
            <div class="col-md-12">
              <div class="form-control-static">
                <strong>Possible Labels:</strong>&nbsp;from, to, person, role, request, response, version, time, info, id<br>
                <ul>
                  <li>E.g. from: 13/03/13</li>
                  <li>E.g. to: 13/03/13</li>
                  <li>E.g. person: teammates.coord</li>
                  <li>E.g. role: Instructor, Student, Unregistered</li>
                  <li>E.g. request: InstructorEval, StudentHome, evaluationclosingreminders</li>
                  <li>E.g. response: Pageload, System Error Report, Delete Course</li>
                  <li>E.g. version: 4.15, 4.16</li>
                  <li>E.g. version: 4-15, 4.16 (both "." and "-" are acceptable)</li>
                  <li>E.g. time: 1000 (means 1000ms) </li>
                  <li>E.g. info: Admin Account Management Page Load </li>
                  <li>E.g. info: Admin Account Management Page Load, Total, 90 (Use "," to search multiple key strings)</li>
                  <li>E.g. id: alice@gmail.com%20160131181745245, charlie@gmail.com%20160201182727734</li>
                </ul>
              </div>
            </div>
          </div>

          <div class="form-group">
            <div class="col-md-12">
              <p class="form-control-static">
                <strong>Possible Roles: </strong>Instructor, Student, Unknown
              </p>
            </div>
          </div>

          <div class="form-group">
            <div class="col-md-12">
              <p class="form-control-static">
                <strong>Possible Servlets Requests: </strong> <br>
                <br>
                <div class="table-responsive">
                  <table class="table table-condensed">
                    ${actionListAsHtml}
                  </table>
                </div>
              </p>
            </div>
          </div>

          <div class="form-group">
            <div class="col-md-12">
              <p class="form-control-static">
                <strong>Possible Responses:</strong> <br> <br>
                <div class="table-responsive">
                  <table class="table table-condensed">
                    <tr>
                      <td>
                        <ul class="list-group">
                          <li class="list-group-item">Remind Students About Evaluation</li>
                          <li class="list-group-item">Send Evaluation Closing reminders</li>
                          <li class="list-group-item">Send Evaluation Opening reminders</li>
                        </ul>
                      </td>

                      <td>
                        <ul class="list-group">
                          <li class="list-group-item">Publish Evaluation</li>
                          <li class="list-group-item">Unpublish Evaluation</li>
                          <li class="list-group-item">Send Registration</li>
                        </ul>
                      </td>

                      <td>
                        <ul class="list-group">
                          <li class="list-group-item">Pageload</li>
                          <li class="list-group-item">System Error Report</li>
                          <li class="list-group-item">Servlet Action Failure</li>
                        </ul>
                      </td>
                    </tr>

                    <tr>
                      <td>
                        <ul class="list-group">
                          <li class="list-group-item">Add New Course</li>
                          <li class="list-group-item">Delete Course</li>
                          <li class="list-group-item">Edit Course Info</li>
                        </ul>
                      </td>

                      <td>
                        <ul class="list-group">
                          <li class="list-group-item">Enroll Students</li>
                          <li class="list-group-item">Edit Student Details</li>
                          <li class="list-group-item">Delete Student</li>
                          <li class="list-group-item">Student Joining Course</li>
                        </ul>
                      </td>

                      <td>
                        <ul class="list-group">
                          <li class="list-group-item">Create New Evaluation</li>
                          <li class="list-group-item">Edit Evaluation Info</li>
                          <li class="list-group-item">Delete Evaluation</li>
                          <li class="list-group-item">Edit Submission</li>
                        </ul>
                      </td>
                    </tr>
                  </table>
                </div>
              </p>
            </div>
          </div>
        </small>
      </div>

    </div>

    <input type="hidden" name="pageChange" value="true">

    <%--
      - This parameter determines whether the logs with requests contained in "excludedLogRequestURIs"
      - in AdminActivityLogPageData should be shown. Use "?all=true" in URL to show all logs. This will keep showing all
      - logs despite any action or change in the page unless the page is reloaded with "?all=false"
      - or simply reloaded with this parameter omitted.
      --%>
    <input type="hidden" name="all" value="${shouldShowAllLogs}">

    <%--
      - This determines whether the logs related to testing data should be shown. Use "testdata=true" in URL
      - to show all testing logs. This will keep showing all logs from testing data despite any action or change in the page
      - unless the page is reloaded with "?testdata=false" or simply reloaded with this parameter omitted.
      --%>
    <input type="hidden" name="testdata" value="${shouldShowTestData}">
  </form>

  <%-- This form is used to store parameters for ajaxloader only --%>
  <form id="ajaxLoaderDataForm">
    <input type="hidden" name="searchTimeOffset" value="">

    <%--
      - This parameter determines whether the logs with requests contained in "excludedLogRequestURIs"
      - in AdminActivityLogPageData should be shown. Use "?all=true" in URL to show all logs. This will keep showing all
      - logs despite any action or change in the page unless the page is reloaded with "?all=false"
      - or simply reloaded with this parameter omitted.
      --%>
    <input type="hidden" name="all" value="${shouldShowAllLogs}">

    <%--
      - This determines whether the logs related to testing data should be shown. Use "testdata=true" in URL
      - to show all testing logs. This will keep showing all logs from testing data despite any action or change in the page
      - unless the page is reloaded with "?testdata=false" or simply reloaded with this parameter omitted.
      --%>
    <input type="hidden" name="testdata" value="${shouldShowTestData}">

    <input type="hidden" id="filterQuery" name="filterQuery" value="${filterQuery}">
  </form>

</div>
