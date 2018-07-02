<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page import="teammates.common.util.Const"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<table class="table-responsive table table-bordered">
  <tr class="background-color-medium-gray">
    <th></th>
    <th class="button_sortsection button-sort-none toggle-sort">
      Section
      <span class="icon-sort unsorted"></span>
    </th>
    <th class="button_sortteam button-sort-none toggle-sort">
      Team
      <span class="icon-sort unsorted"></span>
    </th>
    <th class="button_sortname button-sort-none toggle-sort">
      Student Name
      <span class="icon-sort unsorted"></span>
    </th>
    <th class="button_sortemail button-sort-none toggle-sort">
      Email
      <span class="icon-sort unsorted"></span>
    </th>
    <th class="button_sortemail button-sort-none toggle-sort">
      Submitted?
      <span class="icon-sort unsorted"></span>
    </th>
  </tr>
  <c:forEach items="${data.responseStatus.studentsWhoDidNotRespondSorted}" var="userToRemindEmail">
    <tr class="bg-danger">
      <td class="align-center">
        <div class="checkbox">
          <label>
            <input type="checkbox" class="student-not-responded table-column-no-float" name="<%= Const.ParamsNames.SUBMISSION_REMIND_USERLIST %>" value="${userToRemindEmail}">
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
      <td>
        No
      </td>
    </tr>
  </c:forEach>
  <c:forEach items="${data.responseStatus.studentsWhoRespondedSorted}" var="userToRemindEmail">
    <tr class="bg-info">
      <td class="align-center">
        <div class="checkbox">
          <label>
            <input type="checkbox" class="student-responded table-column-no-float" name="<%= Const.ParamsNames.SUBMISSION_REMIND_USERLIST %>" value="${userToRemindEmail}">
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
      <td>
        Yes
      </td>
    </tr>
  </c:forEach>
</table>

<input type="hidden" name="<%= Const.ParamsNames.COURSE_ID %>" value="${data.courseId}">
<input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="${data.fsName}">
