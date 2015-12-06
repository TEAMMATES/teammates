function linkAjaxForResponseRate(){
    var responseRateClickHandler = function(e) {
        var hyperlinkObject = $(this).clone(),
            parentOfHyperlinkObject = $(this).parent();
        e.preventDefault();
        $.ajax({
            type: 'POST',
            url: hyperlinkObject.attr('href'),
            beforeSend: function() {
                parentOfHyperlinkObject.html('<img src="/images/ajax-loader.gif"/>');
            },
            error: function() {
                parentOfHyperlinkObject.html('Failed. ')
                                       .append(hyperlinkObject);
                hyperlinkObject.attr('data-toggle', 'tooltip')
                               .attr('data-placement', 'top')
                               .prop('title', 'Error occured while trying to fetch response rate. Click to retry.')
                               .html('Try again?')
                               .click(responseRateClickHandler);
            },
            success: function(data) {
                setTimeout(function(){
                    var type = data.sessionDetails ? 'sessionDetails' : 'evaluationDetails';
                    parentOfHyperlinkObject.html(data[type].stats.submittedTotal + ' / ' +
                                                 data[type].stats.expectedTotal);
                }, 500);
            }
        });
    };
    $('td[class*="session-response-for-test"] > a').click(responseRateClickHandler);


    $(".table").each(function(idx) {
        //this is bound to current object in question
        var currentTable = $(this).has('tbody').length ? $(this).find('tbody') : $(this);
        
        var allRows = currentTable.find('tr:has(td)');
        var recentElements = allRows.filter(function(i){
            return $(allRows[i]).find('td[class*="recent"]').length;
        }),
            nonRecentElements = allRows.filter(function(i){
            return !$(allRows[i]).find('td[class*="recent"]').length;
        });

        var sortedElements = $.merge(recentElements, nonRecentElements);
        sortedElements.each(function(idx) {
            currentTable.get(0).appendChild(this);
        });
    });
    
    //recent class will only be appended to 'td' element with class 't_session_response'
    $('.table .recent a').click();
}

$(document).ready(function(){
    linkAjaxForResponseRate();
});