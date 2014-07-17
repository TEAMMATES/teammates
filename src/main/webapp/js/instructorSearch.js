$(function() { 
	$('.comments > .list-group-item').hover(
	   function(){
		$("a[type='button']", this).show();
	}, function(){
		$("a[type='button']", this).hide();
	});
	
	$("div[id^=plainCommentText]").css("margin-left","15px");
});

function bindStudentPhotoLink(elements){
    $(elements).on('click', function(){
        var link = $(this).attr('data-link');
        $(this).siblings('img')
            .attr("src", link)
            .removeClass('hidden');
        $(this).remove();
    });
}

function bindErrorImages(elements){
    $(elements).on('error', function() {
        $(this).attr("src","../images/profile_picture_default.png");
    });
}

$(document).ready(function(){
    $("td[id^=studentphoto-c]").each(function(){
        bindStudentPhotoLink($(this).children('.student-photo-link-for-test'));
        bindErrorImages($(this).children('img'));
    });
});
