import {
    bindBackToTopButtons,
} from '../common/administrator';

import {
    showModalConfirmation,
} from '../common/bootboxWrapper';

import {
    BootstrapContextualColors,
} from '../common/const';

import {
    toggleSort,
} from '../common/sortBy';

const entryPerPage = 200;

let begin = 0;
let end = 0;
let total = 0;

let currentPage = 1;
let totalPages;

function updatePagination() {
    if (totalPages > 5) {
        if (currentPage >= 3 && currentPage + 1 < totalPages) {
            $('div#pagination_top ul.pagination li a.pageNumber').each(function (index) {
                const newPageNumber = (currentPage - 2) + index;
                $(this).text(newPageNumber);
            });
        }

        if (currentPage >= 3 && currentPage + 1 === totalPages) {
            $('div#pagination_top ul.pagination li a.pageNumber').each(function (index) {
                const newPageNumber = (currentPage - 3) + index;
                $(this).text(newPageNumber);
            });
        }

        if (currentPage < 3) {
            $('div#pagination_top ul.pagination li a.pageNumber').each(function (index) {
                $(this).text(index + 1);
            });
        }
    } else {
        $('div#pagination_top ul.pagination li a.pageNumber').each(function (index) {
            $(this).text(index + 1);

            if (index + 1 > totalPages) {
                $(this).parent().hide();
            }
        });
    }

    $('div#pagination_top ul.pagination li a.pageNumber').each(function () {
        const pageNum = parseInt($(this).text(), 10);
        if (pageNum === currentPage) {
            $(this).parent().attr('class', 'active');
        } else {
            $(this).parent().attr('class', '');
        }
    });

    $('#pagination_bottom').html($('#pagination_top').html());
}

function caculateTotalPages() {
    const a = parseInt(total / entryPerPage, 10);
    const b = total % entryPerPage;
    totalPages = b === 0 ? a : a + 1;
}

function updateEntriesCount() {
    const newText = `${begin}~${Math.min(end, total)}`;

    $('span#currentPageEntryCount').text(newText);
    $('span#totalEntryCount').text(total);
}

function hideAllEntries() {
    $('tr.accountEntry').hide();
}

function showEntryInInterval(startIndex, endIndex) {
    hideAllEntries();
    for (let i = startIndex; i <= endIndex; i += 1) {
        $(`#accountEntry_${i}`).show();
    }
}

function showFirstPage() {
    hideAllEntries();
    begin = 1;
    end = entryPerPage;
    currentPage = 1;
    showEntryInInterval(begin, end);
}

function reLabelOrderedAccountEntries() {
    total = 0;
    $('tr.accountEntry').each(function (index) {
        $(this).attr('id', `accountEntry_${index + 1}`);
        total += 1;
    });

    showFirstPage();
    updateEntriesCount();
    updatePagination();
}

function showEntriesForSelectedPage() {
    begin = ((currentPage - 1) * entryPerPage) + 1;
    end = begin + (entryPerPage - 1);
    showEntryInInterval(begin, end);
}

function goToPreviousPage() {
    currentPage = currentPage > 1 ? currentPage - 1 : currentPage;
    showEntriesForSelectedPage();
    updateEntriesCount();
    updatePagination();
}

function goToNextPage() {
    currentPage = currentPage < totalPages ? currentPage + 1 : totalPages;
    showEntriesForSelectedPage();
    updateEntriesCount();
    updatePagination();
}

function bindDeleteAccountAction() {
    $('.admin-delete-account-link').on('click', (event) => {
        event.preventDefault();

        const $clickedLink = $(event.currentTarget);
        const googleId = $clickedLink.data('googleId');
        const existingCourses = $(`#courses_${googleId}`).html();

        const messageText = `Are you sure you want to delete the account ${googleId}?`
                + `<br><br>${existingCourses}`
                + '<br><br>This operation will delete ALL information about this account from the system.';

        const okCallback = function () {
            window.location = $clickedLink.attr('href');
        };

        showModalConfirmation('Confirm deletion',
                messageText, okCallback, null, null, null, BootstrapContextualColors.DANGER);
    });
}

$(document).ready(() => {
    toggleSort($('#button_sort_createat').parent());
    reLabelOrderedAccountEntries();
    caculateTotalPages();
    updatePagination();
    showFirstPage();
    updateEntriesCount();
    bindDeleteAccountAction();
    bindBackToTopButtons('.back-to-top-left, .back-to-top-right');
});

$(document).on('click', '.toggle-sort-and-relabel', (e) => {
    toggleSort(e.currentTarget);
    reLabelOrderedAccountEntries();
});

$(document).on('click', 'ul.pagination li.previous', () => {
    goToPreviousPage();
});

$(document).on('click', 'ul.pagination li a.pageNumber', function () {
    currentPage = parseInt($(this).text(), 10);
    showEntriesForSelectedPage();
    updateEntriesCount();
    updatePagination();
});

$(document).on('click', 'ul.pagination li.next', () => {
    goToNextPage();
});

$(document).keydown((e) => {
    if (e.keyCode === 37) { // LEFT
        goToPreviousPage();
    }
    if (e.keyCode === 39) { // RIGHT
        goToNextPage();
    }
});
