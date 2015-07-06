<%@ tag description="instructorStudentRecords - Student Profile More Info modal" %>
<%@ attribute name="moreinfo" type="teammates.ui.template.InstructorStudentRecordsMoreInfoModal" required="true" %>
<div class="modal fade" id="studentProfileMoreInfo" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title">${moreinfo.studentName}'s Profile - More Info</h4>
            </div>
            <div class="modal-body">
                <br>
                <!--Note: When an element has class text-preserve-space, do not insert and HTML spaces-->
                <p class="text-preserve-space height-fixed-md">${moreinfo.moreInfo}</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>