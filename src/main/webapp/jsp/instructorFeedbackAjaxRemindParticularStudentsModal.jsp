<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="teammates.common.util.Const"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<table class="table-responsive table table-striped table-bordered">
    <tr>
        <th></th>
        <th onclick="toggleSort(this);" class="button-sortsection button-sort-none">
            Section
            <span class="icon-sort unsorted"></span>
        </th>
        <th onclick="toggleSort(this);" class="button-sortteam button-sort-none">
            Team
            <span class="icon-sort unsorted"></span>
        </th>
        <th onclick="toggleSort(this);" class="button-sortname button-sort-none">
            Student Name
            <span class="icon-sort unsorted"></span>
        </th>
        <th onclick="toggleSort(this);" class="button-sortemail button-sort-none">
            Email
            <span class="icon-sort unsorted"></span>
        </th>
    </tr>
    <c:forEach items="${data.responseStatus.noResponse}" var="userToRemindEmail">
        <tr>
            <td class="align-center">
                <div class="checkbox">
                    <label>
                        <input type="checkbox" class="table-column-no-float" name="<%= Const.ParamsNames.SUBMISSION_REMIND_USERLIST %>" value="${userToRemindEmail}">
                    </label>
                </div>
            </td>
            <td>
                ${data.responseStatus.emailSectionTable[userToRemindEmail]}
            </td>
            <td>
                ${data.responseStatus.emailTeamNameTable[userToRemindEmail]}
            </td>
            <td>
                ${data.responseStatus.emailNameTable[userToRemindEmail]}
            </td>
            <td>
                ${userToRemindEmail}
            </td>
        </tr>
    </c:forEach>
</table>

<input type="hidden" name="<%= Const.ParamsNames.COURSE_ID %>" value="${data.courseId}">
<input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="${data.fsName}">
