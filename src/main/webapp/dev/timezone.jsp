<%@ page import="java.util.Date" %>
<%@ page import="org.joda.time.DateTimeZone" %>
<!DOCTYPE html>
<html>
    <head>
        <title>TEAMMATES - Timezone Compilation</title>
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
        <script type="text/javascript" src="/js/lib/jquery.min.js"></script>
        <script type="text/javascript" src="/js/lib/moment.min.js"></script>
        <script type="text/javascript" src="/js/lib/moment-timezone-with-data-2010-2020.min.js"></script>
        <script>
            var d = new Date();
            moment.tz.names().forEach(function(timeZone) {
                var offset = moment.tz.zone(timeZone).offset(d) * -1;
                $('#momentjs').append(timeZone + ' ' + offset + '<br>');
            });
        </script>
    </body>
</html>