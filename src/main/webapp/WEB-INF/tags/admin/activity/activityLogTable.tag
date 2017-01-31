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
                <c:forEach items="${logs}" var="log" varStatus="count">
                    <tr id="${count.first ? "first-row" : ""}">
                        <td class="${log.tableCellClass}">
                            <a onclick="submitLocalTimeAjaxRequest('${log.logTime}','${log.userGoogleId}','${log.displayedRole}', this);">
                                ${log.displayedLogTime}
                            </a>
                            <p class="localTime"></p>
                            <p class="${log.timeTakenClass}">
                                <strong>${log.displayedLogTimeTaken}</strong>
                            </p>
                        </td>
                        <td class="${log.tableCellClass}">
                            <form method="get" action="${Const.ActionURIs.ADMIN_ACTIVITY_LOG_PAGE}">
                                <h4 class="list-group-item-heading">
                                    <span class="${log.userRoleIconClass}"></span>
                                    <span class="${log.masqueradeUserRoleIconClass}"></span>
                                    <a href="${log.displayedActionUrl}" class="${log.actionTextClass}" target="_blank">
                                        ${log.actionName}
                                    </a>
                                    <small>
                                        id: ${log.logId} 
                                        [
                                        ${log.userName}
                                        <c:choose>
                                            <c:when test="${log.hasUserHomeLink}">
                                                <a href="${log.userHomeLink}" target="_blank">${log.userGoogleId}</a>
                                            </c:when>
                                            <c:otherwise>
                                                ${log.displayedRole}
                                            </c:otherwise>   
                                        </c:choose>
                                        <c:choose>
                                            <c:when test="${log.hasUserEmail}">
                                                <a href="mailto:${log.userEmail}" target="_blank">${log.userEmail}</a>
                                            </c:when>
                                            <c:otherwise>
                                                ${log.userEmail}
                                            </c:otherwise>   
                                        </c:choose>
                                        ]
                                    </small>
                                    <button type="submit" class="btn btn-xs ${log.actionButtonClass}">
                                        <span class="glyphicon glyphicon-zoom-in"></span>
                                    </button>
                                    <input type="hidden" name="filterQuery" value="person:${log.userIdentity}">
                                    <input class="ifShowAll_for_person" type="hidden" name="all" value="false">
                                    <input class="ifShowTestData_for_person" type="hidden" name="testdata"
                                        value="false">
                                </h4>
                                <div>${log.displayedMessage}</div>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>