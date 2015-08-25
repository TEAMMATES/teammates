$(document).ready(function() {
     $.ajax({
        type: 'GET',
        url: motdUrl,
        success: function(data) {
            $('#student-motd').html(data);
        },
        error: function(jqXHR, textStatus, errorThrown) {

        }
    });
});

function closeMotd() {
    $('#student-motd-container').hide();
}