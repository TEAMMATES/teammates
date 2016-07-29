<%@ taglib tagdir="/WEB-INF/tags/static" prefix="ts" %>
<%@ tag description="Individual table entry for team leads" %>
<%@ attribute name="imgSuffix" required="true" %>
<%@ attribute name="desc" required="true" %>
<ts:currentMember imgSuffix="${imgSuffix}" desc="${desc}" photoWidth="180" />
