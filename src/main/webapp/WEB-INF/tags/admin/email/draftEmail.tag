<%@ tag description="draft.tag - Draft email row" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="draftEmail" required="true" type="teammates.ui.template.AdminDraftEmailRow" %>

<tr id="${draftEmail.emailId}">
    <td>
        <a <c:forEach items="${draftEmail.actions.editButton.attributes}" var="attribute">
           ${attribute.key}="${attribute.value}"
           </c:forEach>>
               ${draftEmail.actions.editButton.content}
        </a>
        
        <a <c:forEach items="${draftEmail.actions.deleteButton.attributes}" var="attribute">
           ${attribute.key}="${attribute.value}"
           </c:forEach>>
               ${draftEmail.actions.deleteButton.content}
        </a>
    </td>
    <td><input value="${draftEmail.addressReceiver}" readonly="readonly" class="form-control"></td>
    <td><input value="${draftEmail.groupReceiver}" readonly="readonly" class="form-control"></td>
    <td><input value="${draftEmail.subject}" readonly="readonly" class="form-control"></td>
    <td>${draftEmail.date}</td>
</tr>