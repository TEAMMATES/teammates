function linkAjaxForResponseRate() {
    const responseRateClickHandler = function (e) {
        const hyperlinkObject = $(e.currentTarget).clone();
        const parentOfHyperlinkObject = $(e.currentTarget).parent();
        e.preventDefault();
        $.ajax({
            type: 'POST',
            url: hyperlinkObject.attr('href'),
            beforeSend() {
                parentOfHyperlinkObject.html('<img src="/images/ajax-loader.gif"/>');
            },
            error() {
                parentOfHyperlinkObject.html('Failed. ')
                        .append(hyperlinkObject);
                hyperlinkObject.attr('data-toggle', 'tooltip')
                        .attr('data-placement', 'top')
                        .prop('title', 'Error occured while trying to fetch response rate. Click to retry.')
                        .html('Try again?')
                        .click(responseRateClickHandler);
            },
            success(data) {
                setTimeout(() => {
                    const type = data.sessionDetails ? 'sessionDetails' : 'evaluationDetails';
                    const { submittedTotal, expectedTotal } = data[type].stats;
                    parentOfHyperlinkObject.html(`${submittedTotal} / ${expectedTotal}`);
                }, 500);
            },
        });
    };
    $('td[class*="session-response-for-test"] > a').click(responseRateClickHandler);

    $('.table').each(function () {
        // this is bound to current object in question
        const currentTable = $(this).has('tbody').length ? $(this).find('tbody') : $(this);
        const allRows = currentTable.find('tr:has(td)');
        const recentElements = allRows.filter(i => $(allRows[i]).find('td[class*="recent"]').length);
        const nonRecentElements = allRows.filter(i => !$(allRows[i]).find('td[class*="recent"]').length);
        const sortedElements = $.merge(recentElements, nonRecentElements);
        sortedElements.each(function () {
            currentTable.get(0).appendChild(this);
        });
    });

    // recent class will only be appended to 'td' element with class 't_session_response'
    $('.table .recent a').click();
}

export {
    linkAjaxForResponseRate,
};
