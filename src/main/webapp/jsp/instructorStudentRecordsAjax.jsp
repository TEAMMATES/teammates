<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/studentRecords" prefix="tisr" %>
<c:forEach items="${data.resultsTables}" var="resultsTable" varStatus="fbIndex">
  <tisr:resultsTable responses="${resultsTable.receivedResponses}" studentName="${resultsTable.studentName}" fbIndex="${fbIndex.index}"
      panelHeadingToOrFrom="To" panelEntryToOrFrom="From" viewType="GRQ" forOrBy="for" />
  <tisr:resultsTable responses="${resultsTable.givenResponses}" studentName="${resultsTable.studentName}" fbIndex="${fbIndex.index}"
      panelHeadingToOrFrom="From" panelEntryToOrFrom="To" viewType="RGQ" forOrBy="by" />
</c:forEach>
