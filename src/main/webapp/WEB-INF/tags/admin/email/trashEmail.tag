<%@ tag description="trash.tag - Trash email row" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="trashEmail" required="true" type="teammates.ui.template.AdminTrashEmailRow" %>

<tr id="${trashEmail.emailId}">
    <td>
        <a <c:forEach items="${trashEmail.actions.editButton.attributes}" var="attribute">
               ${attribute.key}="${attribute.value}"
           </c:forEach>>
               ${trashEmail.actions.editButton.content}
        </a>
        
        <a <c:forEach items="${trashEmail.actions.moveOutOfTrashButton.attributes}" var="attribute">
               ${attribute.key}="${attribute.value}"
           </c:forEach>>
               ${trashEmail.actions.moveOutOfTrashButton.content}
        </a>
    </td>
    <td><input value="${trashEmail.addressReceiver}" readonly="readonly" class="form-control"></td>
    <td><input value="${trashEmail.groupReceiver}" readonly="readonly" class="form-control"></td>
    <td><input value="${trashEmail.subject}" readonly="readonly" class="form-control"></td>
    <td>${trashEmail.date}</td>
</tr>