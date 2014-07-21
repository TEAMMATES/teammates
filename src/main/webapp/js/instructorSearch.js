$(function() { 
	$('.comments > .list-group-item').hover(
	   function(){
		$("a[type='button']", this).show();
	}, function(){
		$("a[type='button']", this).hide();
	});
	
	$("div[id^=plainCommentText]").css("margin-left","15px");
});
