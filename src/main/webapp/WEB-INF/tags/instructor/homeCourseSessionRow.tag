<%@ tag description="instructorHome - Course table session row" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="sessionRow" type="teammates.ui.template.CourseTableSessionRow" required="true" %>
<%@ attribute name="index" required="true" %>
<tr id="session${index}">
    <td>
        ${sessionRow.name}
    </td>
    <td>
        <span title="${sessionRow.tooltip}" data-toggle="tooltip" data-placement="top">
            ${sessionRow.status}
        </span>
    </td>
    <td class="session-response-for-test${sessionRow.recent}">
        <a oncontextmenu="return false;" href="${sessionRow.href}">Show</a>
    </td>
    <td class="no-print">
        ${sessionRow.actions}
    </td>
</tr>