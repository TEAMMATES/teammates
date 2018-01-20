<%@ page pageEncoding="UTF-8" %>
<%@ page import="teammates.common.util.FrontEndLibrary" %>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8">
    <title>Table Sorting Test Page - TEAMMATES</title>
    <link type="text/css" href="<%= FrontEndLibrary.BOOTSTRAP_CSS %>" rel="stylesheet">
    <link type="text/css" href="<%= FrontEndLibrary.BOOTSTRAP_THEME_CSS %>" rel="stylesheet">
    <link type="text/css" href="/stylesheets/teammatesCommon.css" rel="stylesheet">
  </head>

  <body>
    <div class = "container">
      <table class="table table-striped">
        <thead>
          <tr>
            <th id="button_sortid" class="button-sort-ascending toggle-sort">
              ID <span class="icon-sort unsorted"></span>
            </th>
            <th id="button_sortname" class="button-sort-none toggle-sort">
              Name <span class="icon-sort unsorted"></span>
            </th>
            <th id="button_sortdate" class="button-sort-none toggle-sort">
              Date <span class="icon-sort unsorted"></span>
            </th>
            <th id="button_sortPoint" data-toggle-sort-comparator="sortByPoints" class="button-sort-none toggle-sort">
              Point <span class="icon-sort unsorted"></span>
            </th>
            <th id="button_sortPointNumber" data-toggle-sort-comparator="sortByPoints" class="button-sort-none toggle-sort">
              Point (Number) <span class="icon-sort unsorted"></span>
            </th>
            <th id="button_sortDiff" data-toggle-sort-comparator="sortByDiff" class="button-sort-none toggle-sort">
              Diff <span class="icon-sort unsorted"></span>
            </th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>0</td>
            <td>Chin Yong Wei</td>
            <td>01 January 2012</td>
            <td>E -99%</td>
            <td>0</td>
            <td>+2%</td>
          </tr>

          <tr>
            <td>1</td>
            <td>Loke Yan Hao</td>
            <td>01 January 2013</td>
            <td>N/A</td>
            <td>0.2</td>
            <td>+1%</td>
          </tr>

          <tr>
            <td>2</td>
            <td>Hou GuoChen</td>
            <td>02 January 2012</td>
            <td>E -21%</td>
            <td>0.333</td>
            <td>+3%</td>
          </tr>

          <tr>
            <td>3</td>
            <td>
              <div id="thiscomesfirst">Phan Thi Quynh Trang</div>
            </td>
            <td>01 February 2012</td>
            <td>N/S</td>
            <td>0.45</td>
            <td>0%</td>
          </tr>

          <tr>
            <td>15</td>
            <td>
              <div id="thiscomessecond">Ang Ji Kai</div>
            </td>
            <td>03 February 2012</td>
            <td>E -10%</td>
            <td>0.9</td>
            <td>+5%</td>
          </tr>

          <tr>
            <td>24</td>
            <td>Le Minh Khue</td>
            <td>12 December 2011</td>
            <td>N/S</td>
            <td>1</td>
            <td>-1%</td>
          </tr>

          <tr>
            <td>33</td>
            <td>Shum Chee How</td>
            <td>25 July 2012</td>
            <td>E -4%</td>
            <td>1.1</td>
            <td>+25%</td>
          </tr>

          <tr>
            <td>-0.001</td>
            <td>Teo Yock Swee Terence</td>
            <td>10 May 2012</td>
            <td>N/S</td>
            <td>1.333</td>
            <td>-2%</td>
          </tr>

          <tr>
            <td>-1.3</td>
            <td>Le Minh Khue</td>
            <td>05 June 2013</td>
            <td>E -2%</td>
            <td>1.45</td>
            <td>+30%</td>
          </tr>

          <tr>
            <td>10.7</td>
            <td>Luk Ming Kit</td>
            <td>17 September 2012</td>
            <td>E +99%</td>
            <td>-0.1</td>
            <td>-10%</td>
          </tr>

          <tr>
            <td>10.3</td>
            <td>Zhang HaoQiang</td>
            <td>04 May 2010</td>
            <td>E 0%</td>
            <td>-0.4</td>
            <td>+99%</td>
          </tr>

          <tr>
            <td>-2</td>
            <td>Le Minh Khue</td>
            <td>14 May 2011</td>
            <td>E +20%</td>
            <td>-0.5</td>
            <td>-20%</td>
          </tr>

          <tr>
            <td>-13.5</td>
            <td>Chong Kok Wei</td>
            <td>05 March 2012</td>
            <td>E 0%</td>
            <td>-1</td>
            <td>N/A</td>
          </tr>

          <tr>
            <td>10.35</td>
            <td>Tan Guo Wei</td>
            <td>21 August 2010</td>
            <td>E +20%</td>
            <td>-1.51</td>
            <td>-99%</td>
          </tr>

          <tr>
            <td >10.01</td>
            <td >Le Minh Khue</td>
            <td >06 April 2011</td>
            <td >E +5%</td>
            <td >-1.667</td>
            <td >N/A</td>
          </tr>
        </tbody>
      </table>
      <br> <br> <br>
    </div>
    <script type="text/javascript" src="<%= FrontEndLibrary.JQUERY %>"></script>
    <script type="text/javascript" src="<%= FrontEndLibrary.BOOTSTRAP %>"></script>
    <script type="text/javascript" src="/test/tableSort.js"></script>
  </body>
</html>
