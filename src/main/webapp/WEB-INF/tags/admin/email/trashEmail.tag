<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="trash.tag - Trash email row" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="trashEmail" required="true" type="teammates.ui.template.AdminTrashEmailRow" %>

<tr id="${trashEmail.emailId}">
  <td>
    <a ${trashEmail.actions.editButton.attributesToString}>
      ${trashEmail.actions.editButton.content}
    </a>

    <a ${trashEmail.actions.moveOutOfTrashButton.attributesToString}>>
      ${trashEmail.actions.moveOutOfTrashButton.content}
    </a>
  </td>
  <td><input value="${trashEmail.addressReceiver}" readonly class="form-control"></td>
  <td><input value="${trashEmail.groupReceiver}" readonly class="form-control"></td>
  <td><input value="${trashEmail.subject}" readonly class="form-control"></td>
  <td>${trashEmail.date}</td>
</tr>
