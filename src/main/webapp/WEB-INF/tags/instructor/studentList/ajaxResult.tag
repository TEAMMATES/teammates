<%@ tag description="instructorStudentList - Ajax result" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="courseId" required="true" %>
<%@ attribute name="courseIndex" required="true" %>
<%@ attribute name="hasSection" required="true" %>
<%@ attribute name="sections" type="java.util.List" required="true" %>
<table class="table table-responsive table-striped table-bordered margin-0">
    <c:choose>
        <c:when test="${not empty sections}">
            <thead class="background-color-medium-gray text-color-gray font-weight-normal">
                <tr id="resultsHeader-${courseIndex}">
                    <th>Photo</th>
                    <th id="button_sortsection-${courseIndex}" class="button-sort-none<c:if test="${not hasSection}"> hidden</c:if>" onclick="toggleSort(this,2)">
                        Section <span class="icon-sort unsorted"></span>
                    </th>
                    <th id="button_sortteam-${courseIndex}" class="button-sort-none" onclick="toggleSort(this,3)">
                        Team <span class="icon-sort unsorted"></span>
                    </th>
                    <th id="button_sortstudentname-${courseIndex}" class="button-sort-none" onclick="toggleSort(this,4)">
                        Student Name <span class="icon-sort unsorted"></span>
                    </th>
                    <th id="button_sortemail-${courseIndex}" class="button-sort-none" onclick="toggleSort(this,5)">
                        Email <span class="icon-sort unsorted"></span>
                    </th>
                    <th>Action(s)</th>
                </tr>
                <tr id="searchNoResults-${courseIndex}" class="hidden">
                    <th class="align-center color_white bold">Cannot find students in this course</th>
                </tr>
            </thead>
            <tbody>
                <c:set var="teamIndex" value="${-1}" />
                <c:set var="studentIndex" value="${-1}" />
                <c:forEach items="${sections}" var="section" varStatus="sectionIdx">
                    <c:set var="sectionIndex" value="${sectionIdx.index}" />
                    <%-- generated here but to be appended to #sectionChoices in instructorStudentList.jsp
                         will be transported via JavaScript in instructorStudentListAjax.js --%>
                    <div class="checkbox section-to-be-transported">
                        <input id="section_check-${courseIndex}-${sectionIndex}" type="checkbox" checked="checked" class="section_check">
                        <label for="section_check-${courseIndex}-${sectionIndex}">
                            [${courseId}] : ${section.sectionName}
                        </label>
                    </div>
                    <c:forEach items="${section.teams}" var="team">
                        <c:set var="teamIndex" value="${teamIndex + 1}" />
                        <%-- generated here but to be appended to #teamChoices in instructorStudentList.jsp
                             will be transported via JavaScript in instructorStudentListAjax.js --%>
                        <div class="checkbox team-to-be-transported">
                            <input id="team_check-${courseIndex}-${sectionIndex}-${teamIndex}" type="checkbox" checked="checked" class="team_check">
                            <label for="team_check-${courseIndex}-${sectionIndex}-${teamIndex}">
                                [${courseId}] : ${team.teamName}
                            </label>
                        </div>
                        <c:forEach items="${team.students}" var="student" varStatus="studentIdx">
                            <c:set var="studentIndex" value="${studentIndex + 1}" />
                            <%-- generated here but to be appended to #teamChoices in instructorStudentList.jsp
                                 will be transported via JavaScript in instructorStudentListAjax.js --%>
                            <div class="email-to-be-transported" id="student_email-c${courseIndex}.${studentIndex}">
                                ${student.studentEmail}
                            </div>
                        </c:forEach>
                    </c:forEach>
                </c:forEach>
            </tbody>
        </c:when>
        <c:otherwise>
            <thead class="background-color-medium-gray text-color-gray font-weight-normal">
                <tr>
                    <th class="align-center color_white bold">There are no students in this course</th>
                </tr>
            </thead>
        </c:otherwise>
    </c:choose>
</table>