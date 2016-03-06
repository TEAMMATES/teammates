<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/admin" prefix="ta" %>
<%@ taglib tagdir="/WEB-INF/tags/admin/home" prefix="adminHome" %>

<c:set var="jsIncludes">
    <script type="text/javascript" src="/js/administrator.js"></script>
    <script type="text/javascript" src="/js/adminHome.js"></script>
</c:set>

<ta:adminPage bodyTitle="Add New Instructor" pageTitle="TEAMMATES - Administrator" jsIncludes="${jsIncludes}">
    <adminHome:adminCreateInstructorAccountWithOneBoxForm instructorDetailsSingleLine="${data.instructorDetailsSingleLine}"/>
    <adminHome:adminCreateInstructorAccountForm instructorShortName="${data.instructorShortName}" 
        instructorName="${data.instructorName}" instructorEmail="${data.instructorEmail}" instructorInstitution="${data.instructorInstitution}"/>

    <div class="panel panel-primary" hidden="hidden" id="addInstructorResultPanel">
        <div class="panel-heading">
            <strong>Result</strong>
        </div>
        <div class="table-responsive">
            <table class="table table-striped table-hover" id="addInstructorResultTable">
                <thead>
                    <tr>
                        <th>Short Name</th>
                        <th>Name</th>
                        <th>Email</th>
                        <th>Institution</th>
                        <th>Status</th>
                        <th>Message</th>
                    </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
        </div>
    </div>
    <t:statusMessage/>
</ta:adminPage>
