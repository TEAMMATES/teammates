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

        topicContentElement.find('.panel-group').each(function (idx, subTopic) {
            const subTopicText = $(subTopic).prev().text();
            const subTopicQuestions = $(subTopic).children();

            let subTopicTags = subTopicText.split(' ').map(function (word) {
                // ignore 1 and 2 letter words which are probably articles and prepositions
                if (word.length < 3) {
                    return '';
                }
                return elasticlunr.stemmer(word);
            });
            subTopicTags = subTopicTags.filter(function(v){return v !== '' });

            for (let i = 0; i < subTopicQuestions.length; i += 1) {
                const question = $(subTopicQuestions[i]);
                const htmlId = question.attr('id');
                const questionTags = subTopicTags.concat(htmlId.split('-'));

                questionMap[questionCount] = {
                    id: questionCount,
                    htmlId: htmlId,
                    title: question.find('.panel-title').text(),
                    body: question.find('.panel-body').text(),
                    tags: questionTags
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
        $.each(questionMap, function (id, question) {
            index.addDoc(question);
        });
    }

    $('#topics li').each(function (idx, li) {
        const topicId = $(li).find('a').attr('href');
        addQuestionsForTopic(topicId);
    });
    console.log(questionMap);
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
            title: {boost: 4},
            tags: {boost: 2},
            body: {boost: 0.5}
        },
        bool: "AND"
    });
    $.each(results, function(idx, result) {
        const htmlId = questionMap[result.ref].htmlId;
        const questionPanel = $(`#${htmlId}`)[0];
        $('#searchResults').append(questionPanel.outerHTML);
    });
    $('#searchMetaData').text(`${results.length} results found for "${query}"`);
    $('#allQuestions').hide();
}

function bindEnterKeyForSearchBox() {
    $('#searchQuery').keypress(function(e){
        if(e.keyCode === 13) {
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