<%@ tag description="instructorFeedbackResultsBottom - Users with No Response Panel" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const"%>
<%@ attribute name="noResponsePanel" type="teammates.ui.template.InstructorFeedbackResultsNoResponsePanel" required="true" %>
<c:choose>
    <c:when test="${not empty noResponsePanel.emails}">
        <div class="panel-body padding-0">
            <table class="table table-striped table-bordered margin-0">
                <thead class="background-color-medium-gray text-color-gray font-weight-normal">
                    <tr>
                        <th id="button_sortFromTeam" class="button-sort-ascending"
                            onclick="toggleSort(this,1)" style="width: 15%;">
                            Team<span class="icon-sort unsorted"></span>
                        </th>
                        <th id="button_sortTo" class="button-sort-none"
                            onclick="toggleSort(this,2)" style="width: 15%;">
                            Name<span class="icon-sort unsorted"></span>
                        </th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${noResponsePanel.emails}" var="email">
                        <tr>
                            <td>${noResponsePanel.teams[email]}</td>
                            <td>${noResponsePanel.names[email]}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:when>
    <c:otherwise>
        <div class="panel-body">
            All students have responded to some questions in this session.
        </div>
    </c:otherwise>
</c:choose>