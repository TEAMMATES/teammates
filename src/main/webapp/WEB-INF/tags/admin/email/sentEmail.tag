<%@ tag description="sent.tag - Sent email row" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="sentEmail" required="true" type="teammates.ui.template.AdminSentEmailRow" %>

<tr id="${sentEmail.emailId}">
    <td>
        <a <c:forEach items="${sentEmail.actions.editButton.attributes}" var="attribute">
               ${attribute.key}="${attribute.value}"
           </c:forEach>>
               ${sentEmail.actions.editButton.content}
        </a>
        
        <a <c:forEach items="${sentEmail.actions.deleteButton.attributes}" var="attribute">
               ${attribute.key}="${attribute.value}"
           </c:forEach>>
               ${sentEmail.actions.deleteButton.content}
        </a>
    </td>
    <td><input value="${sentEmail.addressReceiver}" readonly="readonly" class="form-control"></td>
    <td><input value="${sentEmail.groupReceiver}" readonly="readonly" class="form-control"></td>
    <td><input value="${sentEmail.subject}" readonly="readonly" class="form-control"></td>
    <td>${sentEmail.date}</td>
</tr>