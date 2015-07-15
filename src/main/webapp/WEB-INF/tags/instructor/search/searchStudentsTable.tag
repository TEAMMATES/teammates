<%@ tag description="studentsSearchResults.tag - Display search students table for a course" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/search" prefix="search" %>
<%@ attribute name="studentTable" type="teammates.ui.template.SearchStudentsTable" required="true" %>
<%@ attribute name="courseIdx" required="true" %>

<div class="panel panel-info">
    <div class="panel-heading">
        <strong>[${studentTable.courseId}]</strong>
    </div>

    <div class="panel-body padding-0">
        <table class="table table-responsive table-striped table-bordered margin-0">
            <thead class="background-color-medium-gray text-color-gray font-weight-normal">
                <tr id="resultsHeader-${courseIdx}">
                    <th>Photo</th>
                    <th id="button_sortsection-${courseIdx}" class="button-sort-none" onclick="toggleSort(this,2)">
                        Section <span class="icon-sort unsorted"></span>
                    </th>
                    <th id="button_sortteam-${courseIdx}" class="button-sort-none" onclick="toggleSort(this,3)">
                        Team <span class="icon-sort unsorted"></span>
                    </th>
                    <th id="button_sortstudentname-${courseIdx}" class="button-sort-none" onclick="toggleSort(this,4)">
                        Student Name <span class="icon-sort unsorted"></span>
                    </th>
                    <th id="button_sortemail-${courseIdx}" class="button-sort-none" onclick="toggleSort(this,5)">
                        Email <span class="icon-sort unsorted"></span>
                    </th>
                    <th>Action(s)</th>
                </tr>
            </thead>
            
            <tbody>
                <c:forEach items="${studentTable.studentRows}" var="studentRow" varStatus="i">
                    <search:searchStudentsRow studentIdx="${i.index}" student="${studentRow}" courseIdx="${courseIdx}" />
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>