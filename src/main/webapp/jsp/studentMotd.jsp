<%--
    - @(#)
    - Description: This jsp file is for message of the day on student pages
 --%>

<%@ page import="teammates.common.util.Const" %>
<%@ page import="teammates.common.util.Config" %>
<%@ page import="teammates.ui.controller.PageData" %>
<%
    PageData data = (PageData)request.getAttribute("data");
    String motdUrl = Config.inst().STUDENT_MOTD_URL;
%>

<% if (motdUrl != null && !motdUrl.isEmpty()) { %>
    <script>
        var url = window.location.origin + "/" + "<%= motdUrl%>";
        $.ajax({
            type: "GET",
            url: url,
            success: function(data){
                $("#student-motd").html(data);
            },
            error: function(jqXHR, textStatus, errorThrown){
                console.log('AJAX request failed');
            }
        });
        function closeMotd() {
            $("#student-motd-container").hide();
        }
    </script>
    <div class="container theme-showcase" id="student-motd-container">
        <div class="row">
            <div class="col-sm-12">
                <div class="panel panel-default">
                    <div class="panel-body padding-top-0">
                        <div class="row">
                            <div "col-sm-12">
                                <p class="padding-15px margin-0">
                                    <b class="text-color-gray">TEAMMATES Message of the day</b>
                                    &nbsp;
                                    <button type="button" class="close" aria-label="Close" onclick="closeMotd();">
                                        <span aria-hidden="true">&times;</span>
                                    </button>
                                </p>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-sm-12" id="student-motd">
                                <!-- Message of the day loads here -->
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
<% } %>
