<%@ page pageEncoding="UTF-8" %>
<%@ page import="java.util.Date" %>
<%@ page import="org.joda.time.DateTimeZone" %>
<%@ page import="teammates.common.util.FrontEndLibrary" %>
<!DOCTYPE html>
<html>
  <head>
    <title>Timezone Compilation - TEAMMATES</title>
  </head>
  <body>
    <table>
      <tr>
        <td id="jodatime">
          <%
          long date = new Date().getTime();
          for (String timeZone: DateTimeZone.getAvailableIDs()) {
            int offset = DateTimeZone.forID(timeZone).getOffset(date) / 60 / 1000; %>
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
