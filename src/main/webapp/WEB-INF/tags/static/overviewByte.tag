<%@ tag description="Individual overview byte portion for the Index page" %>
<%@ attribute name="caption" required="true" %>
<%@ attribute name="imgSuffix" required="true" %>
<%@ attribute name="desc" required="true" %>
<div class="overviewByte">
    <div class="overviewBytePicture">
        <img alt="TEAMMATES - ${caption}" src="images/overview_${imgSuffix}.png" width="175px">
    </div>
    <div class="overviewByteData">
        <h2 class="overviewByteHeading">${caption}:</h2>
        ${desc}
    </div>
    <div style="clear: both;"></div>
</div>
