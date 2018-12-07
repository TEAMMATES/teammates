<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorCourseDetails - Student Table Modal" pageEncoding="UTF-8" %>

<div class="modal fade" id="studentTableWindow">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
            <span aria-hidden="true">&times;</span>
        </button>
        <span class="help-block">
          Tips: <span class="btn-link" id="btn-select-element-contents">Click here</span> to select the table,
          then <kbd>Ctrl + C</kbd> or <kbd>⌘ + C</kbd> to COPY and <kbd>Ctrl + V</kbd> or <kbd>⌘ + V</kbd> to PASTE to your Excel Workbook.
        </span>

      </div>

      <div class="modal-body">
        <div class="table-responsive">
          <div id="detailsTable"></div>
          <br>

          <div id="ajaxStatus"></div>
        </div>
      </div>
    </div>
  </div>
</div>
