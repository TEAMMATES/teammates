<%@ tag description="Status message" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="doNotFocusToStatus" %>

<c:set var="STATUS_MESSAGE"><%= Const.ParamsNames.STATUS_MESSAGE %></c:set>
<c:set var="STATUS_MESSAGE_COLOR"><%= Const.ParamsNames.STATUS_MESSAGE_COLOR %></c:set>
<c:set var="ERROR"><%= Const.ParamsNames.ERROR %></c:set>

<c:set var="color">
    <c:choose>
        <c:when test="${requestScope[ERROR]}">
            danger
        </c:when>
        <c:when test="${empty requestScope[STATUS_MESSAGE_COLOR]}">
            info
        </c:when>
        <c:otherwise>
            ${requestScope[STATUS_MESSAGE_COLOR]}
        </c:otherwise>
    </c:choose>
</c:set>

<c:choose>
    <c:when test="${not empty requestScope[STATUS_MESSAGE]}">
        <div id="statusMessage" class="alert alert-${color}">
            ${requestScope[STATUS_MESSAGE]}
        </div>
        <c:if test="${not doNotFocusToStatus}">
            <script type="text/javascript" src="/js/statusMessage.js"></script>
        </c:if>
    </c:when>
    <c:otherwise>
        <div id="statusMessage" style="display: none;"></div>
    </c:otherwise>
</c:choose>