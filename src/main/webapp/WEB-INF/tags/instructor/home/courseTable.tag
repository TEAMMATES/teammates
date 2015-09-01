<%@ tag description="instructorHome - Course table" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/feedbacks" prefix="tif" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="sessionRows" type="java.util.Collection" required="true" %>
<table class="table-responsive table table-striped table-bordered">
    <thead>
        <tr>
            <th id="button_sortname" onclick="toggleSort(this,1);"
                class="button-sort-none">
                Session Name<span class="icon-sort unsorted"></span></th>
            <th>Status</th>
            <th>
                <span title="<%= Const.Tooltips.FEEDBACK_SESSION_RESPONSE_RATE %>" 
                      data-toggle="tooltip" data-placement="top">Response Rate</span>
            </th>
            <th class="no-print">Action(s)</th>
        </tr>
    </thead>
    <c:if test="${empty sessionRows}">
        <tr>
            <td>
                <span class="text-muted">
                    This course does not have any sessions yet.
                </span>
            </td>
            <td></td>
            <td></td>
            <td></td>
        </tr>
    </c:if>
    <c:forEach items="${sessionRows}" var="sessionRow" varStatus="i">
        <tr id="session${i.index}">
            <td>
                ${sessionRow.name}
            </td>
            <td>
                <span title="${sessionRow.tooltip}" data-toggle="tooltip" data-placement="top">
                    ${sessionRow.status}
                </span>
            </td>
            <td class="session-response-for-test<c:if test="${sessionRow.recent}"> recent</c:if>">
                <a oncontextmenu="return false;" href="${sessionRow.href}">Show</a>
            </td>
            <td class="no-print">
                <tif:feedbackSessionActions actions="${sessionRow.actions}" />
            </td>
        </tr>
    </c:forEach>
</table>