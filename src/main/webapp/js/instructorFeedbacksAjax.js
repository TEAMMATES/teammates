$(document).ready(function(){
    var ajaxRequest = function(e) {
        e.preventDefault();
        var formData = $(this).serialize();
        $.ajax({
            type : 'POST',
            cache: false,
            url :   $(this).attr('action') + "?" + formData,
            beforeSend : function() {
                console.log('Before sending');
            },
            error : function() {
                console.log('Error');
            },
            success : function(data) {
                var appendedModalBody = $(data).find('#copySessionsBody').html();
                var appendedSessionTable = $(data).find('#sessionList').html();

                $('#copySessionsBody').html(appendedModalBody);
                $('#frameBodyWrapper').append(appendedSessionTable);
                bindEventsAfterAjax();
            }
        });
    };
    $("#ajaxForSessions").submit(ajaxRequest);
});