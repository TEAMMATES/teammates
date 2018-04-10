<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/studentList" prefix="tisl" %>
<c:set var="jsIncludes">
  <script type="text/javascript" src="/js/instructorStudentList.js"></script>
</c:set>
<ti:instructorPage title="Student List" jsIncludes="${jsIncludes}">
  <tisl:searchBox searchBox="${data.searchBox}" />
  <br>
  <h2>Filter Students</h2>
  <tisl:filterBox filterBox="${data.filterBox}" />
  <t:statusMessage statusMessagesToUser="${data.statusMessagesToUser}" />
  <c:forEach items="${data.studentsTable}" var="entry" varStatus="i">
    <tisl:studentsTable course="${entry}" index="${i.index}"/>
  </c:forEach>
</ti:instructorPage>
