<%@ tag description="instructorCourseDetails - Course Information Board" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>

<table class="table table-bordered table-striped">
    <c:set var="hasSection">
        ${data.courseDetails.stats.sectionsTotal > 0}
    </c:set>

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
    
    <c:forEach items="${data.studentsTable}" var="row" varStatus="i">
        <tr class="student_row" id="student${i.index}">
            <c:if test="${hasSection}">
                <td id="<%=Const.ParamsNames.SECTION_NAME%>">${row.student.section}</td>
            </c:if>
            <td id="<%=Const.ParamsNames.TEAM_NAME%>">${row.student.team}</td>
            <td id="<%=Const.ParamsNames.STUDENT_NAME%>">${row.student.name}</td>
            <td class="align-center">
                <c:choose>
                    <c:when test="row.student.registered">
                        <%= Const.STUDENT_COURSE_STATUS_JOINED %>
                    </c:when>
                    <c:otherwise>
                        <%= Const.STUDENT_COURSE_STATUS_YET_TO_JOIN %>
                    </c:otherwise>
                </c:choose>
            </td>
            <td class="align-center no-print">
                <c:forEach items="row.actions" var="action">
                    <a  <c:forEach items="action.attributes" var="attribute">
                            ${attribute.key}="${attribute.value}"
                        </c:forEach> >
                        ${attribute.content}
                    </a>
                </c:forEach>
                
                <div class="btn-group">
                    <c:forEach items="row.commentActions" var="action">
                        <a  <c:forEach items="action.attributes" var="attribute">
                                ${attribute.key}="${attribute.value}"
                            </c:forEach> >
                            ${attribute.content}
                        </a>
                    </c:forEach>
                    
                    <ul class="dropdown-menu" role="menu" aria-labelledby="dLabel" style="text-align:left;">
                        <li role="presentation">
                            <a class="t_student_details_tostudent-c<%=data.courseDetails.course.id %>.<%=idx%>" role="menuitem" tabindex="-1" 
                                    href="<%=data.getCourseStudentDetailsLink(student) + "&" + Const.ParamsNames.SHOW_COMMENT_BOX + "=student"%>">
                                To student: <%=sanitizeForHtml(student.name)%>
                            </a>
                        </li>
                        
                        <li role="presentation">
                            <a class="t_student_details_toteam-c<%=data.courseDetails.course.id %>.<%=idx%>" role="menuitem" tabindex="-1" 
                                    href="<%=data.getCourseStudentDetailsLink(student) + "&" + Const.ParamsNames.SHOW_COMMENT_BOX + "=team"%>">
                                To team: <%=sanitizeForHtml(student.team)%>
                            </a>
                        </li>
                        
                        <% if (student.section != null && !student.section.equals("None")) { %>
                            <li role="presentation">
                                <a class="t_student_details_tosection-c<%=data.courseDetails.course.id %>.<%=idx%>" role="menuitem" tabindex="-1" 
                                        href="<%=data.getCourseStudentDetailsLink(student) + "&" + Const.ParamsNames.SHOW_COMMENT_BOX + "=section"%>">
                                    To section: <%=sanitizeForHtml(student.section)%></a>
                            </li>
                        <% } %>
                    </ul>
                </div>
            </td>
        </tr>
    </c:forEach>
    
    <%  int idx = -1;
        for (StudentAttributes student : data.students) {
            idx++;
    %>
            <tr class="student_row" id="student<%=idx%>">
                <%  if (hasSection) { %>
                    <td id="<%=Const.ParamsNames.SECTION_NAME%>">${student.section}</td>
                <%  } %>
                <td id="<%=Const.ParamsNames.TEAM_NAME%>">${student.team}</td>
                <td id="<%=Const.ParamsNames.STUDENT_NAME%>">${student.name}</td>
                <td class="align-center">
                    <c:choose>
                        <c:when test="student.registered">
                            <%= Const.STUDENT_COURSE_STATUS_JOINED %>
                        </c:when>
                        <c:otherwise>
                            <%= Const.STUDENT_COURSE_STATUS_YET_TO_JOIN %>
                        </c:otherwise>
                    </c:choose>
                </td>
                <td class="align-center no-print">
                    <a class="btn btn-default btn-xs" href="<%=data.getCourseStudentDetailsLink(student)%>" data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.COURSE_STUDENT_DETAILS%>" 
                            <% if (!data.currentInstructor.isAllowedForPrivilege(student.section, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS)) { %>
                                disabled="disabled"
                            <% } %> >
                        View
                    </a>
                    
                    <a class="btn btn-default btn-xs" href="<%=data.getCourseStudentEditLink(student)%>" data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.COURSE_STUDENT_EDIT%>"
                            <% if (!data.currentInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT)) { %>
                                disabled="disabled"
                            <% } %> >
                        Edit
                    </a>
                    
                    <%  if (data.getStudentStatus(student).equals(Const.STUDENT_COURSE_STATUS_YET_TO_JOIN)) { %>
                        <a class="btn btn-default btn-xs" href="<%=data.getCourseStudentRemindLink(student)%>"
                                onclick="return toggleSendRegistrationKey()"
                                data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.COURSE_STUDENT_REMIND%>"
                                <% if (!data.currentInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT)) { %>
                                    disabled="disabled"
                                <% } %> >
                            Send Invite
                        </a>
                    <% } %>
                    
                    <a class="btn btn-default btn-xs" href="<%=data.getCourseStudentDeleteLink(student)%>"
                            onclick="return toggleDeleteStudentConfirmation('<%=sanitizeForJs(student.course)%>','<%=sanitizeForJs(student.name)%>')"
                            data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.COURSE_STUDENT_DELETE%>"
                            <% if (!data.currentInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT)) { %>
                                disabled="disabled"
                            <% } %> >
                        Delete
                    </a>
                    
                    <a class="btn btn-default btn-xs" href="<%=data.getStudentRecordsLink(student)%>" data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.COURSE_STUDENT_RECORDS%>" > 
                        All Records
                    </a>
                    
                    <div class="btn-group">
                        <a class="btn btn-default btn-xs cursor-default" href="javascript:;"
                                data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.COURSE_STUDENT_COMMENT%>"
                                <% if (!data.currentInstructor.isAllowedForPrivilege(student.section, Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS)) { %>
                                    disabled="disabled"
                                <% } %> > 
                            Add Comment
                        </a>

                        <a href="javascript:;" class="btn btn-default btn-xs dropdown-toggle" data-toggle="dropdown"
                                <% if (!data.currentInstructor.isAllowedForPrivilege(student.section, Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS)) { %>
                                    disabled="disabled"
                                <% } %> >
                            <span class="caret"></span>
                            <span class="sr-only">Add comments</span>
                        </a>
                        
                        <ul class="dropdown-menu" role="menu" aria-labelledby="dLabel" style="text-align:left;">
                            <li role="presentation">
                                <a class="t_student_details_tostudent-c<%=data.courseDetails.course.id %>.<%=idx%>" role="menuitem" tabindex="-1" 
                                        href="<%=data.getCourseStudentDetailsLink(student) + "&" + Const.ParamsNames.SHOW_COMMENT_BOX + "=student"%>">
                                    To student: <%=sanitizeForHtml(student.name)%>
                                </a>
                            </li>
                            
                            <li role="presentation">
                                <a class="t_student_details_toteam-c<%=data.courseDetails.course.id %>.<%=idx%>" role="menuitem" tabindex="-1" 
                                        href="<%=data.getCourseStudentDetailsLink(student) + "&" + Const.ParamsNames.SHOW_COMMENT_BOX + "=team"%>">
                                    To team: <%=sanitizeForHtml(student.team)%>
                                </a>
                            </li>
                            
                            <% if (student.section != null && !student.section.equals("None")) { %>
                                <li role="presentation">
                                    <a class="t_student_details_tosection-c<%=data.courseDetails.course.id %>.<%=idx%>" role="menuitem" tabindex="-1" 
                                            href="<%=data.getCourseStudentDetailsLink(student) + "&" + Const.ParamsNames.SHOW_COMMENT_BOX + "=section"%>">
                                        To section: <%=sanitizeForHtml(student.section)%></a>
                                </li>
                            <% } %>
                        </ul>
                    </div>
                </td>
            </tr>
        <%  if (idx % 10 == 0) {
                out.flush();
            } %>
    <%  } %>
</table>