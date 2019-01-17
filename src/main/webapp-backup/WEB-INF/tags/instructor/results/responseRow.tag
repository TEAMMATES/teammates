<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorFeedbackResults - Question - response row" pageEncoding="UTF-8" %>
<%@ tag import="teammates.common.util.Const" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/results" prefix="results" %>

<%@ attribute name="responseRow" type="teammates.ui.template.InstructorFeedbackResultsResponseRow" required="true" %>
<%@ attribute name="questionIndex" type="java.lang.Integer"%>
<%@ attribute name="responseIndex" type="java.lang.Integer"%>

<tr <c:forEach items="${responseRow.rowAttributes.attributes}" var="attr">${attr.key}="${attr.value}"</c:forEach>>
  <c:if test="${responseRow.giverDisplayed}">
    <c:if test="${responseRow.giverProfilePictureAColumn}">
      <td class="middlealign">
        <c:choose>
          <c:when test="${not empty responseRow.giverProfilePictureLink}">
            <div class="profile-pic-icon-click align-center" data-link="${responseRow.giverProfilePictureLink}">
              <a class="student-profile-pic-view-link btn-link">
                View Photo
              </a>
              <img src="" alt="No Image Given" class="hidden">
            </div>
          </c:when>
          <c:otherwise>
            <div class="align-center" data-link="">
              <a class="student-profile-pic-view-link btn-link">
                No Photo
              </a>
            </div>
          </c:otherwise>
        </c:choose>
      </td>
    </c:if>
    <td class="word-wrap-break middlealign<c:if test="${responseRow.rowGrey}"> color-neutral</c:if>">${fn:escapeXml(responseRow.giverTeam)}</td>
    <td class="word-wrap-break middlealign<c:if test="${responseRow.rowGrey}"> color-neutral</c:if>">
    <c:choose>
      <c:when test="${not empty responseRow.giverProfilePictureLink && !responseRow.giverProfilePictureAColumn}">
        <div class="profile-pic-icon-hover" data-link="${responseRow.giverProfilePictureLink}">
          ${fn:escapeXml(responseRow.giverDisplayableIdentifier)}
          <img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
        </div>
      </c:when>
      <c:otherwise>
        ${fn:escapeXml(responseRow.giverDisplayableIdentifier)}
      </c:otherwise>
    </c:choose>
    </td>
  </c:if>
  <c:if test="${responseRow.recipientDisplayed}">
    <c:if test="${responseRow.recipientProfilePictureAColumn}">
      <td class="middlealign<c:if test="${responseRow.rowGrey}"> color-neutral</c:if>">
        <c:choose>
          <c:when test="${not empty responseRow.recipientProfilePictureLink}">
            <div class="profile-pic-icon-click align-center" data-link="${responseRow.recipientProfilePictureLink}">
              <a class="student-profile-pic-view-link btn-link">
                View Photo
              </a>
              <img src="" alt="No Image Given" class="hidden">
            </div>
          </c:when>
          <c:otherwise>
            <div class="align-center" data-link="">
              <a class="student-profile-pic-view-link btn-link">
                No Photo
              </a>
            </div>
          </c:otherwise>
        </c:choose>
      </td>
    </c:if>
    <td class="word-wrap-break middlealign<c:if test="${responseRow.rowGrey}"> color-neutral</c:if>">${fn:escapeXml(responseRow.recipientTeam)}</td>
    <td class="word-wrap-break middlealign<c:if test="${responseRow.rowGrey}"> color-neutral</c:if>">
      <c:choose>
        <c:when test="${not empty responseRow.recipientProfilePictureLink && !responseRow.recipientProfilePictureAColumn}">
          <div class="profile-pic-icon-hover" data-link="${responseRow.recipientProfilePictureLink}">
            ${fn:escapeXml(responseRow.recipientDisplayableIdentifier)}
            <img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
          </div>
        </c:when>
        <c:otherwise>
          ${fn:escapeXml(responseRow.recipientDisplayableIdentifier)}
        </c:otherwise>
      </c:choose>
    </td>
  </c:if>
  <!--Note: When an element has class text-preserve-space, do not insert and HTML spaces-->
  <td class="word-wrap-break text-preserve-space<c:if test="${responseRow.rowGrey}"> color-neutral</c:if>">${responseRow.displayableResponse}</td>
  <c:if test="${responseRow.feedbackParticipantCommentsOnResponsesAllowed}">
    <td class="word-wrap-break<c:if test="${responseRow.rowGrey}"> color-neutral</c:if>">${responseRow.feedbackParticipantComment}</td>
  </c:if>
  <c:if test="${responseRow.actionsDisplayed}">
    <td>
      <c:if test="${not empty responseRow.moderationButton}">
        <results:moderationButton moderationButton="${responseRow.moderationButton}" />
      </c:if>
      <c:if test="${not responseRow.rowGrey && responseRow.instructorCommentsOnResponsesAllowed}">
        <button type="button" class="btn btn-default btn-xs comment-button" style="margin-top:0.5em;"
            data-toggle="modal" data-target="#commentModal-${responseRow.responseRecipientIndex}-${responseRow.responseGiverIndex}-${questionIndex}"
            data-recipientindex="${responseRow.responseRecipientIndex}" data-giverindex="${responseRow.responseGiverIndex}"
            data-qnindex="${questionIndex}">
          Add Comment
        </button>
        <results:commentModal responseRow="${responseRow}" responseRecipientIndex="${responseRow.responseRecipientIndex}" responseGiverIndex="${responseRow.responseGiverIndex}"
            questionIndex="${questionIndex}" />
      </c:if>
    </td>
  </c:if>
</tr>
