<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorFeedbacks - new feedback session form additional settings information modal" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ attribute name="settingId" required="true"%>
<%@ attribute name="settingDescription" required="true"%>
<%@ attribute name="settingOptions" type="java.lang.Object" required="true"%>
<%@ attribute name="settingOptionsDescription" type="java.lang.Object" required="true"%>

<div id="${settingId}" style="display: none;">
  <p>${settingDescription}</p><br>
  <label>Options:</label><br>
  <div>
    <ul>
      <c:forEach items="${settingOptions}" var="option" varStatus="loop">
        <li><p><label>${option}:</label> ${settingOptionsDescription[loop.index]}</p></li>
      </c:forEach>
    </ul>
  </div>
</div>