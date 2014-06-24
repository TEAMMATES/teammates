$(function () {
	$('.form-control').on('click', function() {
		if($(this).val() == $(this).attr('data-actual-value')) {
			$(this).select();
		}
	});
});

function finaliseForm(event) {
	if ($('#studentPhoto').val() == "") {
		$('#profileEditForm').submit();
		return;
	}
	
	initialSubmitMessage = $('#profileEditSubmit').html();
	
	$.ajax({
		url: "/page/studentProfileCreateFormUrl?user="+$("input[name='user']").val(),
		beforeSend : function() {
            $('#profileEditSubmit').html("<img src='../images/ajax-loader.gif'/>");
        },
        error: function() {
        	$(this).Text(initialSubmitMessage);
        	$('#statusMessage').css("display", "block")
        					   .attr('class', 'alert alert-danger')
        					   .html('There seems to be a network error, please try again later');
        	$("html, body").animate({ scrollTop: 0 });
        },
        success: function(data) {
        	if (!data.isError) {
	        	$('#profileEditForm').attr('enctype','multipart/form-data');
	        	// for IE compatibility
	        	$('#profileEditForm').attr('encoding','multipart/form-data');
	        	$('#profileEditForm').attr('action', data.formUrl);
	        	$('#profileEditForm').submit();
        	} else {
        		$(this).Text(initialSubmitMessage);
            	$('#statusMessage').css("display", "block")
            					   .attr('class', 'alert alert-danger')
            					   .html('There seems to be a network error, please try again later');
            	$("html, body").animate({ scrollTop: 0 });
        	}
        }
        
	});
}
