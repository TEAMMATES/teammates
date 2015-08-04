<%@ tag description="Status message" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<c:set var="STATUS_MESSAGE"><%= Const.ParamsNames.STATUS_MESSAGE %></c:set>
<c:set var="ERROR"><%= Const.ParamsNames.ERROR %></c:set>
<c:choose>
    <c:when test="${not empty requestScope[STATUS_MESSAGE]}">
        <div id="statusMessage"  class="alert alert-<c:choose><c:when test="${requestScope[ERROR]}">danger</c:when><c:otherwise>warning</c:otherwise></c:choose>">
            ${requestScope[STATUS_MESSAGE]}
        </div>
        <script type="text/javascript" src="/js/statusMessage.js"></script>
    </c:when>
    <c:otherwise>
        <div id="statusMessage" style="display: none;"></div>
    </c:otherwise>
</c:choose>