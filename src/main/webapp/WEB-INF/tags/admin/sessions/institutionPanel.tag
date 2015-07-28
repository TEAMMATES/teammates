<%@ tag description="Admin sessions - institution panel" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib tagdir="/WEB-INF/tags/admin/sessions" prefix="adminSessions" %>
<%@ attribute name="institutionPanel" type="teammates.ui.template.InstitutionPanel" required="true"%>
<%@ attribute name="tableIndex" required="true"%>
<%@ attribute name="showAll" required="true"%>
<div class="panel panel-primary">
    <ul class="nav nav-pills nav-stacked">
        <li id="pill_${tableIndex}" class="active">
            <a href="#" onclick="toggleContent(${tableIndex}); return false;">
                <span class="badge pull-right" id="badge_${tableIndex}" style="display: none">
                    ${fn:length(institutionPanel.feedbackSessionRows)}
                </span>
                <strong>${institutionPanel.institutionName}</strong>
            </a>
        </li>
    </ul>
    <div class="table-responsive" id="table_${tableIndex}">
        <table class="table table-striped dataTable">
            <thead>
                <tr>
                    <th>Status</th>
                    <th onclick="toggleSort(this,2)"
                        class="button-sort-non">[Course ID] Session Name &nbsp; <span
                        class="icon-sort unsorted"></span>
                    </th>
                    <th>Response Rate</th>
                    <th onclick="toggleSort(this,4,sortDate)"
                        class="button-sort-non">Start Time&nbsp;
                        <span class="icon-sort unsorted"></span>
                    </th>
                    <th onclick="toggleSort(this,5,sortDate)"
                        class="button-sort-non">End Time&nbsp; <span
                        class="icon-sort unsorted"></span></th>
                    <th onclick="toggleSort(this,6)"
                        class="button-sort-non">Creator
                        <span class="icon-sort unsorted"></span></th>
                </tr>
            </thead>

            <tbody>
                <c:forEach items="${institutionPanel.feedbackSessionRows}" var="feedbackSessionRow">
                    <c:if test="${showAll or (not feedbackSessionRow.endsWithTmt)}">
                        <adminSessions:feedbackSessionRow feedbackSessionRow="${feedbackSessionRow}" />
                    </c:if>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>