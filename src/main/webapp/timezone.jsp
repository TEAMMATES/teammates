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
                    <% for (String timeZone: DateTimeZone.getAvailableIDs()) { %>
                        <%= timeZone %><br>
                    <% } %>
                </td>
                <td id="momentjs"></td>
            </tr>
        </table>
        <script type="text/javascript" src="/js/lib/jquery.min.js"></script>
        <script type="text/javascript" src="/js/lib/moment.min.js"></script>
        <script type="text/javascript" src="/js/lib/moment-timezone-with-data-2010-2020.min.js"></script>
        <script>
            moment.tz.names().forEach(function(timeZone) {
                $('#momentjs').append(timeZone + '<br>');
            });
        </script>
    </body>
</html>