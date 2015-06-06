<%@ tag description="instructorHome - Course tables' header" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<table class="table-responsive table table-striped table-bordered">
    <thead>
        <tr>
            <th id="button_sortname" onclick="toggleSort(this,1);"
                class="button-sort-none">
                Session Name<span class="icon-sort unsorted"></span></th>
            <th>Status</th>
            <th>
                <span title="<%=Const.Tooltips.FEEDBACK_SESSION_RESPONSE_RATE%>" 
                      data-toggle="tooltip" data-placement="top">Response Rate</span>
            </th>
            <th class="no-print">Action(s)</th>
        </tr>
    </thead>
    <jsp:doBody />
</table>