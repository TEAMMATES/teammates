/* global elasticlunr:true */

const questionMap = {};
let index = null;

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

function searchQuestions() {
    const query = $('#searchQuery').val();

    $('#searchResults').empty();
    if (query === '') {
        $('#allQuestions').show();
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

    $.each(query.split(' '), (idx, keyword) => {
        if (keyword.length > 2) {
            highlightKeyword(keyword);
        }
    });

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
