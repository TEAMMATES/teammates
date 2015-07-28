<%@ tag description="instructorCourseDetails - Course Information Board" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="courseDetails" type="teammates.common.datatransfer.CourseDetailsBundle" required="true" %>
<%@ attribute name="studentsTable" type="teammates.ui.template.CourseDetailsStudentsTable" required="true" %>

<table class="table table-bordered table-striped">
    <c:set var="hasSection">${courseDetails.stats.sectionsTotal > 0}</c:set>
    <c:choose>
        <c:when test="${not empty studentsTable.rows}">
    <thead class="fill-primary">
        <tr id="resultsHeader-0">
            <th id="button_sortsection-0" class="button-sort-none<c:if test="${not hasSection}"> hidden</c:if>" onclick="toggleSort(this,1)">
                Section <span class="icon-sort unsorted"></span>
            </th>
            <th id="button_sortteam-0" class="button-sort-none" onclick="toggleSort(this,2)">
                Team <span class="icon-sort unsorted"></span>
            </th>
            <th id="button_sortstudentname-0" class="button-sort-none" onclick="toggleSort(this,3)">
                Student Name <span class="icon-sort unsorted"></span>
            </th>
            <th id="button_sortstudentstatus" class="button-sort-none" onclick="toggleSort(this,4)">
                Status <span class="icon-sort unsorted"></span>
            </th>
            <th>Action(s)</th>
        </tr>
    </thead>
    <c:forEach items="${studentsTable.rows}" var="row" varStatus="i">
        <tr class="student_row" id="student-c0.${i.index}">
            <td id="<%=Const.ParamsNames.SECTION_NAME%>" <c:if test="${not hasSection}">class="hidden"</c:if>>
                ${row.student.section}
            </td>
            <td id="<%=Const.ParamsNames.TEAM_NAME%>">${row.student.team}</td>
            <td id="<%=Const.ParamsNames.STUDENT_NAME%>-c0.${i.index}">${row.student.name}</td>
            <td class="align-center">${row.student.studentStatus}</td>
            <td class="no-print align-center">
                <c:forEach items="${row.actions}" var="action">
                    <a ${action.attributesToString}>
                        ${action.content}
                    </a>
                </c:forEach>
                <div class="btn-group">
                    <c:forEach items="${row.commentActions}" var="action">
                        <a ${action.attributesToString}>
                            ${action.content}
                        </a>
                    </c:forEach>
                    <ul class="dropdown-menu align-left" role="menu" aria-labelledby="dLabel">
                        <c:forEach items="${row.commentRecipientOptions}" var="option">
                            <li role="presentation">
                                <a role="menuitem" tabindex="-1" ${option.attributesToString}>
                                    ${option.content}
                                </a>
                            </li>
                        </c:forEach>
                    </ul>
                </div>
            </td>
        </tr>
        <c:if test="${i.index % 10 == 0}">
            <% out.flush(); %>
        </c:if>
    </c:forEach>
        </c:when>
        <c:otherwise>
            <thead class="fill-primary">
                <tr>
                    <th class="align-center color_white bold">There are no students in this course</th>
                </tr>
            </thead>
        </c:otherwise>
    </c:choose>
</table>