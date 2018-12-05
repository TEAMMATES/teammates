/* global elasticlunr:true */

const questionMap = {};
const numResultsPerPage = 10;

let index = null;
let curPage = 1;
let numPages = 1;

/**
 * Shows the panel specified by the URL hash,
 * e.g. "instructorHelp.jsp#question-essay".
 */
function syncPanelCollapseWithUrlHash() {
    if (window.location.hash) {
        const target = $('body').find(window.location.hash);
        if ($(target).hasClass('panel')) {
            const targetCollapse = $(target).find('.collapse');
            $(targetCollapse).collapse('show');
        }
    }
}

/**
 * Shows the panel specified by the data-target when any
 * .collapse-link element is clicked.
 *
 * To be used by links on the instructorHelp.jsp page itself.
 */
function bindPanelCollapseLinksAndAnchor() {
    $('.collapse-link').on('click', function () {
        const targetPanel = this.getAttribute('data-target');
        $(targetPanel).collapse('show');
    });
}

/**
 * Constructs a hashmap of questions and utilizes it to
 * build an index for search. Each question also has associated
 * tags which are obtained from stemming and sub-categories allocated
 * to different questions.
 */
function prepareQuestionsForSearch() {
    let questionCount = 0;

    function addQuestionsForTopic(topic) {
        const topicContentElement = $(topic).next('.contentHolder');

        topicContentElement.find('.panel-group').each((idx, subTopic) => {
            const subTopicText = $(subTopic).prev('h3').text();
            const subTopicQuestions = $(subTopic).children();

            let subTopicTags = subTopicText.split(' ').map((word) => {
                // ensure case insensitivity
                const lowerCaseWord = word.toLowerCase();

                // ignore 1 and 2 letter words which are probably articles and prepositions
                if (lowerCaseWord.length < 3) {
                    return '';
                }
                return elasticlunr.stemmer(lowerCaseWord);
            });
            subTopicTags = $.grep(subTopicTags, tag => tag !== '');

            for (let i = 0; i < subTopicQuestions.length; i += 1) {
                const question = $(subTopicQuestions[i]);
                const htmlId = question.attr('id');
                const questionTags = [...new Set([...subTopicTags, ...htmlId.split('-')])];

                questionMap[questionCount] = {
                    id: questionCount,
                    htmlId,
                    title: question.find('.panel-title').text(),
                    body: question.find('.panel-body').text(),
                    tags: questionTags,
                };
                questionCount += 1;
            }
        });
    }

    function buildIndex() {
        index = elasticlunr();
        index.addField('title');
        index.addField('body');
        index.addField('tags');
        index.setRef('id');
        $.each(questionMap, (id, question) => {
            index.addDoc(question);
        });
    }

    $.each($('#topics li'), (idx, li) => {
        const topicId = $(li).find('a').attr('href');
        addQuestionsForTopic(topicId);
    });
    buildIndex();
}

/**
 * Renders the search results of the specified page and
 * re-initializes the pagination toolbar to highlight the new page.
 *
 * @param page current page to be rendered
 */
function renderPage(page) {
    if (page < 1 || page > numPages) {
        return;
    }
    if (page === 'prev') {
        renderPage(curPage - 1);
        return;
    }
    if (page === 'next') {
        renderPage(curPage + 1);
        return;
    }

    // remove currently highlighted page before changing current page
    $(`#pagingControls > button:contains(${curPage})`).removeClass('btn-primary').addClass('btn-default');
    curPage = page;

    const searchResultElements = $('#searchResults').children();
    const pageElements = $(searchResultElements.slice((curPage - 1) * numResultsPerPage,
            (curPage - 1) * numResultsPerPage + numResultsPerPage));

    // hide all search results by default
    searchResultElements.each((_, resultElem) => {
        $(resultElem).hide();
    });
    // show search results belonging to current page
    pageElements.each((_, pageElem) => {
        $(pageElem).show();
    });
    // render paging controls
    $('#pagingControls').children().each((idx, button) => {
        let pageNum = $(button).text();

        // previous and next buttons should always be visible and enabled
        if ($(button).attr('data') === 'prevPage' || $(button).attr('data') === 'nextPage') {
            $(button).removeClass('hidden-xs');
            $(button).prop('disabled', false);
        } else if (pageNum === '...') { // remove redundant ellipses
            $(button).remove();
        } else {
            pageNum = parseInt(pageNum, 10);
            if (pageNum === curPage) { // disable current page button and highlight it
                $(button).removeClass('btn-default hidden-xs');
                $(button).addClass('btn-primary');
                $(button).prop('disabled', true);
            } else if (
                pageNum === 1
                || pageNum === numPages
                || pageNum === curPage - 1
                || pageNum === curPage + 1
            ) {
                $(button).removeClass('hidden-xs');
                $(button).prop('disabled', false);

                // add ellipses for small screen instead of showing all buttons
                if ((pageNum === curPage - 1) && (curPage - 2 > 1)) {
                    $(button).before('<button type="button" class="btn visible-xs" disabled>...</button>');
                }
                if ((pageNum === curPage + 1) && (curPage + 2 < numPages)) {
                    $(button).after('<button type="button" class="btn visible-xs" disabled>...</button>');
                }
            } else { // enable all other buttons
                $(button).addClass('hidden-xs');
                $(button).prop('disabled', false);
            }
        }
    });

    // disable prev and next buttons for first and last page respectively
    if (curPage === 1) {
        $('button[data="prevPage"]').prop('disabled', true);
    }
    if (curPage === numPages) {
        $('button[data="nextPage"]').prop('disabled', true);
    }
}

/**
 * Resets the page to default version and clears the search box.
 */
function displayDefaultAndClearSearchBox() {
    $('#searchQuery').val('');
    $('#searchMetaData').text('');
    $('#searchResults').empty().hide();
    $('#pagingControls').empty();
    $('#pagingControls').removeClass('padding-15px margin-bottom-35px');
    $('#pagingDivider').hide();
    $('#allQuestions').show();
    $('#topics').show();
}

function bindPagingButtons() {
    function bindPageButton(pageButton, pageNo) {
        $(pageButton).on('click', () => {
            renderPage(pageNo);
        });
    }
    $('#pagingControls').find('button').each((idx, pageButton) => {
        if ($(pageButton).text() !== '...') {
            if ($(pageButton).attr('data') === 'prevPage') {
                bindPageButton(pageButton, 'prev');
            } else if ($(pageButton).attr('data') === 'nextPage') {
                bindPageButton(pageButton, 'next');
            } else {
                bindPageButton(pageButton, idx);
            }
        }
    });
}

/**
 * Searches for the query specified in the search box
 */
function searchQuestions() {
    const query = $('#searchQuery').val();
    $('#searchResults').empty();
    $('#pagingControls').empty();
    $('#pagingControls').removeClass('padding-15px margin-bottom-35px');
    if (query === '') {
        displayDefaultAndClearSearchBox();
        return;
    }
    // different fields are given a boost value to specify their relative importance
    // for details refer to elasticlunr documentation
    const results = index.search(query, {
        fields: {
            title: { boost: 4 },
            tags: { boost: 2 },
            body: { boost: 0.5 },
        },
        bool: 'AND',
    });
    // sort results in descending order of score followed by reference number
    results.sort((r1, r2) => {
        const diff = r2.score - r1.score;

        return (Math.abs(diff) < Number.EPSILON) ? (r1.ref - r2.ref) : diff;
    });

    // add relevant panels to search results
    $.each(results, (idx, result) => {
        const { htmlId } = questionMap[result.ref];
        const questionPanel = $(`#${htmlId}`).get(0);
        $('#searchResults').append(questionPanel.outerHTML);
    });

    // highlight matches for keyword in the search results
    function highlightKeyword(keyword) {
        $('#searchResults').find('.panel.panel-default').each((_, resultPanel) => {
            // highlight heading and body matches for query
            $(resultPanel).mark(keyword);
        });
    }

    function initializePagingControls() {
        const numResults = results.length;
        numPages = Math.ceil(numResults / numResultsPerPage);

        if (numPages < 2) {
            return;
        }

        let buttonHtml = '<button type="button" class="btn btn-default" data="prevPage">'
                         + '<span class="glyphicon glyphicon-chevron-left"></span></button>';
        for (let i = 0; i < numPages; i += 1) {
            buttonHtml += `<button type='button' class='btn btn-default'>${i + 1}</button>`;
        }
        buttonHtml += '<button type="button" class="btn btn-default" data="nextPage">'
                     + '<span class="glyphicon glyphicon-chevron-right"></span></button>';
        $('#pagingControls').html(buttonHtml);
        $('#pagingControls').addClass('padding-15px margin-bottom-35px');
        bindPagingButtons();
    }

    // highlight each keyword of query in search results
    $.each(query.split(' '), (idx, keyword) => {
        if (keyword.length > 2) {
            highlightKeyword(keyword);
        }
    });

    initializePagingControls();
    // render first page by default
    renderPage(1);
    $('#pagingDivider').show();
    $('#searchResults').show();
    $('#searchMetaData').text(`${results.length} results found for '${query}'`);
    $('#allQuestions').hide();
    $('#topics').hide();
}

function bindEnterKeyForSearchBox() {
    $('#searchQuery').keypress((e) => {
        // 13 is the Unicode character code for enter key
        if (e.keyCode === 13) {
            $('#search').click();
        }
    });
}

function bindSearchButton() {
    $('#search').on('click', () => {
        searchQuestions();
    });
}

function bindClearSearchButton() {
    $('#clear').on('click', () => {
        displayDefaultAndClearSearchBox();
    });
}

$(document).ready(() => {
    syncPanelCollapseWithUrlHash();
    bindPanelCollapseLinksAndAnchor();
    prepareQuestionsForSearch();
    bindEnterKeyForSearchBox();
    bindSearchButton();
    bindClearSearchButton();
    displayDefaultAndClearSearchBox();
});
