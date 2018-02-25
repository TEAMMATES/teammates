<%@ page pageEncoding="UTF-8" %>
<%@ page import="teammates.common.util.FrontEndLibrary" %>
<%@ page import="java.time.ZoneId" %>
<%@ page import="java.time.Instant" %>
<!DOCTYPE html>
<html>
  <head>
    <title>Timezone Compilation - TEAMMATES</title>
  </head>
  <body>
    <table>
      <tr>
        <td id="javatime">
          <%
          Instant now = Instant.now();
          for (String timeZone: ZoneId.getAvailableZoneIds()) {
            int offset = ZoneId.of(timeZone).getRules().getOffset(now).getTotalSeconds() / 60; %>
            <%= timeZone %> <%= offset %><br>
          <% } %>
        </td>
        <td id="momentjs"></td>
      </tr>
    </table>
    <script type="text/javascript" src="<%= FrontEndLibrary.MOMENT %>"></script>
    <script type="text/javascript" src="<%= FrontEndLibrary.MOMENT_TIMEZONE %>"></script>
    <script>
      var d = new Date();
      var text = '';
      moment.tz.names().forEach(function(timeZone) {
        var offset = moment.tz.zone(timeZone).offset(d) * -1;
        text += timeZone + ' ' + offset + '<br>';
      });
      document.getElementById('momentjs').innerHTML = text;
    </script>
  </body>
</html>
