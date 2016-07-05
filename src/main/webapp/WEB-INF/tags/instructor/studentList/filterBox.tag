<%@ tag description="instructorStudentList - Student filter box" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="filterBox" type="teammates.ui.template.InstructorStudentListFilterBox" required="true" %>
<div id="moreOptionsDiv" class="well well-plain"<c:if test="${empty filterBox.courses}"> style="display:none;"</c:if>>
    <div class="row">
        <div class="col-md-3">
            <div class="checkbox">
                <input id="displayArchivedCourses-check" type="checkbox"<c:if test="${filterBox.displayArchive}"> checked</c:if>>
                <label for="displayArchivedCourses-check">Display Archived Courses</label>
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
                    <input type="checkbox" value="" id="course-all"> 
                    <label for="course-all"><strong>Select all</strong></label>
                </div>
                <br>
                <c:forEach items="${filterBox.courses}" var="course" varStatus="i">
                    <div class="checkbox">
                        <input id="course-check-${i.index}" type="checkbox">
                        <label for="course-check-${i.index}">
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
                    <input type="checkbox" value="" id="section-all"> 
                    <label for="section-all"><strong>Select all</strong></label>
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
                    <input id="team-all" type="checkbox">
                    <label for="team-all"><strong>Select All</strong></label>
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
                    <input id="show-email" type="checkbox" checked>
                    <label for="show-email"><strong>Show Emails</strong></label>
                </div>
                <br>
                <div id="emails">
                </div>
            </div>
        </div>
    </form>
</div>