<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
  <head>
    <title>QUnit Testing Result</title>
    <meta charset="utf-8">
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

    <!-- Library scripts -->
    <script type="text/javascript" src="/js/libs-common.js"></script>
    <script type="text/javascript" src="/js/libs-tinymce.js"></script>
    <script type="text/javascript" src="/test/libs-qunit.js"></script>
    <script type="text/javascript" src="/test/libs-blanket.js"></script>

    <script type="text/javascript" src="/test/jsUnitTests.js" data-cover></script>
  </body>
</html>
