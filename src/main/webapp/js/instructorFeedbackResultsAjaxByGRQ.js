function getAppendedData(data){
    var appendedHtml = '';
    for(var giver in data.responses){
        if(data.responses.hasOwnProperty(giver)){
            appendedHtml += getResponsesFromGiver(giver, data);
        }
    }
}

function getResponsesFromGiver(giver, data){    
    var appendedResponses = '';
    appendedResponses += '<div class="panel panel-primary">';
    appendedResponses += '<div class="panel-heading">';
    appendedResponses += 'From: <strong>' + giver + '</strong>'
    appendedResponses +=
    appendedResponses +=
    appendedResponses +=
    appendedResponses +=
    appendedResponses +=
    appendedResponses +=
    appendedResponses +=
    appendedResponses +=

                
                    
                        <a class="link-in-dark-bg" href="mailTo:<%= targetEmail%> " <%=mailtoStyleAttr%>>[<%=targetEmailDisplay%>]</a>
             <span class="glyphicon <%= !shouldCollapsed ? "glyphicon-chevron-up" : "glyphicon-chevron-down" %> pull-right"></span> 
               </div>

    for(var receiver in data.responses[giver]){
        var receiversList = data.responses[giver];
        if(receiversList.hasOwnProperty(receiver)){
            appendedResponses += getResponsesToReceiver(giver, receiver, data);
        }
    }
}

function getResponsesToReceiver(giver, receiver, data){

}


$(document).ready(function(){
    var seeMoreRequest = function(e) {
        var panelHeading = $(this);
        var displayIcon = $(this).children('.display-icon');
        var formObject = $(this).children("form");
        var panelCollapse = $(this).parent().children('.panel-collapse');
        var panelBody = $(panelCollapse[0]).children('.panel-body');
        var formData = formObject.serialize();
        e.preventDefault();
        $.ajax({
            type : 'POST',
            url : 	$(formObject[0]).attr('action') + "?" + formData,
            beforeSend : function() {
                displayIcon.html("<img height='25' width='25' src='/images/ajax-preload.gif'/>")
            },
            error : function() {
                console.log('Error');
            },
            success : function(data) {
                getAppendedData(data);
                //$(panelBody[0]).html(getAppendedData(data));
                $(panelHeading).removeClass('ajax_submit');
                $(panelHeading).off('click');
                displayIcon.html('<span class="glyphicon glyphicon-chevron-down pull-right"></span>')
                $(panelHeading).click(toggleSingleCollapse);
                $(panelHeading).trigger('click');
            }
        });
    };
    $(".ajax_submit").click(seeMoreRequest);
});