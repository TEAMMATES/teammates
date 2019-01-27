<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/home" prefix="home" %>
<home:coursePanel courseTable="${data.courseTable}" index="${data.index}">
  <home:courseTable sessionRows="${data.courseTable.rows}" />
</home:coursePanel>
