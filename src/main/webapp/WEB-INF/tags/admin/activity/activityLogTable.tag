<%@ tag description="Activity Log Table in Admin Activity Log Page" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="logs" type="java.util.Collection" required="true" %>

<div class="panel panel-primary">
    <div class="panel-heading">
        <strong>Activity Log</strong>
    </div>
    <div class="table-responsive">
        <table class="table table-condensed dataTable" id="logsTable">
            <thead>
                <tr>
                    <th width="10%">Date [Timing]</th>
                    <th>[Role][Action][Google ID][Name][Email]</th>
                </tr>
            </thead>
            <tbody>
                <c:if test="${empty logs}">
                    <tr id="noResultFoundMessage">
                        <td colspan='2'><i>No application logs found</i></td>
                    </tr>
                </c:if>
                <c:forEach items="${logs}" var="log">
                    ${log.logInfoForTableRowAsHtml}
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>