<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="draft.tag - Draft email row" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="draftEmail" required="true" type="teammates.ui.template.AdminDraftEmailRow" %>

<tr id="${draftEmail.emailId}">
  <td>
    <a ${draftEmail.actions.editButton.attributesToString}>
      ${draftEmail.actions.editButton.content}
    </a>

    <a ${draftEmail.actions.deleteButton.attributesToString}>
      ${draftEmail.actions.deleteButton.content}
    </a>
  </td>
  <td><input value="${draftEmail.addressReceiver}" readonly class="form-control"></td>
  <td><input value="${draftEmail.groupReceiver}" readonly class="form-control"></td>
  <td><input value="${draftEmail.subject}" readonly class="form-control"></td>
  <td>${draftEmail.date}</td>
</tr>
