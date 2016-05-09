<%@ tag description="Modal alert dialog to replace browser alert box" %>
<%@ taglib tagdir="/WEB-INF/tags/shared/feedbackSubmissionEdit" prefix="feedbackSubmissionEdit" %>

<div class="modal fade" id="alertModal" tabindex="-1" role="dialog"
     aria-labelledby="alertModal" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="alert-modal-title"></h4>
            </div>
            <div class="modal-body" id="alert-modal-message">
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" id="alert-modal-ok" data-dismiss="modal"></button>
            </div>
        </div>
    </div>
</div>