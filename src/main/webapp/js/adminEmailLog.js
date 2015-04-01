var retryTimes = 0;
var numOfEntriesPerPage = 50;

$(document).ready(function(){
	bindClickAction();
	clickOlderButtonIfNeeded();
	$("#filterReference").toggle();
});

function toggleReference() {
	$("#filterReference").toggle("slow");
	
	var button = $("#detailButton").attr("class");
	
	if(button == "glyphicon glyphicon-chevron-down"){
	$("#detailButton").attr("class","glyphicon glyphicon-chevron-up");
	$("#referenceText").text("Hide Reference");
	}else{
		$("#detailButton").attr("class","glyphicon glyphicon-chevron-down");
		$("#referenceText").text("Show Reference");
	}
}

function bindClickAction(){
	$("body").unbind('click', handler).on("click", ".log",handler);
}

var handler = function(event){
	$(this).next("#small").toggle();
    $(this).next("#small").next("#big").toggle();
};

function clickOlderButtonIfNeeded(){
	if(retryTimes >= 20){
		return;
	}
	
	var curNumOfEntries = $("#emailLogsTable tbody tr").length;
	
	if(curNumOfEntries < numOfEntriesPerPage){
		if($("#button_older").length){
			$("#button_older").click();
			retryTimes ++;
		}
	}
}

function submitFormAjax(offset) {
	$('input[name=offset]').val(offset);
	var formObject = $("#ajaxLoaderDataForm");
	var formData = formObject.serialize();
	var button = $('#button_older');
	var lastLogRow = $('#emailLogsTable tr:last');
	
	$.ajax({
        type : 'POST',
        url :   "/admin/adminEmailLogPage?" + formData,
        beforeSend : function() {
        	button.html("<img src='/images/ajax-loader.gif'/>");
        },
        error : function() {
            setFormErrorMessage(button, "Failed to load older logs. Please try again.");
            button.html("Retry");        	
        },
        success : function(data) {
            setTimeout(function() {
                if (!data.isError) {
	                    // Inject new log row              	
	                	var logs = data.logs;                	
	                	jQuery.each(logs, function(i, value){                		
	                	lastLogRow.after(value.logInfoAsHtml);
	                	lastLogRow = $('#emailLogsTable tr:last');
	                	bindClickAction();
	                	clickOlderButtonIfNeeded();
                	});
                	
                } else {
                    setFormErrorMessage(button, data.errorMessage);
                }
            	               
                $("#statusMessage").html(data.statusForAjax);

            },500);
        }
    });
}

function setFormErrorMessage(button, msg){
	button.after("&nbsp;&nbsp;&nbsp;"+ msg);
}

