$(function(){
    //make textarea supports displaying breakline
    $('div[id^="plainCommentText"]').each(function(){
        var commentTextWithBreakLine = $(this).text().replace(/\n/g, '<br />');
        $(this).html(commentTextWithBreakLine);
    });
});
