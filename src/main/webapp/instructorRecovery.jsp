<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<t:recoveryPage>
  <h1>Recycle Bin</h1>
  <br>
  <br>
  <h2>Deleted Courses</h2>
  <table class="table table-bordered table-striped" id="tableDeletedCourses">
    <thead>
      <tr class="fill-info">
        <th class="button-sort-none toggle-sort" id="button_sortid1">
          Course ID
          <span class="icon-sort unsorted">
          </span>
        </th>
        <th class="button-sort-none toggle-sort" id="button_sortid2">
          Course Name
          <span class="icon-sort unsorted">
          </span>
        </th>
        <th class="button-sort-none toggle-sort" data-toggle-sort-comparator="sortDate" data-toggle-sort-extractor="dateStampExtractor" id="button_sortcoursecreateddate">
          Creation Date
          <span class="icon-sort unsorted">
          </span>
        </th>
        <th class="button-sort-none toggle-sort" data-toggle-sort-comparator="sortDate" data-toggle-sort-extractor="dateStampExtractor" id="button_sortcoursedeleteddate">
          Deletion Date
          <span class="icon-sort unsorted">
          </span>
        </th>
        <th class="align-center no-print">
          Action(s)
        </th>
      </tr>
    </thead>
    <tbody>
      <tr>
        <td id="courseid1">
          InstructorName1-CS1101S
        </td>
        <td id="coursename1">
          Programming Methodology
        </td>
        <td data-date-stamp="${datetime.now.iso8601utc}" data-original-title="${datetime.now}" data-toggle="tooltip" id="coursecreateddate1">
          26 Jan 2018
        </td>
        <td data-date-stamp="${datetime.now.iso8601utc}" data-original-title="${datetime.now}" data-toggle="tooltip" id="coursedeleteddate1">
          13 May 2018
        </td>
        <td class="align-center no-print">
          <a class="btn btn-default btn-xs">
            Restore
          </a>
          <a class="btn btn-default btn-xs course-delete-link" data-course-id="InstructorName1-CS1101S" data-original-title="Delete the course and its corresponding sessions" data-placement="top" data-toggle="tooltip" style="color: red;">
            Delete Permanently
          </a>
        </td>
      </tr>
    </tbody>
    <tbody>
      <tr>
        <td id="courseid2">
          InstructorName1-CS1020
        </td>
        <td id="coursename2">
          Data Structures and Algorithms I
        </td>
        <td data-date-stamp="${datetime.now.iso8601utc}" data-original-title="${datetime.now}" data-toggle="tooltip" id="coursecreateddate2">
          28 Jan 2018
        </td>
        <td data-date-stamp="${datetime.now.iso8601utc}" data-original-title="${datetime.now}" data-toggle="tooltip" id="coursedeleteddate2">
          12 May 2018
        </td>
        <td class="align-center no-print">
          <a class="btn btn-default btn-xs">
            Restore
          </a>
          <a class="btn btn-default btn-xs course-delete-link" data-course-id="InstructorName1-CS1020" data-original-title="Delete the course and its corresponding sessions" data-placement="top" data-toggle="tooltip" style="color: red">
            Delete Permanently
          </a>
        </td>
      </tr>
    </tbody>
    <tbody>
      <tr>
        <td id="courseid3">
          InstructorName2-CS2010
        </td>
        <td id="coursename3">
          Data Structures and Algorithms II
        </td>
        <td data-date-stamp="${datetime.now.iso8601utc}" data-original-title="${datetime.now}" data-toggle="tooltip" id="coursecreateddate3">
          27 Jan 2018
        </td>
        <td data-date-stamp="${datetime.now.iso8601utc}" data-original-title="${datetime.now}" data-toggle="tooltip" id="coursedeleteddate3">
          12 May 2018
        </td>
        <td class="align-center no-print">
          <a class="btn btn-default btn-xs">
            Restore
          </a>
          <a class="btn btn-default btn-xs course-delete-link" data-course-id="InstructorName2-CS2010" data-original-title="Delete the course and its corresponding sessions" data-placement="top" data-toggle="tooltip" style="color: red">
            Delete Permanently
          </a>
        </td>
      </tr>
    </tbody>
    <tbody>
      <tr>
        <td id="courseid4">
          InstructorName3-CS2103T
        </td>
        <td id="coursename4">
          Software Engineering
        </td>
        <td data-date-stamp="${datetime.now.iso8601utc}" data-original-title="${datetime.now}" data-toggle="tooltip" id="coursecreateddate4">
          26 Jan 2018
        </td>
        <td data-date-stamp="${datetime.now.iso8601utc}" data-original-title="${datetime.now}" data-toggle="tooltip" id="coursedeleteddate4">
          10 May 2018
        </td>
        <td class="align-center no-print">
          <a class="btn btn-default btn-xs">
            Restore
          </a>
          <a class="btn btn-default btn-xs course-delete-link" data-course-id="InstructorName3-CS2103T" data-original-title="Delete the course and its corresponding sessions" data-placement="top" data-toggle="tooltip" style="color: red">
            Delete Permanently
          </a>
        </td>
      </tr>
    </tbody>
  </table>
  <div class="row">
    <div class="col-xs-10">
      <div class="pull-right">
        <a class="btn btn-info btn-md" id="restoreAllCourses">Restore All Courses</a>
      </div>
    </div>
    <div class="col-xs-2">
      <div class="pull-right">
        <a class="btn btn-danger btn-md" id="deleteAllCourses">Delete All Courses</a>
      </div>
    </div>
  </div>
  <br>
  <br>
  <div class="separate-content-holder">
    <hr>
  </div>
  <h2>Deleted Sessions</h2>
  <table class="table table-bordered table-striped" id="tableDeletedSessions">
    <thead>
    <tr class="fill-info">
      <th class="button-sort-none toggle-sort" id="button_sortid3">
        Course ID
        <span class="icon-sort unsorted">
          </span>
      </th>
      <th class="button-sort-none toggle-sort" id="button_sortid4">
        Session Name
        <span class="icon-sort unsorted">
          </span>
      </th>
      <th class="button-sort-none toggle-sort" data-toggle-sort-comparator="sortDate" data-toggle-sort-extractor="dateStampExtractor" id="button_sortsessioncreateddate">
        Creation Date
        <span class="icon-sort unsorted">
          </span>
      </th>
      <th class="button-sort-none toggle-sort" data-toggle-sort-comparator="sortDate" data-toggle-sort-extractor="dateStampExtractor" id="button_sortsessiondeleteddate">
        Deletion Date
        <span class="icon-sort unsorted">
          </span>
      </th>
      <th class="align-center no-print">
        Action(s)
      </th>
    </tr>
    </thead>
    <tbody>
    <tr>
      <td id="sessioncourseid1">
        InstructorName5-CS2102
      </td>
      <td id="sessionname1">
        Phase B team project peer evaluation
      </td>
      <td data-date-stamp="${datetime.now.iso8601utc}" data-original-title="${datetime.now}" data-toggle="tooltip" id="sessioncreateddate1">
        10 Apr 2018
      </td>
      <td data-date-stamp="${datetime.now.iso8601utc}" data-original-title="${datetime.now}" data-toggle="tooltip" id="sessiondeleteddate1">
        30 Apr 2018
      </td>
      <td class="align-center no-print">
        <a class="btn btn-default btn-xs">
          Restore
        </a>
        <a class="btn btn-default btn-xs course-delete-link" data-course-id="InstructorName1-CS1101S" data-original-title="Delete the course and its corresponding sessions" data-placement="top" data-toggle="tooltip" style="color: red;">
          Delete Permanently
        </a>
      </td>
    </tr>
    </tbody>
    <tbody>
    <tr>
      <td id="sessioncourseid2">
        InstructorName6-CS2105
      </td>
      <td id="sessionname2">
        Tutorial work feedback session
      </td>
      <td data-date-stamp="${datetime.now.iso8601utc}" data-original-title="${datetime.now}" data-toggle="tooltip" id="sessioncreateddate2">
        20 Apr 2018
      </td>
      <td data-date-stamp="${datetime.now.iso8601utc}" data-original-title="${datetime.now}" data-toggle="tooltip" id="sessiondeleteddate2">
        27 Apr 2018
      </td>
      <td class="align-center no-print">
        <a class="btn btn-default btn-xs">
          Restore
        </a>
        <a class="btn btn-default btn-xs course-delete-link" data-course-id="InstructorName1-CS1020" data-original-title="Delete the course and its corresponding sessions" data-placement="top" data-toggle="tooltip" style="color: red">
          Delete Permanently
        </a>
      </td>
    </tr>
    </tbody>
    <tbody>
    <tr>
      <td id="sessioncourseid3">
        InstructorName7-CS2100
      </td>
      <td id="sessionname3">
        Term assignment feedback session
      </td>
      <td data-date-stamp="${datetime.now.iso8601utc}" data-original-title="${datetime.now}" data-toggle="tooltip" id="sessioncreateddate3">
        21 Mar 2018
      </td>
      <td data-date-stamp="${datetime.now.iso8601utc}" data-original-title="${datetime.now}" data-toggle="tooltip" id="sessiondeleteddate3">
        20 Apr 2018
      </td>
      <td class="align-center no-print">
        <a class="btn btn-default btn-xs">
          Restore
        </a>
        <a class="btn btn-default btn-xs course-delete-link" data-course-id="InstructorName2-CS2010" data-original-title="Delete the course and its corresponding sessions" data-placement="top" data-toggle="tooltip" style="color: red">
          Delete Permanently
        </a>
      </td>
    </tr>
    </tbody>
    <tbody>
    <tr>
      <td id="sessioncourseid4">
        InstructorName5-CS2102
      </td>
      <td id="sessionname4">
        Phase A team project peer evaluation
      </td>
      <td data-date-stamp="${datetime.now.iso8601utc}" data-original-title="${datetime.now}" data-toggle="tooltip" id="sessioncreateddate4">
        29 Jan 2018
      </td>
      <td data-date-stamp="${datetime.now.iso8601utc}" data-original-title="${datetime.now}" data-toggle="tooltip" id="sessiondeleteddate4">
        12 Mar 2018
      </td>
      <td class="align-center no-print">
        <a class="btn btn-default btn-xs">
          Restore
        </a>
        <a class="btn btn-default btn-xs course-delete-link" data-course-id="InstructorName3-CS2103T" data-original-title="Delete the course and its corresponding sessions" data-placement="top" data-toggle="tooltip" style="color: red">
          Delete Permanently
        </a>
      </td>
    </tr>
    </tbody>
  </table>
  <div class="row">
    <div class="col-xs-10">
      <div class="pull-right">
        <a class="btn btn-info btn-md" id="restoreAllSessions">Restore All Sessions</a>
      </div>
    </div>
    <div class="col-xs-2">
      <div class="pull-right">
        <a class="btn btn-danger btn-md" id="deleteAllSessions">Delete All Sessions</a>
      </div>
    </div>
  </div>
  <br>
  <br>
  <br>
  <br>
</t:recoveryPage>
