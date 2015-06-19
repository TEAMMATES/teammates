<%@ tag description="instructorFeedbackResults - Question - response row" %>
<%@ tag import="teammates.common.util.Const" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/results" prefix="results" %>

<%@ attribute name="responseRow" type="teammates.ui.template.InstructorResultsResponseRow" required="true" %>

<tr class="pending_response_row">
    <td class="middlealign color_neutral">
    <c:choose>
        <c:when test="${responseRow.giverProfilePictureDisplayed}">
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
    <td class="middlealign color_neutral">${responseRow.giverTeam}></td>
    <td class="middlealign color_neutral">
    <c:choose>
        <c:when test="${responseRow.recipientProfilePictureDisplayed}">
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
    <td class="middlealign color_neutral">${responseRow.recipientTeam}</td>
    <td>
        <c:if test="${responseRow.moderationsButtonDisplayed}">
            <results:moderationsButton moderationButton="${responseRow.moderationButton}" />
        </c:if>
    </td>

</tr>