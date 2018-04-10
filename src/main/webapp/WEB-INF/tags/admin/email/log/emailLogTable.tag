<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="adminEmailLog.jsp - email log table" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags/admin/email/log" prefix="adminEmailLog" %>
<%@ attribute name="logs" type="java.util.Collection" required="true" %>

<div class="panel panel-primary">
  <div class="panel-heading">
    <strong>Email Log</strong>
  </div>

  <div class="table-responsive">
    <table class="table data-table" id="email-logs-table">
      <thead>
        <tr>
          <th><strong>Receiver</strong></th>
          <th><strong>Subject</strong></th>
          <th><strong>Date</strong></th>
        </tr>
      </thead>

      <tbody>
        <c:forEach items="${logs}" var="log">
          <adminEmailLog:emailLogTableRow log="${log}" />
        </c:forEach>
      </tbody>
    </table>
  </div>
</div>
