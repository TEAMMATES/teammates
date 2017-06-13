import { prepareInstructorPages } from '../common/instructor.es6';
import { highlightSearchResult } from '../common/ui.es6';

$(document).ready(() => {
    prepareInstructorPages();

    $('.comments > .list-group-item').hover(function () {
        $("a[type='button']", this).show();
    }, function () {
        $("a[type='button']", this).hide();
    });

    // highlight search string
    highlightSearchResult('#searchBox', '.panel-body');
});
