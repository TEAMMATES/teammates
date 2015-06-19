<%@ tag description="instructorStudentList - Ajax result" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="courseId" required="true" %>
<%@ attribute name="courseIndex" required="true" %>
<%@ attribute name="hasSection" required="true" %>
<%@ attribute name="sections" required="true" %>
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