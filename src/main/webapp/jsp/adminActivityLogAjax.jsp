<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/admin/activity" prefix="activity" %>

<div>
  <table id="activity-logs-table">
    <c:forEach items="${data.logs}" var="log">
      <activity:activityLogTableRow log="${log}"/>
    </c:forEach>
  </table>

  <div id="status-message">
    ${data.statusForAjax}
  </div>
</div>
