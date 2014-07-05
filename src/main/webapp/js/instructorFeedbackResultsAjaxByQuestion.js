function getAppendedData(data){
    var appendHtml = '';
    if(data.responses.length == 0){

        appendHtml += 'There is currently no responses for this question or you are not allowed to see the responses in this question';
        return appendHtml;
    }
    appendHtml += "<div class='resultStatistics'>";
    appendHtml += data.questionStats;
    appendHtml += "</div>";
    appendHtml += "<div class='table-responsive'>";
    appendHtml += "<table class='table table-striped table-bordered dataTable margin-0'>";
    appendHtml += "<thead class='background-color-medium-gray text-color-gray font-weight-normal'>";
    appendHtml += "<tr>";
    appendHtml += "<th id='button_sortFromName' onclick='toggleSort(this,1)' style='width: 15%;'>Giver</th>";
    appendHtml += "<th id='button_sortFromTeam' onclick='toggleSort(this,2)' style='width: 15%;'>Team</th>";
    appendHtml += '<th id="button_sortToName" onclick="toggleSort(this,3)" style="width: 15%;">Recipient</th>';
    appendHtml += '<th id="button_sortToTeam" class="button-sort-ascending" onclick="toggleSort(this,4)" style="width: 15%;">Team</th>';
    appendHtml += '<th id="button_sortFeedback" onclick="toggleSort(this,5)">Feedback</th>';
    appendHtml += '</tr></thead>';
    appendHtml += '<tbody>';
    for(var i = 0; i < data.responses.length; i++){
        appendHtml += '<tr>';
        appendHtml += '<td class="middlealign">' + data.emailNameTable[(data.responses[i])['giverEmail']] + '</td>';
        appendHtml += '<td class="middlealign">' + data.emailTeamTable[(data.responses[i])['giverEmail']] + '</td>';
        appendHtml += '<td class="middlealign">' + data.emailNameTable[(data.responses[i])['recipientEmail']] + '</td>';
        appendHtml += '<td class="middlealign">' + data.emailTeamTable[(data.responses[i])['recipientEmail']] + '</td>';
        appendHtml += '<td class="multiline">' + data.answerTable[(data.responses[i])['feedbackResponseId']] + '</td>';
        appendHtml += '</tr>';                            
    }
    appendHtml += '</tbody></table></div>';

    return appendHtml;
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
                var appendedData = getAppendedData(data);
                if(appendedData.indexOf('resultStatistics') == -1){
                    $(panelBody[0]).removeClass('padding-0');
                }
                $(panelBody[0]).html(appendedData);
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