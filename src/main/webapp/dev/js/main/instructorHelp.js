/* global elasticlunr:true */

const questionMap = {};
const numResultsPerPage = 5;

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

function prepareQuestionsForSearch() {
    let questionCount = 0;

    function addQuestionsForTopic(topic) {
        const topicContentElement = $(topic).next();

        topicContentElement.find('.panel-group').each((idx, subTopic) => {
            const subTopicText = $(subTopic).prev().text();
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
            subTopicTags = $.grep(subTopicTags, (v) => {
                if (v !== '') {
                    return true;
                }
                return false;
            });

            for (let i = 0; i < subTopicQuestions.length; i += 1) {
                const question = $(subTopicQuestions[i]);
                const htmlId = question.attr('id');
                const tagsSeenSoFar = {};

                let questionTags = subTopicTags.concat(htmlId.split('-'));
                questionTags = $.grep(questionTags, (item) => {
                    if (tagsSeenSoFar[item]) {
                        return false;
                    }
                    tagsSeenSoFar[item] = true;
                    return item;
                });

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

    curPage = page;
    const pageNum = page;
    const searchResultElements = $('#searchResults').children();
    const pageElements = $(searchResultElements.slice((pageNum - 1)*numResultsPerPage,
                                                     (pageNum - 1)*numResultsPerPage + numResultsPerPage));

    // hide all search results by default
    searchResultElements.each(function () {
        $(this).hide();
    });
    // show search results belonging to current page
    pageElements.each(function () {
        $(this).show();
    });
    // render paging controls
    $('#pagingControls').children().each((idx, button) => {
        if (idx === pageNum) {
            $(button).prop('disabled', true);
        } else {
            $(button).prop('disabled', false);
        }
    });
    $('#prevPage').prop('disabled', false);
    $('#nextPage').prop('disabled', false);

    // disable prev and next buttons for first and last page respectively
    if (pageNum === 1) {
        $('#prevPage').prop('disabled', true);
    }
    if (pageNum === numPages) {
        $('#nextPage').prop('disabled', true);
    }
}

function searchQuestions() {
    const query = $('#searchQuery').val();

    $('#searchResults').empty();
    $('#pagingControls').empty();
    $('#pagingControls').removeClass('padding-15px margin-bottom-35px');
    if (query === '') {
        $('#allQuestions').show();
        $('#pagingDivider').hide();
        $('#searchMetaData').text('');
        return;
    }
    const results = index.search(query, {
        fields: {
            title: { boost: 4 },
            tags: { boost: 2 },
            body: { boost: 0.5 },
        },
        bool: 'AND',
    });
    $.each(results, (idx, result) => {
        const { htmlId } = questionMap[result.ref];
        const questionPanel = $(`#${htmlId}`)[0];
        $('#searchResults').append(questionPanel.outerHTML);
    });

    // highlight matches for keyword in the search results
    function highlightKeyword(keyword) {
        const highlightRegex = new RegExp(keyword, 'g');
        $('#searchResults').children().each(function () {
            // highlight heading matches for query
            $(this).find('.panel-heading').html(function () {
                return $(this).html().replace(highlightRegex, `<span class='text-bold color-positive'>${keyword}</span>`);
            });
            // highlight body matches for query
            $(this).find('.panel-body').html(function () {
                return $(this).html().replace(highlightRegex, `<span class='text-bold color-positive'>${keyword}</span>`);
            });
        });
    }

    function initializePagingControls() {
        const numResults = results.length;
        numPages = Math.ceil(numResults/numResultsPerPage);

        if (numPages < 2) {
            return;
        }

        let buttonHtml = '<button type="button" class="btn btn-default" id="prevPage" onclick="renderPage(\'prev\')">'
                         + '<span class="glyphicon glyphicon-chevron-left"></span>Previous</button>';
        for (let i = 0; i < numPages; i += 1) {
            buttonHtml += `<button type='button' class='btn btn-default' onclick='renderPage(${i + 1})'>${i + 1}</button>`;
        }
        buttonHtml += '<button type="button" class="btn btn-default" id="nextPage" onclick="renderPage(\'next\')">'
                     + 'Next<span class="glyphicon glyphicon-chevron-right"></span></button>';
        $('#pagingControls').html(buttonHtml);
        $('#pagingControls').addClass('padding-15px margin-bottom-35px');
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
    $('#searchMetaData').text(`${results.length} results found for '${query}'`);
    $('#allQuestions').hide();
}

function bindEnterKeyForSearchBox() {
    $('#searchQuery').keypress((e) => {
        if (e.keyCode === 13) {
            $('#search').click();
        }
    });
}

function resetSearch() {
    $('#searchQuery').val('');
    $('#searchMetaData').text('');
    $('#searchResults').empty();
    $('#pagingControls').empty();
    $('#pagingDivider').hide();
    $('#allQuestions').show();
}

$(document).ready(() => {
    syncPanelCollapseWithUrlHash();
    bindPanelCollapseLinksAndAnchor();
    prepareQuestionsForSearch();
    bindEnterKeyForSearchBox();
    resetSearch();
});

window.searchQuestions = searchQuestions;
window.resetSearch = resetSearch;
window.renderPage = renderPage;
