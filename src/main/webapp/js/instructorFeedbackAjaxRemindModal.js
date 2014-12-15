$(document).ready(function(){
    $('#remindModal').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget); // Button that triggered the modal
        var actionlink = button.data('actionlink');

	    $.ajax({
            type : 'POST',
            cache : false,
            url : actionlink,
            beforeSend : function() {
                $('#studentList').html("<img class='margin-center-horizontal' src='/images/ajax-loader.gif'/>");
            },
            error : function() {
            	$('#studentList').html('Error retrieving student list.' + 
            			'Please close the dialog window and try again.');
            },
            success : function(data) {
            	console.log(data);
            	setTimeout(function(){
            		var htmlToAppend = "";
            		var usersToRemind = data.responseStatus.noResponse;
            		
            		htmlToAppend += "<form>"
            		for (var i = 0 ; i < usersToRemind.length; i++) {
            			htmlToAppend += "<label><input type=\"checkbox\" name=\"usersToRemind\"";
            			htmlToAppend += "value=\"" + usersToRemind[i] + "\"> " + usersToRemind[i];
            			htmlToAppend +=  "</input></label><br>";
            		};
            		htmlToAppend += "</form>"
            		
            		$('#studentList').html(htmlToAppend);
            	}, 500);
            }
        });
    });
});