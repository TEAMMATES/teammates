$(document).ready(function(){
    var seeMoreRequest = function(e) {
        var submitButton = $(this);
        var formObject = $(this).parent().parent();
        var formData = formObject.serialize();
        
        e.preventDefault();
        
        $.ajax({
            type : 'POST',
            url : 	submitButton.attr('href') + "?" + formData,
            beforeSend : function() {
                submitButton.html("<img src='/images/ajax-loader.gif'/>");
            },
            error : function() {
                submitButton.html('See More');
                console.log('Error');
            },
            success : function(data) {
                submitButton.html('See More');
                console.log(data);
            }
        });
    };
    $("form[class*='seeMoreForm'] > div > a").click(seeMoreRequest);
});