<%@ tag description="feedbackSessionDetailsPanel.tag - Displays a row of information about a feedback session" %>
<%@ attribute name="label" required="true" %>

<div class="form-group">
    <label class="col-sm-2 control-label">
        ${label}
    </label>
    <div class="col-sm-10">
        <p class="form-control-static">
            <jsp:doBody/>
        </p>
    </div>
</div>