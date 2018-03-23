<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags/shared" prefix="shared"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:choose>
  <c:when test="${fn:length(data.commentIds) eq 5}">
    <shared:feedbackResponseCommentRow frc="${data.comment}"
        firstIndex="${data.commentIds[3]}"
        secondIndex="${data.commentIds[1]}"
        thirdIndex="${data.commentIds[2]}"
        fourthIndex="${data.commentIds[0]}"
        frcIndex="${data.commentIds[4]}" />
  </c:when>
  <c:otherwise>
    <shared:feedbackResponseCommentRow frc="${data.comment}"
        firstIndex="${data.commentIds[0]}"
        secondIndex="${data.commentIds[1]}"
        thirdIndex="${data.commentIds[2]}"
        frcIndex="${data.commentIds[3]}" />
  </c:otherwise>
</c:choose>
