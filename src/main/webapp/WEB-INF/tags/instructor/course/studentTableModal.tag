<%@ tag description="instructorCourseDetails - Student Table Modal" %>

<div class="modal fade" id="studentTableWindow">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">       
                <span class="pull-left help-block">
                    Tips: After Selecting the table, <kbd>Ctrl + C</kbd> to COPY and <kbd>Ctrl + V</kbd> to PASTE to your Excel Workbook.
                </span>
                
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Close
                </button>
                
                <button type="button" class="btn btn-primary" onclick="selectElementContents( document.getElementById('detailsTable') );">
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