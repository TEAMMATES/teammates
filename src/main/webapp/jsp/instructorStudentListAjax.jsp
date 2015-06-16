<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="teammates.common.datatransfer.CourseDetailsBundle"%>
<%@ page import="teammates.common.datatransfer.SectionDetailsBundle" %>
<%@ page import="teammates.common.datatransfer.TeamDetailsBundle"%>
<%@ page import="teammates.common.datatransfer.InstructorAttributes" %>
<%@ page import="teammates.common.datatransfer.CourseAttributes" %>
<%@ page import="teammates.common.datatransfer.StudentAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackSessionAttributes"%>
<%@ page import="static teammates.ui.controller.PageData.sanitizeForJs"%>
<%@ page import="static teammates.ui.controller.PageData.sanitizeForHtml"%>
<%@ page import="teammates.ui.controller.InstructorStudentListAjaxPageData"%>
<%
    InstructorStudentListAjaxPageData data = (InstructorStudentListAjaxPageData) request.getAttribute("data");
%>
<%
String COURSE_STUDENT_EDIT = "Use this to edit the details of this student. <br>"
                           + "To edit multiple students in one go, you can use the enroll page: <br>"
                           + "Simply enroll students using the updated data and existing data will be updated accordingly";
String COURSE_STUDENT_DELETE = "Delete the student and the corresponding evaluations from the course";
String COURSE_STUDENT_RECORDS = "View all student's evaluations and feedbacks";
int courseIdx = data.courseIdx;
int sortIdx = 2;
if (data.courseSectionDetails.size() > 0) { %>
    <table class="table table-responsive table-striped table-bordered margin-0">
        <thead class="background-color-medium-gray text-color-gray font-weight-normal">
            <tr id="resultsHeader-<%= courseIdx %>">
                <th>Photo</th>
                <th id="button_sortsection-<%= courseIdx %>" class="button-sort-none<%= data.hasSection ? "": " hidden" %>" onclick="toggleSort(this,<%= sortIdx %>)">
                    Section <span class="icon-sort unsorted"></span>
                </th>
                <% sortIdx++; %>
                <th id="button_sortteam-<%= courseIdx %>" class="button-sort-none" onclick="toggleSort(this,<%= sortIdx %>)">
                    Team <span class="icon-sort unsorted"></span>
                </th>
                <% sortIdx++; %>
                <th id="button_sortstudentname-<%= courseIdx %>" class="button-sort-none" onclick="toggleSort(this,<%= sortIdx %>)">
                    Student Name <span class="icon-sort unsorted"></span>
                </th>
                <% sortIdx++; %>
                <th id="button_sortemail-<%= courseIdx %>" class="button-sort-none" onclick="toggleSort(this,<%= sortIdx %>)">
                    Email <span class="icon-sort unsorted"></span>
                </th>
                <% sortIdx++; %>
                <th>Action(s)</th>
            </tr>
            <tr id="searchNoResults-<%= courseIdx %>" class="hidden">
                <th class="align-center color_white bold">Cannot find students in this course</th>
            </tr>
        </thead>
        <tbody>
            <% int sectionIdx = -1;
            int teamIdx = -1;
            int studentIdx = -1;
            for (SectionDetailsBundle section : data.courseSectionDetails) {
                sectionIdx++;
                // append to section choice, will be transported later
                %>
                <div class="checkbox">
                    <input id="section_check-<%= courseIdx %>-<%= sectionIdx %>" type="checkbox" checked="checked" class="section_check">
                    <label for="section_check-<%= courseIdx %>-<%= sectionIdx %>">
                        [<%= data.course.id %>] : <%= data.sanitizeForHtml(section.name) %>
                    </label>
                </div>
                <% for (TeamDetailsBundle team: section.teams) {
                    teamIdx++;
                    // append to team choice, will be transported later
                    %>
                    <div class="checkbox">
                        <input id="team_check-<%= courseIdx %>-<%= sectionIdx %>-<%= teamIdx %>" type="checkbox" checked="checked" class="team_check">
                        <label for="team_check-<%= courseIdx %>-<%= sectionIdx %>-<%= teamIdx %>">
                            [<%= data.course.id %>] : <%= data.sanitizeForHtml(team.name) %>
                        </label>
                    </div>
                    <% for (StudentAttributes student: team.students) {
                        studentIdx++;
                        // append to email choice, will be transported later
                        %>
                        <div id="student_email-c<%= courseIdx %>.<%= studentIdx %>">
                            <%= student.email %>
                        </div>
                        <tr id="student-c<%= courseIdx %>.<%= studentIdx %>">
                            <td id="studentphoto-c<%= courseIdx %>.<%= studentIdx %>">
                                <div class="profile-pic-icon-click align-center" data-link="<%= data.emailPhotoUrlMapping.get(student.email) %>">
                                    <a class="student-profile-pic-view-link btn-link">View Photo</a>
                                    <img src="" alt="No Image Given" class="hidden">
                                </div>
                            </td>
                            <td id="studentsection-c<%= courseIdx %>.<%= sectionIdx %>"<%= data.hasSection ? "" : " class=\"hidden\"" %>>
                                <%= data.sanitizeForHtml(section.name) %>
                            </td>
                            <td id="studentteam-c<%= courseIdx %>.<%= sectionIdx %>.<%= teamIdx %>">
                                <%= data.sanitizeForHtml(team.name) %>
                            </td>
                            <td id="studentname-c<%= courseIdx %>.<%= studentIdx %>">
                                <%= data.sanitizeForHtml(student.name) %>
                            </td>
                            <td id="studentemail-c<%= courseIdx %>.<%= studentIdx %>">
                                <%= data.sanitizeForHtml(student.email) %>
                            </td>
                            <td class="no-print align-center">
                                <a class="btn btn-default btn-xs student-view-for-test"
                                   href="<%= data.getCourseStudentDetailsLink(student, data.account.googleId).replace("%40", "@") %>"
                                   title="View the details of the student"
                                   target="_blank"
                                   data-toggle="tooltip"
                                   data-placement="top"
                                   <%= data.sectionPrivileges.get(section.name).get("canviewstudentinsection") ? "" : "disabled=\"disabled\"" %>>
                                    View
                                </a>
                                &nbsp;
                                <a class="btn btn-default btn-xs student-edit-for-test"
                                   href="<%= data.getCourseStudentEditLink(student, data.account.googleId).replace("%40", "@") %>"
                                   title="<%= COURSE_STUDENT_EDIT %>"
                                   target="_blank"
                                   data-toggle="tooltip"
                                   data-placement="top"
                                   <%= data.sectionPrivileges.get(section.name).get("canmodifystudent") ? "" : "disabled=\"disabled\"" %>>
                                    Edit
                                </a>
                                &nbsp;
                                <a class="btn btn-default btn-xs student-delete-for-test"
                                   href="<%= data.getCourseStudentDeleteLink(student, data.account.googleId).replace("%40", "@") %>"
                                   onclick="return toggleDeleteStudentConfirmation('<%= data.sanitizeForJs(student.course) %>','<%= data.sanitizeForJs(student.name) %>')"
                                   title="<%= COURSE_STUDENT_DELETE %>"
                                   data-toggle="tooltip"
                                   data-placement="top"
                                   <%= data.sectionPrivileges.get(section.name).get("canmodifystudent") ? "" : "disabled=\"disabled\"" %>>
                                    Delete
                                </a>
                                &nbsp;
                                <a class="btn btn-default btn-xs student-records-for-test"
                                   href="<%= data.getStudentRecordsLink(student, data.account.googleId).replace("%40", "@") %>"
                                   title="<%= COURSE_STUDENT_RECORDS %>"
                                   target="_blank"
                                   data-toggle="tooltip"
                                   data-placement="top">
                                    All Records
                                </a>
                                &nbsp;
                                <div class="dropdown inline">
                                    <a class="btn btn-default btn-xs dropdown-toggle"
                                       href="javascript:;"
                                       data-toggle="dropdown"
                                       <%= data.sectionPrivileges.get(section.name).get("cangivecommentinsection") ? "" : "disabled=\"disabled\"" %>>
                                        Add Comment
                                    </a>
                                    <ul class="dropdown-menu align-left" role="menu" aria-labelledby="dLabel">
                                        <li role="presentation">
                                            <a target="_blank"
                                               role="menuitem"
                                               tabindex="-1"
                                               href="<%= data.getCourseStudentDetailsLink(student, data.account.googleId).replace("%40", "@") %>&addComment=student">
                                                Comment on <%= data.sanitizeForHtml(student.name) %>
                                            </a>
                                        </li>
                                        <li role="presentation">
                                            <a target="_blank"
                                               role="menuitem"
                                               tabindex="-1"
                                               href="<%= data.getCourseStudentDetailsLink(student, data.account.googleId).replace("%40", "@") %>&addComment=team">
                                                Comment on <%= data.sanitizeForHtml(team.name) %>
                                            </a>
                                        </li>
                                        <% if (data.hasSection) { %>
                                            <li role="presentation">
                                                <a target="_blank"
                                                   role="menuitem"
                                                   tabindex="-1"
                                                   href="<%= data.getCourseStudentDetailsLink(student, data.account.googleId).replace("%40", "@") %>&addComment=section">
                                                    Comment on <%= data.sanitizeForHtml(section.name) %>
                                                </a>
                                            </li>
                                        <% } %>
                                    </ul>
                                </div>
                            </td>
                        </tr>
                    <% }
                }
            } %>
        </tbody>
    </table>
<% } else { %>
    <table class="table table-responsive table-striped table-bordered margin-0">
        <thead class="background-color-medium-gray text-color-gray font-weight-normal">
            <tr>
                <th class="align-center color_white bold">There are no students in this course</th>
            </tr>
        </thead>
    </table>
<% } %>