<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="adminEmailLog.jsp - email log table row" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ attribute name="log" type="teammates.ui.template.AdminEmailTableRow" required="true" %>

<tr class="email-log-header">
  <td class="email-receiver">${log.receiver}</td>
  <td class="email-subject">${log.subject}</td>
  <td>${log.timeForDisplay}</td>
</tr>

<tr class="email-log-content-sanitized">
  <td colspan="3">
    <ul class="list-group">
      <li class="list-group-item list-group-item-info">
        <input type="text" value="${log.sanitizedContent}" class="form-control" readonly>
      </li>
    </ul>
  </td>
</tr>

<tr class="email-log-content-unsanitized" style="display:none;">
  <td colspan="3">
    <div class="well well-sm">
      <ul class="list-group">
        <li class="list-group-item list-group-item-success email-log-text email-content">
          <small>${log.unsanitizedContent}</small>
        </li>
      </ul>
    </div>
  </td>
</tr>
