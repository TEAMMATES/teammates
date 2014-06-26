$(function () {
	$('.form-control').on('click', function() {
		if($(this).val() == $(this).attr('data-actual-value')) {
			$(this).select();
		}
	});
	
	$(window).load(function() {
		if($('#profilePic').attr('data-edit') == "true") {
			$('#studentPhotoUploader').modal('show');
		}
	});
});

function finaliseUploadPictureForm(event) {
	if ($('#studentPhoto').val() == "") return;
	
	initialSubmitMessage = $('#profileUploadPictureSubmit').html();
	alert('1');
	$.ajax({
		url: "/page/studentProfileCreateFormUrl?user="+$("input[name='user']").val(),
		beforeSend : function() {
            $('#profileUploadPictureSubmit').html("<img src='../images/ajax-loader.gif'/>");
        },
        error: function() {
        	$('#profileUploadPictureSubmit').Text(initialSubmitMessage);
        	$('#statusMessage').css("display", "block")
        					   .attr('class', 'alert alert-danger')
        					   .html('There seems to be a network error, please try again later');
        	$("html, body").animate({ scrollTop: 0 });
        },
        success: function(data) {
        	if (!data.isError) {
	        	$('#profilePictureUploadForm').attr('enctype','multipart/form-data');
	        	// for IE compatibility
	        	$('#profilePictureUploadForm').attr('encoding','multipart/form-data');
	        	$('#profilePictureUploadForm').attr('action', data.formUrl);
	        	$('#profilePictureUploadForm').submit();
        	} else {
        		$('#profileUploadPictureSubmit').Text(initialSubmitMessage);
            	$('#statusMessage').css("display", "block")
            					   .attr('class', 'alert alert-danger')
            					   .html('There seems to be a network error, please try again later');
            	$("html, body").animate({ scrollTop: 0 });
        	}
        }
        
	});
}
