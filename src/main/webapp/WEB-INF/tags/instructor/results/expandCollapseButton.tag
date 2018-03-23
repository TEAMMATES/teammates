<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorFeedbackResultsTop - Expand Collapse Button" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="sortType" required="true" %>
<%@ attribute name="showAll" required="true" %>
<div>
  <c:choose>
    <c:when test="${not showAll}">
      <div style="display:inline-block;" class="pull-right" data-toggle="tooltip" title="This button is disabled because this session contains more data than we can retrieve at one go. You can still expand one panel at a time by clicking on the panels below.">
        <a class="btn btn-default btn-xs pull-right disabled" id="collapse-panels-button">
          Expand All ${sortType == 'question' ? 'Questions' : 'Sections'}
        </a>
      </div>
    </c:when>
    <c:otherwise>
      <a class="btn btn-default btn-xs pull-right" id="collapse-panels-button" data-toggle="tooltip" title="Expand all panels. You can also click on the panel heading to toggle each one individually.">
        Expand All ${sortType == 'question' ? 'Questions' : 'Sections'}
      </a>
    </c:otherwise>
  </c:choose>
</div>
<br>
