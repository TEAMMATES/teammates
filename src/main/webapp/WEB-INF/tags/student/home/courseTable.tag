<%@ tag description="studentHome - Course table" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/student/home" prefix="home" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="sessionRows" type="java.util.Collection" required="true" %>
<table class="table-responsive table table-striped">
    <c:choose>
        <c:when test="${not empty sessionRows}">
            <thead>
                <tr>
                    <th>Session Name</th>
                    <th>Deadline</th>
                    <th>Status</th>
                    <th class="studentHomeActions">Action(s)</th>
                </tr>
            </thead>
            <c:forEach items="${sessionRows}" var="sessionRow">
                <tr class="home_evaluations_row" id="evaluation${sessionRow.index}">
                    <td>${sessionRow.name}</td>
                    <td>${sessionRow.endTime}</td>
                    <td>
                        <span data-toggle="tooltip" data-placement="top" 
                              title="${sessionRow.tooltip}">
                            ${sessionRow.status}
                        </span>
                    </td>
                    <td class="studentHomeActions">
                        <home:rowActions actions="${sessionRow.actions}" index="${sessionRow.index}" />
                    </td>
                </tr>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <tr>
                <th class="align-center bold color_white">
                    Currently, there are no open evaluation/feedback sessions in this course. When a session is open for submission you will be notified.
                </th>
            </tr>
        </c:otherwise>
    </c:choose>
</table>