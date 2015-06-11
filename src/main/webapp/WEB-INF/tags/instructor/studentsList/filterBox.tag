<%@ tag description="instructorStudentList - Student filter box" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/studentsList" prefix="tisl" %>
<%@ tag import="teammates.ui.template.InstructorStudentsListFilterCourses" %>
<%@ attribute name="data" type="teammates.ui.template.InstructorStudentsListFilterBox" required="true" %>
<div id="moreOptionsDiv" class="well well-plain"<c:if test="${data.numOfCourses == 0}"> style="display:none;"</c:if>>
    <div class="row">
        <div class="col-md-3">
            <div class="checkbox">
                <input id="displayArchivedCourses_check" type="checkbox"<c:if test="${data.displayArchive}"> checked="checked"</c:if>>
                <label for="displayArchivedCourses_check">Display Archived Courses</label>
            </div>
        </div>
    </div>
    <form class="form-horizontal" role="form">
        <div class="row">
            <div class="col-sm-3">
                <div class="text-color-primary">
                    <strong>Courses</strong>
                </div>
                <br>
                <div class="checkbox">
                    <input type="checkbox" value="" id="course_all"> 
                    <label for="course_all"><strong>Select all</strong></label>
                </div>
                <br>
                <c:forEach items="${data.courses}" var="course" varStatus="i">
                    <div class="checkbox">
                        <input id="course_check-${i.index}" type="checkbox">
                        <label for="course_check-${i.index}">
                            [${course.courseId}] : ${course.courseName}
                        </label>
                    </div>
                </c:forEach>
            </div>
            <div class="col-sm-3">
                <div class="text-color-primary">
                    <strong>Sections</strong>
                </div>
                <br>
                <div class="checkbox" style="display:none;">
                    <input type="checkbox" value="" id="section_all"> 
                    <label for="section_all"><strong>Select all</strong></label>
                </div>
                <br>
                <div id="sectionChoices">
                </div>
            </div>
            <div class="col-sm-3">
                <div class="text-color-primary">
                    <strong>Teams</strong>
                </div>
                <br>
                <div class="checkbox" style="display:none;">
                    <input id="team_all" type="checkbox">
                    <label for="team_all"><strong>Select All</strong></label>
                </div>
                <br>
                <div id="teamChoices">
                </div>
            </div>
            <div class="col-sm-3">
                <div class="text-color-primary">
                    <strong>Emails</strong>
                </div>
                <br>
                <div class="checkbox" style="display:none;">
                    <input id="show_email" type="checkbox" checked="checked">
                    <label for="show_email"><strong>Show Emails</strong></label>
                </div>
                <br>
                <div id="emails">
                </div>
            </div>
        </div>
    </form>
</div>