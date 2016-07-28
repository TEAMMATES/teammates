$(function() {
    $('.comments > .list-group-item').hover(function() {
        $("a[type='button']", this).show();
    }, function() {
        $("a[type='button']", this).hide();
    });
    
    // highlight search string
    highlightSearchResult('#searchBox', '.panel-body');

    $('div[id^=plainCommentText]').css('margin-left', '15px');
});
