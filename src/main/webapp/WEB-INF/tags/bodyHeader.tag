<%@ tag description="Body header (top of page)" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="title" required="true" %>
<div id="topOfPage"></div>
<div class="inner-container">
    <div class="row">
        <h1>${title}</h1>
    </div>
</div>