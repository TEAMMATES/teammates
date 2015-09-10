<%@ tag description="adminSearch.jsp - instructor results table" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ taglib tagdir="/WEB-INF/tags/admin/search" prefix="adminSearch" %>
<%@ attribute name="instructorResultsTable" type="teammates.ui.template.AdminSearchInstructorTable" required="true" %>

<div class="panel panel-primary">
    <div class="panel-heading">
        <strong>Instructors Found </strong>
        <span class="pull-right">
            <button class="btn btn-primary btn-xs" type="button"
                    onclick="adminSearchDiscloseAllInstructors()">Disclose All</button>
                    
            <button class="btn btn-primary btn-xs" type="button"
                    onclick="adminSearchCollapseAllInstructors()">Collapse All</button>
        </span>
    </div>

    <div class="table-responsive">
        <table class="table table-striped dataTable" id="search_table_instructor">
            <thead>
                <tr>
                    <th>Course</th>
                    <th>Name</th>
                    <th>Google ID</th>
                    <th>Institute</th>
                    <th>Options</th>
                </tr>
            </thead>
                            
            <tbody>
                <c:forEach items="${instructorResultsTable.instructorRows}" var="instructor">
                    <adminSearch:instructorRow instructor="${instructor}"/>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>