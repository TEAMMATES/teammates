$(function () {
	$('.form-control').on('click', function() {
		if($(this).val() == $(this).attr('data-actual-value')) {
			$(this).select();
		}
	});
	
	$(window).load(function() {
		if($('#profilePic').attr('data-edit') == "true") {
			$('#studentPhotoUploader').modal({
				keyboard: false,
				show: true
			});
			
			$('#editableProfilePicture').Jcrop({
				bgColor: 'transparent',
				setSelect: [10, 10, 200, 200],
				aspectRatio: 1,
				bgOpacity: 0.4,
				addClass: "inline-block",
				boxWidth: 400,
				boxHeight: 400,
				onSelect: updateFormData
			});
		}
	});
});

function updateFormData(coords) {
	$('#cropBox').val(coords.x + "-" + coords.y + "-" + coords.x2 + "-" + coords.y2);
}

function finaliseUploadPictureForm(event) {
	if ($('#studentPhoto').val() == "") return;
	
	initialSubmitMessage = $('#profileUploadPictureSubmit').html();
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
