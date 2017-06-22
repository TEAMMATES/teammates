<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="jsIncludes">
  <script type="text/javascript" src="/js/libs-maps.js"></script>
  <script type="text/javascript" src="/js/userMap.js"></script>
</c:set>
<c:set var="geoDataUrl" value="/js/coordinates.json" />
<t:staticPage jsIncludes="${jsIncludes}">
  <main>
    <input type="hidden" id="geo-data-url" value="${geoDataUrl}">
    <h1 class="color_orange">Who is using TEAMMATES?</h1>
    <div id="world-map"></div>
    <p class="text-right">Last updated: <span id="lastUpdate" ></span></p>
    <h2 class="text-center color_blue">
      <span id="totalUserCount" class="color_orange"></span>
      institutions from
      <span id="totalCountryCount" class="color_orange"></span>
      countries
    </h2>
  </main>
</t:staticPage>
