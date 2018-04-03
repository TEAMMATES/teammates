<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="sent.tag - Sent email row" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="sentEmail" required="true" type="teammates.ui.template.AdminSentEmailRow" %>

<tr id="${sentEmail.emailId}">
  <td>
    <a ${sentEmail.actions.editButton.attributesToString}>
      ${sentEmail.actions.editButton.content}
    </a>

    <a ${sentEmail.actions.deleteButton.attributesToString}>
      ${sentEmail.actions.deleteButton.content}
    </a>
  </td>
  <td><input value="${sentEmail.addressReceiver}" readonly class="form-control"></td>
  <td><input value="${sentEmail.groupReceiver}" readonly class="form-control"></td>
  <td><input value="${sentEmail.subject}" readonly class="form-control"></td>
  <td>${sentEmail.date}</td>
</tr>
