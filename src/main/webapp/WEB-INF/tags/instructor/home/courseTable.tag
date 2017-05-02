<%@ tag description="instructorHome - Course table" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/feedbacks" prefix="tif" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="sessionRows" type="java.util.Collection" required="true" %>
<table class="table-responsive table table-striped table-bordered">
    <thead>
        <tr>
            <th onclick="toggleSort(this);" class="button_sortname button-sort-none">
                Session Name<span class="icon-sort unsorted"></span></th>
            <th onclick="toggleSort(this,instructorHomeDateComparator);" class="button_sortstartdate button-sort-none">Start Date<span class="icon-sort unsorted"></span></th>
            <th onclick="toggleSort(this,instructorHomeDateComparator);" class="button_sortenddate button-sort-none">End Date<span class="icon-sort unsorted"></span></th>
            <th>Status</th>
            <th>
                <span class="text-nowrap" title="<%= Const.Tooltips.FEEDBACK_SESSION_RESPONSE_RATE %>" 
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
            <td></td>
            <td></td>
        </tr>
    </c:if>
    <c:forEach items="${sessionRows}" var="sessionRow" varStatus="i">
        <tr id="session${i.index}">
            <td>
                ${sessionRow.name}
            </td>
            <td class="text-nowrap">
                <span title="${sessionRow.startTimeToolTip}" data-toggle="tooltip">${sessionRow.startTime}</span>
            </td>
            <td class="text-nowrap">
                 <span title="${sessionRow.endTimeToolTip}" data-toggle="tooltip">${sessionRow.endTime}</span>
            </td>
            <td>
                <span title="${sessionRow.tooltip}" data-toggle="tooltip" data-placement="top">
                    ${sessionRow.status}
                </span>
            </td>
            <td class="session-response-for-test">
                <a oncontextmenu="return false;" href="${sessionRow.href}">Show</a>
            </td>
            <td class="no-print text-nowrap padding-right-25px">
                <tif:feedbackSessionActions actions="${sessionRow.actions}" />
            </td>
        </tr>
    </c:forEach>
</table>