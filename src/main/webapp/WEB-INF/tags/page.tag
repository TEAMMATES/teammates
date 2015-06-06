<%@ tag description="Generic TEAMMATES Page" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ attribute name="pageTitle" %>
<%@ attribute name="headerJs" fragment="true" %>
<%@ attribute name="navBar" fragment="true" %>
<%@ attribute name="bodyTitle" %>
<!DOCTYPE html>
<html>
<head>
    <title>${pageTitle}</title>
    <t:includes />
    <jsp:invoke fragment="headerJs" />
    <t:enableJS />
</head>
<body>
    <jsp:invoke fragment="navBar" />
    <div id="frameBodyWrapper" class="container theme-showcase">
        <t:header title="${bodyTitle}" />
        <jsp:doBody />
    </div>
    <t:footer version="tempVal"/>
</body>
</html>