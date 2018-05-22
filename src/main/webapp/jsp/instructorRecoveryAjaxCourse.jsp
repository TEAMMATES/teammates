<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/recovery" prefix="recovery" %>
<recovery:coursePanel courseTable="${data.courseTable}" index="${data.index}">
  <recovery:courseTable sessionRows="${data.courseTable.rows}" />
</recovery:coursePanel>
