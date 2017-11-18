<%@ tag description="instructorCourseDetails - Student Table Modal" %>

<div class="modal fade" id="studentTableWindow">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
            <span aria-hidden="true">&times;</span>
        </button>
        <span class="help-block">
          Tips: After Selecting the table, <kbd>Ctrl + C</kbd> or <kbd>⌘ + C</kbd> to COPY and
          <kbd>Ctrl + V</kbd> or <kbd>⌘ + V</kbd> to PASTE to your Excel Workbook.
        </span>

        <button type="button" class="btn btn-primary" id="btn-select-element-contents">
          Select Table
        </button>
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
