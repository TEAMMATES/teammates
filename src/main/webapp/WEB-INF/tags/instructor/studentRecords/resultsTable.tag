<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorStudentRecords - Ajax result (feedback responses and FRCs)" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags/shared" prefix="shared" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="responses" type="java.util.Collection" required="true" %>
<%@ attribute name="studentName" required="true" %>
<%@ attribute name="fbIndex" required="true" %>
<%@ attribute name="panelHeadingToOrFrom" required="true" %>
<%@ attribute name="panelEntryToOrFrom" required="true" %>
<%@ attribute name="viewType" required="true" %>
<%@ attribute name="forOrBy" required="true" %>
<br>
<c:choose>
  <c:when test="${not empty responses}">
    <div class="panel panel-primary">
      <div class="panel-heading">
        ${panelHeadingToOrFrom}: <strong>${fn:escapeXml(studentName)}</strong>
      </div>
      <div class="panel-body">
        <c:forEach items="${responses}" var="responsesByPerson" varStatus="personIndex">
          <div class="row<c:if test="${personIndex.index != 0}"> border-top-gray</c:if>">
            <div class="col-md-2">
              ${panelEntryToOrFrom}: <strong>${fn:escapeXml(responsesByPerson.personName)}</strong>
            </div>
            <div class="col-md-10">
              <c:forEach items="${responsesByPerson.responses}" var="response" varStatus="qnIndex">
                <div class="panel panel-info">
                  <div class="panel-heading">
                    Question ${response.questionNumber}: <span class="text-preserve-space">${response.questionText}${response.questionMoreInfo}</span>
                  </div>
                  <div class="panel-body">
                    <div style="clear:both; overflow: hidden">
                      <%-- Note: When an element has class text-preserve-space, do not insert any HTML spaces --%>
                      <div class="pull-left text-preserve-space">${response.responseText}</div>
                    </div>
                    <c:if test="${not empty response.feedbackParticipantComment}">
                      <br>
                      <shared:feedbackResponseCommentRowForFeedbackParticipant frc="${response.feedbackParticipantComment}"/>
                    </c:if>
                    <c:if test="${not empty response.instructorComments}">
                      <ul class="list-group" id="responseCommentTable-${fbIndex}-${personIndex.index + 1}-${qnIndex.index + 1}-${viewType}" style="margin-top:15px;">
                        <c:forEach items="${response.instructorComments}" var="responseComment" varStatus="status">
                          <c:if test="${forOrBy == 'by'}">
                            <shared:feedbackResponseCommentRow frc="${responseComment}" firstIndex="${personIndex.index + 1}"
                                secondIndex="1" thirdIndex="${qnIndex.index + 1}" frcIndex="${status.count}" viewType="GRQ"/>
                          </c:if>
                          <c:if test="${forOrBy == 'for'}">
                            <shared:feedbackResponseCommentRow frc="${responseComment}" firstIndex="1"
                                secondIndex="${personIndex.index + 1}" thirdIndex="${qnIndex.index + 1}" frcIndex="${status.count}" viewType="RGQ"/>
                          </c:if>
                        </c:forEach>
                      </ul>
                    </c:if>
                  </div>
                </div>
              </c:forEach>
              <c:if test="${empty responsesByPerson.responses}">
                <div class="col-sm-12" style="color: red;">
                  No feedback from this user.
                </div>
              </c:if>
            </div>
          </div>
          <br>
        </c:forEach>
      </div>
    </div>
  </c:when>
  <c:otherwise>
    <div class="panel panel-info">
      <div class="panel-body">
        No feedback ${forOrBy}${" "}${fn:escapeXml(studentName)} found
      </div>
    </div>
  </c:otherwise>
</c:choose>
