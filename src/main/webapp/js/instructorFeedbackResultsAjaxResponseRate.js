function getAppendedResponseRateData(data){
    var appendHtml = '';
    
    if(data.responseStatus.noResponse.length == 0){
        appendHtml += '<div class="panel-body">';
        appendHtml += 'All students have responsed to some questions in this session.';
        appendHtml += '</div>';
    } else {
        appendHtml += '<div class="panel-body padding-0">';
        appendHtml += '<table class="table table-striped table-bordered margin-0">';
        appendHtml += '<tbody>';
        for(var i = 0; i < data.responseStatus.noResponse.length; i++){
            appendHtml += '<tr><td>' + data.responseStatus.noResponse[i] + '</td></tr>';
        }
        appendHtml += '</tbody></table>';
        appendHtml += '</div>';
    }
   
    return appendHtml;
}

$(document).ready(function(){
    var responseRateRequest = function(e) {
        var panelHeading = $(this);
        var displayIcon = $(this).children('.display-icon');
        var formObject = $(this).children("form");
        var panelCollapse = $(this).parent().children('.panel-collapse');
        var formData = formObject.serialize();
        e.preventDefault();
        $.ajax({
            type : 'POST',
            url : 	$(formObject[0]).attr('action') + "?" + formData,
            beforeSend : function() {
                displayIcon.html("<img height='25' width='25' src='/images/ajax-preload.gif'/>")
                //submitButton.html("<img src='/images/ajax-loader.gif'/>");
            },
            error : function() {
                console.log('Error');
            },
            success : function(data) {
                $(panelCollapse[0]).html(getAppendedResponseRateData(data));
                $(panelHeading).removeClass('ajax_response_rate_submit');
                $(panelHeading).off('click');
                displayIcon.html('<span class="glyphicon glyphicon-chevron-down pull-right"></span>')
                $(panelHeading).click(toggleSingleCollapse);
                $(panelHeading).trigger('click');
            }
        });
    };
    $(".ajax_response_rate_submit").click(responseRateRequest);
});