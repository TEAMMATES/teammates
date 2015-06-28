<%@ tag description="instructorFeedbackResults - Question - response row" %>
<%@ tag import="teammates.common.util.Const" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/results" prefix="results" %>

<%@ attribute name="responseRow" type="teammates.ui.template.InstructorResultsResponseRow" required="true" %>

<tr <c:forEach items="${responseRow.rowAttributes.attributes}" var="attr">${attr.key}="${attr.value}"</c:forEach>>
    <c:if test="${responseRow.giverDisplayed}">
        <c:if test="${responseRow.giverProfilePictureAColumn}">
            <td class="middlealign<c:if test="${responseRow.rowGrey}"> color_neutral</c:if>">
                <div class="profile-pic-icon-click align-center" data-link="${responseRow.giverProfilePictureLink}">
                    <a class="student-profile-pic-view-link btn-link">
                        View Photo
                    </a>
                    <img src="" alt="No Image Given" class="hidden">
                </div>
            </td>
        </c:if>
        <td class="middlealign<c:if test="${responseRow.rowGrey}"> color_neutral</c:if>">
        <c:choose>
            <c:when test="${responseRow.giverProfilePictureDisplayed && !responseRow.giverProfilePictureAColumn}">
                <div class="profile-pic-icon-hover" data-link="${responseRow.giverProfilePictureLink}">
                    ${responseRow.giverDisplayableIdentifier}
                    <img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                </div>             
            </c:when>
            <c:otherwise>  
                ${responseRow.giverDisplayableIdentifier}
            </c:otherwise>
        </c:choose>   
        </td>
        <td class="middlealign<c:if test="${responseRow.rowGrey}"> color_neutral</c:if>">${responseRow.giverTeam}</td>
    </c:if>
    <c:if test="${responseRow.recipientDisplayed}">
        <c:if test="${responseRow.recipientProfilePictureAColumn}">
            <td class="middlealign<c:if test="${responseRow.rowGrey}"> color_neutral</c:if>">
                <div class="profile-pic-icon-click align-center" data-link="${responseRow.recipientProfilePictureLink}">
                    <a class="student-profile-pic-view-link btn-link">
                        View Photo
                    </a>
                    <img src="" alt="No Image Given" class="hidden">
                </div>
            </td>
        </c:if>
        <td class="middlealign<c:if test="${responseRow.rowGrey}"> color_neutral</c:if>">
        <c:choose>
            <c:when test="${responseRow.recipientProfilePictureDisplayed && !responseRow.recipientProfilePictureAColumn}">
                <div class="profile-pic-icon-hover" data-link="${responseRow.recipientProfilePictureLink}">
                    ${responseRow.recipientDisplayableIdentifier}
                    <img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                </div>             
            </c:when>
            <c:otherwise>  
                ${responseRow.recipientDisplayableIdentifier}
            </c:otherwise>
        </c:choose>   
        </td>
        <td class="middlealign<c:if test="${responseRow.rowGrey}"> color_neutral</c:if>">${responseRow.recipientTeam}</td>
    </c:if>
    <!--Note: When an element has class text-preserve-space, do not insert and HTML spaces-->
    <td class="text-preserve-space<c:if test="${responseRow.rowGrey}"> color_neutral</c:if>">${responseRow.displayableResponse}</td>
    <td>
        <c:if test="${responseRow.moderationsButtonDisplayed}">
            <results:moderationsButton moderationButton="${responseRow.moderationButton}" />
        </c:if>
    </td>

</tr>