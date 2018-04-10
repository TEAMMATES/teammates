<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page import="teammates.common.util.FrontEndLibrary" %>
<%@ page import="java.time.ZoneId" %>
<%@ page import="java.time.Instant" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.time.zone.ZoneRulesProvider" %>
<!DOCTYPE html>
<html>
  <head>
    <title>Timezone Compilation - TEAMMATES</title>
  </head>
  <body>
    <table>
      <tr>
        <td id="javatime">
          <%= ZoneRulesProvider.getVersions("UTC").firstKey() %><br>
          <%
          Instant now = Instant.now();
          ArrayList<String> zoneIds = new ArrayList<>(ZoneId.getAvailableZoneIds());
          Collections.sort(zoneIds);
          for (String timeZone: zoneIds) {
            if (!timeZone.contains("SystemV")) {
              int offset = ZoneId.of(timeZone).getRules().getOffset(now).getTotalSeconds() / 60; %>
              <%= timeZone %><%= " " %><%= offset %><br>
          <% } %>
          <% } %>
        </td>
        <td id="momentjs"></td>
      </tr>
    </table>
    <script type="text/javascript" src="<%= FrontEndLibrary.MOMENT %>"></script>
    <script type="text/javascript" src="/data/moment-timezone-with-data-2013-2023.min.js"></script>
    <script>
      function isSupportedByJava(name) {
          // These short timezones are not supported by Java
          const badZones = {
              EST: true, 'GMT+0': true, 'GMT-0': true, HST: true, MST: true, ROC: true,
          };
          return !badZones[name];
      }
      var d = new Date();
      var text = moment.tz.dataVersion + '<br>';
      moment.tz.names().filter(isSupportedByJava).forEach(function(timeZone) {
        var offset = moment.tz.zone(timeZone).utcOffset(d) * -1;
        text += timeZone + ' ' + offset + '<br>';
      });
      document.getElementById('momentjs').innerHTML = text;
    </script>
  </body>
</html>
