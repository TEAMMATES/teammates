<%@ page import="teammates.common.util.FrontEndLibrary" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="jsIncludes">
    <script type="text/javascript" src="<%= FrontEndLibrary.D3 %>"></script>
    <script type="text/javascript" src="<%= FrontEndLibrary.TOPOJSON %>"></script>
    <script type="text/javascript" src="<%= FrontEndLibrary.DATAMAPS %>"></script>
    <script>
        var geoDataUrl = '<%= FrontEndLibrary.WORLDMAP %>';
    </script>
    <script type="text/javascript" src="/js/countryCodes.js"></script>
    <script type="text/javascript" src="/js/userMap.js"></script>
</c:set>
<t:staticPage jsIncludes="${jsIncludes}">
    <h1 class="caption">Who is using TEAMMATES?</h1>
    <div id="contentHolder">
        <div id="container" style="position: relative; width: 800px; height: 500px; border: 1px solid #DEDEDE;"></div>
        <p class="lastUpdate">Last updated: <span id="lastUpdate" ></span></p>
        <h2 class="subcaption align-center">
            <span id="totalUserCount" class="totalCount"></span> 
            institutions from 
            <span id="totalCountryCount" class="totalCount"></span> 
            countries
        </h2>
    </div>
</t:staticPage>
