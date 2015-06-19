$(document).ready(function() {
    $('#remindModal').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget); // Button that triggered the modal
        var actionlink = button.data('actionlink');
        
        $.ajax({
            type : 'POST',
            cache : false,
            url : actionlink,
            beforeSend : function() {
                $('#studentList').html('<img class="margin-center-horizontal" src="/images/ajax-loader.gif"/>');
            },
            error : function() {
                $('#studentList').html('Error retrieving student list.' + 
                  'Please close the dialog window and try again.');
            },
            success : function(data) {
                setTimeout(function() {
                    $('#studentList').html(data);
                }, 500);
            }
        });
    });
});