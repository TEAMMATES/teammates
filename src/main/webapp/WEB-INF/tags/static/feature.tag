<%@ tag description="Individual feature portion for the Features page" %>
<%@ attribute name="caption" required="true" %>
<%@ attribute name="imgSuffix" required="true" %>
<%@ attribute name="desc" required="true" %>
<div class="feature-details">
    <h2 id="caption">${caption}</h2>
    <img src="images/feature-${imgSuffix}.png" width="500px" alt="${caption}">
    <div class="feature-description">
        ${desc}
    </div>
</div>
<img src="images/raised-edge.png" width="600px">
