<%@ tag description="Modal prompt with bootstrap warning color" %>
<%@ taglib tagdir="/WEB-INF/tags/shared/feedbackSubmissionEdit" prefix="feedbackSubmissionEdit" %>

<div class="modal fade" id="warning-modal" tabindex="-1" role="dialog"
     aria-labelledby="warning-modal" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header alert-warning">
                <h4 class="modal-title" id="warning-modal-title"></h4>
            </div>
            <div class="modal-body" id="warning-modal-message">
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" id="warning-modal-ok" data-dismiss="modal"></button>
            </div>
        </div>
    </div>
</div>