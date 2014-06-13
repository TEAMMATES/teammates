$(function () {
	$('.form-control').on('click', function() {
		if($(this).val() == $(this).attr('data-actual-value')) {
			$(this).select();
		}
	});
});
