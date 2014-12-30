$(document).ready(function(){
    $('#remindModal').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget); // Button that triggered the modal
        var actionlink = button.data('actionlink');
        var courseid = button.data('courseid');
        var fsname = button.data('fsname');
        
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
                setTimeout(function(){
                    var htmlToAppend = "";
                    var usersToRemind = data.responseStatus.noResponse;
                    var emailNameTable = data.responseStatus.emailNameTable;
                    
                    for (var i = 0 ; i < usersToRemind.length; i++) {
                    	htmlToAppend += "<div class=\"checkbox\">";
                        htmlToAppend += "<label><input type=\"checkbox\" name=\"usersToRemind\"";
                        htmlToAppend += "value=\"" + usersToRemind[i] + "\"> " + emailNameTable[usersToRemind[i]];
                        htmlToAppend +=  "</label></div>";
                    };
                    htmlToAppend += "<input type=\"hidden\" name=\"courseid\" value=\"" + courseid + "\">";
                    htmlToAppend += "<input type=\"hidden\" name=\"fsname\" value=\"" + fsname + "\">";
            		
                    $('#studentList').html(htmlToAppend);
                }, 500);
            }
        });
    });
});