<%@ tag description="Confirmation modal with bootstrap warning color" %>

<div class="modal fade" id="confirmation-modal" tabindex="-1" role="dialog" aria-labelledby="confirmation-modal" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header alert-warning">
                <div class="modal-title text-bold" id="confirmation-modal-title"></div>
            </div>
            <div class="modal-body" id="confirmation-modal-body">
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal" id="confirmation-modal-cancel"></button>
                <button type="button" class="btn btn-danger" id="confirmation-modal-ok"></button>
            </div>
        </div>
    </div>
</div>