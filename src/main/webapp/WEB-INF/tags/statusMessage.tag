<%@ tag description="Status message" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>

<%@ attribute name="statusMessagesToUser" type="java.util.Collection" %>
<%@ attribute name="doNotFocusToStatus" %>

<c:set var="STATUS_MESSAGE"><%= Const.ParamsNames.STATUS_MESSAGE %></c:set>
<c:set var="STATUS_MESSAGE_COLOR"><%= Const.ParamsNames.STATUS_MESSAGE_COLOR %></c:set>
<c:set var="ERROR"><%= Const.ParamsNames.ERROR %></c:set>

<div id="statusMessagesToUser">
    <c:forEach items="${statusMessagesToUser}" var="statusMessage">
        <div class="overflow-auto alert alert-${statusMessage.color} statusMessage">
            ${statusMessage.text}
        </div>
    </c:forEach>
</div>