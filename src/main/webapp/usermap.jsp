<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="jsIncludes">
    <script type="text/javascript" src="/js/lib/d3.min.js"></script>
    <script type="text/javascript" src="/js/lib/topojson.min.js"></script>
    <script type="text/javascript" src="/js/lib/datamaps.none.min.js"></script>
    <script type="text/javascript" src="/js/countryCodes.js"></script>
    <script type="text/javascript" src="/js/userMap.js"></script>
</c:set>
<t:staticPage jsIncludes="${jsIncludes}">
    <h1 id="caption">Who is using TEAMMATES?</h1>
    <div id="contentHolder">
        <div id="container" style="position: relative; width: 800px; height: 500px; border: 1px solid #DEDEDE;"></div>
        <p id="lastUpdate" class="lastUpdate">Last updated: 30 March 2016</p>
        <h2 class="subcaption align-center">
            <span id="totalUserCount" class="totalCount"></span> 
            institutions from 
            <span id="totalCountryCount" class="totalCount"></span> 
            countries
        </h2>
    </div>
</t:staticPage>
