<%@ tag description="instructorStudentList - Ajax result" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="courseId" required="true" %>
<%@ attribute name="courseIndex" required="true" %>
<%@ attribute name="hasSection" required="true" %>
<%@ attribute name="sections" required="true" %>
<table class="table table-responsive table-striped table-bordered margin-0">
    <c:choose>
        <c:when test="${not empty sections}">
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