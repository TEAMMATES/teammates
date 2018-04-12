<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page import="teammates.common.util.FrontEndLibrary" %>
<!DOCTYPE html>
<html>
  <head>
    <title>QUnit Testing Result</title>
    <meta charset="utf-8">
    <link rel="stylesheet" href="<%= FrontEndLibrary.BOOTSTRAP_CSS %>" type="text/css" media="screen">
    <link rel="stylesheet" href="<%= FrontEndLibrary.BOOTSTRAP_THEME_CSS %>" type="text/css" media="screen">
    <link rel="stylesheet" href="<%= FrontEndLibrary.QUNIT_CSS %>" type="text/css" media="screen">
  </head>
  <body>
    <div id="qunit"></div>
    <div id="qunit-fixture"></div>
    <div id="blanket-main"></div>
    <hr><hr><hr>
    <h3>Elements required for Testing</h3>
    <span id= "submissionsNumber" class="submissionsNumber"></span>
    Any HTML elements required for the above tests are located here. <br><br>

    <input id="team_all" type="checkbox" checked="">
    <button id="test-bootbox-button"></button>
    <div id="test-bootbox-modal-stub"></div>
    <div id="visible">Visible</div>
    <input type="text" id="date-picker-div">

    <div class="panel-body rubricStatistics">
      <div class="col-sm-4 text-right ">
        <input class="excluding-self-response-checkbox" onclick="toggleExcludingSelfResultsForRubricStatistics(this)" type="checkbox">
        <span class="text-nowrap tool-tip-decorate">Exclude self evaluation</span>
      </div>
      <div class="col-sm-12 table-responsive">
        <table class="table table-striped table-bordered margin-0">
          <thead></thead>
          <tbody class="table-body-including-self"></tbody>
          <tbody class="table-body-excluding-self hidden"></tbody>
        </table>
      </div>
    </div>

    <!-- Library scripts -->
    <script type="text/javascript" src="<%= FrontEndLibrary.JQUERY %>"></script>
    <script type="text/javascript" src="<%= FrontEndLibrary.JQUERY_UI %>"></script>
    <script type="text/javascript" src="<%= FrontEndLibrary.BOOTSTRAP %>"></script>
    <script type="text/javascript" src="<%= FrontEndLibrary.BOOTBOX %>"></script>
    <script type="text/javascript" src="<%= FrontEndLibrary.TINYMCE %>"></script>
    <script type="text/javascript" src="<%= FrontEndLibrary.QUNIT %>"></script>
    <script type="text/javascript" src="<%= FrontEndLibrary.BLANKET %>"></script>

    <script type="text/javascript" src="/test/jsUnitTests.js" data-cover></script>
  </body>
</html>
