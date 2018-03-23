<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="Activity Log Table in Admin Activity Log Page" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/admin/activity" prefix="activity" %>
<%@ attribute name="logs" type="java.util.Collection" required="true" %>
<%@ tag import="teammates.common.util.Const" %>

<div class="panel panel-primary">
  <div class="panel-heading">
    <strong>Activity Log</strong>
  </div>
  <div class="table-responsive">
    <table class="table table-condensed data-table" id="activity-logs-table">
      <thead>
        <tr>
          <th width="10%">Date [Timing]</th>
          <th>[Role][Action][Google ID][Name][Email]</th>
        </tr>
      </thead>
      <tbody>
        <c:if test="${empty logs}">
          <tr id="noResultFoundMessage">
            <td colspan='2'><i>No application logs found</i></td>
          </tr>
        </c:if>
        <c:forEach items="${logs}" var="log">
          <activity:activityLogTableRow log="${log}"/>
        </c:forEach>
      </tbody>
    </table>
  </div>
</div>
