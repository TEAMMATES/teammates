import {
    attachEventToDeleteStudentLink,
    prepareInstructorPages,
} from '../common/instructor';

import {
    highlightSearchResult,
    toggleAdditionalQuestionInfo,
} from '../common/ui';

window.toggleAdditionalQuestionInfo = toggleAdditionalQuestionInfo;

$(document).ready(() => {
    prepareInstructorPages();

    $('.comments > .list-group-item').hover(function () {
        $("a[type='button']", this).show();
    }, function () {
        $("a[type='button']", this).hide();
    });

    attachEventToDeleteStudentLink();

    // highlight search string
    highlightSearchResult('#searchBox', '.panel-body');
});
