<%--
    - @(#)
    - Description: This jsp file is for banners on student pages
 --%>

<%@ page import="teammates.common.util.Const" %>
<%@ page import="teammates.common.util.Config" %>
<%@ page import="teammates.ui.controller.PageData" %>
<%
    PageData data = (PageData)request.getAttribute("data");
    String bannerUrl = Config.inst().STUDENT_BANNER_URL;
%>

<% if (bannerUrl != null && !bannerUrl.isEmpty()) { %>
    <script>
        var url = window.location.origin + "/" + "<%= bannerUrl%>";
        $.ajax({
            type: "GET",
            url: url,
            success: function(data){
                $("#student-banner").html(data);
            },
            error: function(jqXHR, textStatus, errorThrown){
                console.log('AJAX request failed');
            }
        }); 
    </script>
    <div class="container theme-showcase">
        <div class="row">
            <div class="col-sm-12" id="student-banner">
                <!-- Banner loads here -->
            </div>
        </div>
    </div>
<% } %>
