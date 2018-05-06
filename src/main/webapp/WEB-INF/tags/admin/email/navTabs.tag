<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="adminEmail.jsp - navbar tabs Compose, Sent, Draft, Trash" pageEncoding="UTF-8" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="isCompose" required="true" type="java.lang.Boolean" %>
<%@ attribute name="isSent" required="true" type="java.lang.Boolean" %>
<%@ attribute name="isDraft" required="true" type="java.lang.Boolean" %>
<%@ attribute name="isTrash" required="true" type="java.lang.Boolean" %>

<ul class="nav nav-tabs">
  <li role="presentation" class=${isCompose ? "active" : "" }>
    <a href="<%=Const.ActionURIs.ADMIN_EMAIL_COMPOSE_PAGE%>">Compose</a>
  </li>
  <li role="presentation" class=${isSent ? "active" : "" }>
    <a href="<%=Const.ActionURIs.ADMIN_EMAIL_SENT_PAGE%>">Sent</a>
  </li>
  <li role="presentation" class=${isDraft ? "active" : "" }>
    <a href="<%=Const.ActionURIs.ADMIN_EMAIL_DRAFT_PAGE%>">Draft</a>
  </li>
  <li role="presentation" class=${isTrash ? "active" : "" }>
    <a href="<%=Const.ActionURIs.ADMIN_EMAIL_TRASH_PAGE%>">Trash</a>
  </li>
</ul>

<br>
<br>
