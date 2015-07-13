<%@ tag description="instructorCourseDetails - Course Information Board" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="courseDetails" type="teammates.common.datatransfer.CourseDetailsBundle" required="true" %>
<%@ attribute name="studentsTable" type="teammates.ui.template.CourseDetailsStudentsTable" required="true" %>

<table class="table table-bordered table-striped">
    <c:set var="hasSection">${courseDetails.stats.sectionsTotal > 0}</c:set>

    <thead class="fill-primary">
        <tr>
            <c:if test="${hasSection}">
                <th onclick="toggleSort(this, 1);" id="button_sortstudentsection" class="button-sort-none">
                    Section<span class="icon-sort unsorted"></span>
                </th>
            </c:if>
            <th onclick="toggleSort(this, ${hasSection ? 2 : 1});" id="button_sortstudentteam" class="button-sort-none">
                Team<span class="icon-sort unsorted"></span>
            </th>
            <th onclick="toggleSort(this, ${hasSection ? 3 : 2});" id="button_sortstudentname" class="button-sort-none">
                Student Name<span class="icon-sort unsorted"></span>
            </th>
            <th onclick="toggleSort(this, ${hasSection ? 4 : 3});" id="button_sortstudentstatus" class="button-sort-none">
                Status<span class="icon-sort unsorted"></span>
            </th>
            <th class="align-center no-print">
                Action(s)
            </th>
        </tr>
    </thead>
    
    <c:forEach items="${studentsTable.rows}" var="row" varStatus="i">
        <tr class="student_row" id="student${i.index}">
            <c:if test="${hasSection}">
                <td id="<%=Const.ParamsNames.SECTION_NAME%>">${row.student.section}</td>
            </c:if>
            <td id="<%=Const.ParamsNames.TEAM_NAME%>">${row.student.team}</td>
            <td id="<%=Const.ParamsNames.STUDENT_NAME%>">${row.student.name}</td>
            <td class="align-center">${row.student.studentStatus}</td>
            <td class="align-center no-print">
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
                    
                    <ul class="dropdown-menu" role="menu" aria-labelledby="dLabel" style="text-align:left;">
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
    <c:if test="${empty studentsTable.rows}">
        <tr>
            <c:if test="${hasSection}">
                <td></td>
            </c:if>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
        </tr>
    </c:if>
</table>