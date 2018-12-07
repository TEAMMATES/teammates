<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/results" prefix="r" %>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.util.TimeHelper"%>
<c:set var="showAll" value="${!data.largeNumberOfResponses}" />
<c:set var="noResponses" value="${empty data.bundle.responses}" />
<c:set var="shouldShowFilterPanelAndExpandCollapseButton" value="${not (noResponses and data.allSectionsSelected and showAll)}" />

<r:sessionPanel sessionPanel="${data.sessionPanel}" />

<c:if test="${shouldShowFilterPanelAndExpandCollapseButton}">
  <r:filterPanel filterPanel="${data.filterPanel}"/>
</c:if>
<br>
<t:statusMessage statusMessagesToUser="${data.statusMessagesToUser}" />
<c:if test="${shouldShowFilterPanelAndExpandCollapseButton}">
  <r:expandCollapseButton sortType="${data.filterPanel.sortType}" showAll="${showAll}" />
</c:if>
<c:if test="${noResponses and showAll}">
  <div class="bold color_red align-center">There are no responses for this feedback session yet or you do not have access to the responses collected so far.</div>
</c:if>
