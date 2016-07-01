<%@ tag description="Individual table entry for current team members" %>
<%@ attribute name="imgSuffix" required="true" %>
<%@ attribute name="photoWidth" required="true" %>
<%@ attribute name="desc" required="true" %>
<tr>
    <td class="coreTeamPhotoCell">
        <img src="images/teammembers/${imgSuffix}.png" width="${photoWidth}px">
    </td>
    <td class="coreTeamDetailsCell">
        ${desc}
    </td>
</tr>
