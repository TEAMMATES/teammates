/**
 * Holds Handsontable settings, reference and other information for the spreadsheet interface.
 */
/* global Handsontable:false */
import {
    prepareInstructorPages,
} from '../common/instructor';

import {
    showModalConfirmation,
    showModalConfirmationWithCancel,
} from '../common/bootboxWrapper';

import {
    BootstrapContextualColors,
    ParamsNames,
    Const,
} from '../common/const';

import {
    getUpdatedHeaderString,
    getUserDataRows,
    ajaxDataToHandsontableData,
    getUpdatedData,
    displayNoExistingStudents,
    displayErrorExecutingAjax,
    displayErrorExecutingAjaxUpdate,
    getSpreadsheetLength,
    toggleStudentsPanel,
    getUpdatedStudentRows,
    getNewEmailList,
} from '../common/instructorEnroll';

import {
    appendNewStatusMessage,
    clearStatusMessages
} from '../common/statusMessage';

const dataContainer = document.getElementById('existingDataSpreadsheet');
const dataHandsontable = new Handsontable(dataContainer, {
    height: 400,
    autoWrapRow: true,
    preventOverflow: 'horizontal',
    manualColumnResize: true,
    manualRowResize: true,
    rowHeaders: true,
    colHeaders: ['Section', 'Team', 'Name', 'Email', 'Comments', 'Fill in the new email here'],
    columnSorting: true,
    sortIndicator: true,
    minRows: 20,
    maxCols: 6,
    stretchH: 'all',
});

dataHandsontable.addHook('beforeColumnSort', function () {
    dataHandsontable.loadData(existingStudentsData); // load latest existing copy of the data
});

dataHandsontable.addHook('afterColumnSort', function () {
    updateDataHandsontableCellSettings(resetDefaultViewRenderer);
    existingStudentsData = dataHandsontable.getData();
});

const enrollContainer = document.getElementById('enrollSpreadsheet');
const enrollHandsontable = new Handsontable(enrollContainer, {
    className: 'enroll-handsontable',
    height: 500,
    autoWrapRow: true,
    preventOverflow: 'horizontal',
    manualColumnResize: true,
    manualRowResize: true,
    manualColumnMove: true,
    rowHeaders: true,
    colHeaders: ['Section', 'Team', 'Name', 'Email', 'Comments'],
    columnSorting: true,
    sortIndicator: true,
    minRows: 20,
    maxCols: 5,
    maxRows: 100,
    stretchH: 'all',
    minSpareRows: 1,
    contextMenu: [
        'row_above',
        'row_below',
        'remove_row',
        'undo',
        'redo',
        'make_read_only',
        'alignment',
    ],
});

let existingStudentsData = null;
let errorMessagesMap = new Map();
let successfulMessagesMap = new Map();

/**
 * Updates the student data from the spreadsheet when the user clicks "Enroll Students" button.
 * Pushes the output data into the textarea (used for form submission).
 */
function updateEnrollDataDump() {
    const enrollSpreadsheetData = enrollHandsontable.getData();
    const dataPushToTextarea = getUpdatedHeaderString(enrollHandsontable.getColHeader());
    const userDataRows = getUserDataRows(enrollSpreadsheetData);
    $('#enrollstudents').text(userDataRows === ''
            ? '' : dataPushToTextarea + userDataRows); // only pushes header string if userDataRows is not empty
}

/**
 * Compares the current updated data in dataHandsontable with the latest valid copy of existing students' data.
 * The rows that are different would be marked as student entries to update in the 'massupdatestudents' textarea.
 */
function updateExistingStudentsDataDump() {
    $('#massupdatestudents').text(
            getUpdatedStudentRows(dataHandsontable.getData(), existingStudentsData));
}

/**
 * Loads existing student data into the spreadsheet interface.
 */
function loadExistingStudentsData(studentsData) {
    dataHandsontable.loadData(ajaxDataToHandsontableData(studentsData, dataHandsontable.getColHeader()));
}

/**
 * Gets list of student data through an AJAX request.
 * @returns {Promise} the state of the result from the AJAX request
 */
function getAjaxStudentList(displayIcon) {
    return new Promise((resolve, reject) => {
        const $spreadsheetForm = $('#student-data-spreadsheet-form');
        $.ajax({
            type: 'POST',
            url: '/page/instructorCourseEnrollAjaxPage',
            cache: false,
            data: {
                courseid: $spreadsheetForm.children(`input[name="${ParamsNames.COURSE_ID}"]`).val(),
                user: $spreadsheetForm.children(`input[name="${ParamsNames.USER_ID}"]`).val(),
            },
            beforeSend() {
                displayIcon.html('<img height="25" width="25" src="/images/ajax-preload.gif">');
            },
        })
                .done(resolve)
                .fail(reject);
    });
}

/**
 * Updates list of student data through an AJAX request.
 * @returns {Promise} the state of the result from the AJAX request
 */
function getAjaxUpdateStudentList() {
    return new Promise((resolve, reject) => {
        const $spreadsheetForm = $('#student-data-spreadsheet-form');
        $.ajax({
            type: 'POST',
            url: '/page/instructorCourseEnrollAjaxUpdatePage',
            cache: false,
            data: {
                courseid: $spreadsheetForm.children(`input[name="${ParamsNames.COURSE_ID}"]`).val(),
                massupdatestudents: $spreadsheetForm.find(`#massupdatestudents`).val(),
            },
        })
                .done(resolve)
                .fail(reject);
    });
}

/**
 * Displays the modal box when the user clicks the 'Update' button.
 * User is given an option to resend past session links to new emails if existing emails are being updated.
 */
function showUpdateModalBox(submitText, event) {
    event.preventDefault();
    const isOpenOrPublishedEmailSentInThisCourse = $('#openorpublishedemailsent').val();
    let newEmailList = '';
    if (submitText !== '') {
        newEmailList = getNewEmailList(submitText);
    }

    const yesCallback = function () {
        $('[name=\'sessionsummarysendemail\']').val(true);
        processAjaxUpdateData();
    };
    const noCallback = function () {
        $('[name=\'sessionsummarysendemail\']').val(false);
        processAjaxUpdateData();
    };
    const okCallback = function () {
        $('[name=\'sessionsummarysendemail\']').val(false);
        processAjaxUpdateData();
    };

    let messageText = `Updating any changes will result in some existing responses from this student to be deleted.
    You may download the data before you make the changes.`;

    if (newEmailList === '' || isOpenOrPublishedEmailSentInThisCourse === false) {
        showModalConfirmation('Confirm update changes', messageText,
                okCallback, null, null, null, BootstrapContextualColors.INFO);
    } else {
        messageText += `<br><br>Do you want to resend past session links of this course
                        to the following ${newEmailList.split('<br>').length} new email(s)?<br>
                        ${newEmailList}`;
        showModalConfirmationWithCancel('Confirm update changes', messageText,
                yesCallback, noCallback, null, 'Yes, save changes and resend links',
                'No, just save the changes', 'Cancel', BootstrapContextualColors.INFO);
    }
}

/**
 * Function that does post-processing after submitting an AJAX request to update students.
 */
function processAjaxUpdateData() {
    getAjaxUpdateStudentList()
            .then((data) => {
                if (data.statusMessagesToUser.length === 1
                        && data.statusMessagesToUser[0].text === Const.StatusMessages.MASS_UPDATE_LINE_EMPTY) {
                    clearStatusMessages();
                    appendNewStatusMessage(Const.StatusMessages.MASS_UPDATE_LINE_EMPTY,
                            BootstrapContextualColors[data.statusMessagesToUser[0].color]);
                } else { // successful update of students
                    clearStatusMessages();
                    updateDataHandsontableCellSettings(resetDefaultViewRenderer);
                    const oldEmailColumnIndex = 3;

                    // Updates any error or successful messages to process later
                    if (!jQuery.isEmptyObject(data.errorUpdatedLines)) {
                        for (let k of Object.keys(data.errorUpdatedLines)) {
                            errorMessagesMap.set(k, data.errorUpdatedLines[k]);
                        }
                    }
                    if (!jQuery.isEmptyObject(data.successfulUpdatedLines)) {
                        for (let k of Object.keys(data.successfulUpdatedLines)) {
                            successfulMessagesMap.set(k, data.successfulUpdatedLines[k]);
                        }
                    }
                    // Updates current existing students' spreadsheet data to reflect the changes user updated
                    dataHandsontable.loadData(dataHandsontable.getData().map((row, index) => {

                        // Maps the row index to the corresponding error message
                        if (errorMessagesMap.has(row[oldEmailColumnIndex])) {
                            errorMessagesMap.set(index, errorMessagesMap.get(row[oldEmailColumnIndex]));
                        }

                        if (successfulMessagesMap.size !== 0
                                && successfulMessagesMap.has(row[oldEmailColumnIndex])) {

                            // Replaces the old row in existing students' data with the updated student row (verified in backend)
                            existingStudentsData[index] = successfulMessagesMap.get(row[oldEmailColumnIndex]).split('|');
                            existingStudentsData[index].push(null);

                            // Maps the row index to the corresponding success message
                            successfulMessagesMap.set(index,
                                    JSON.parse($('[name=\'sessionsummarysendemail\']').val())
                                            ? "Student successfully updated. Past session links have been sent to the new email."
                                            : "Student successfully updated.");
                            return successfulMessagesMap.get(row[oldEmailColumnIndex]).split('|');
                        } else { // Returns the  existing row if the student data entry is not successful/have no changes
                            return row;
                        }
                    }));
                    updateDataHandsontableCellSettings(statusMessageRowsRenderer);
                }
            }).catch(() => {
                const $panelHeading = $('#existing-data-spreadsheet');
                const displayIcon = $panelHeading.children('.display-icon');
                displayErrorExecutingAjaxUpdate(displayIcon);
            });
}

/**
 * Function to update the datahandsontable cell settings according to a custom renderer.
 */
function updateDataHandsontableCellSettings(targetRenderer) {
    dataHandsontable.updateSettings({
        cells: function (row) {
            let cellProperties = {};
            cellProperties.renderer = targetRenderer; // uses function directly
            return cellProperties;
        }
    });
}

/**
 * Custom renderer to reset any cell styles and tooltips in the current Handsontable instance.
 */
function resetDefaultViewRenderer(instance, td) {
    Handsontable.renderers.TextRenderer.apply(this, arguments);
    $(td).tooltip('destroy');
    td.style.background = '#FFFFFF';
}

/**
 * Custom renderer to update the rows of the Handsontable instance to the respective cell styles and tooltips.
 */
function statusMessageRowsRenderer(instance, td, row, col, prop, value, cellProperties) {
    Handsontable.renderers.TextRenderer.apply(this, arguments);
    let title = '';

    if (errorMessagesMap.has(row)) {
        td.style.background = '#ff6666';
        title = errorMessagesMap.get(row);
    } else if (successfulMessagesMap.has(row)) {
        td.style.background = '#7CFC00';
        title = successfulMessagesMap.get(row);
    } else {
        td.style.background = '#FFFFFF';
        return;
    }
    $(td).tooltip({
            trigger: 'hover active',
            title: title,
            placement: 'auto',
            container: 'body',
            template: '<div class="tooltip" role="tooltip"><div class="tooltip-arrow"></div><div class="tooltip-inner"></div></div>'
    });
}

/**
 * Updates settings of dataHandsontable.
 * "Email" column is read-only.
 */
function updateDataHandsontableColumnHeaders() {
    dataHandsontable.updateSettings({
        columns: [
            { colHeader: 'Section' },
            { colHeader: 'Team' },
            { colHeader: 'Name' },
            { colHeader: 'Email', readOnly: true },
            { colHeader: 'Comments' },
            { colHeader: 'Fill in the new email here' },
        ],
    });
}

/**
 * Expands "Existing students" panel and loads existing students' data (if spreadsheet is not empty)
 * into the spreadsheet interface. Spreadsheet interface would be shown after expansion.
 * The panel will be collapsed otherwise if the spreadsheet interface is already shown.
 */
function expandCollapseExistingStudentsPanel() {
    const $panelHeading = $(this);
    const panelName = 'Existing students';
    const panelCollapse = $panelHeading.parent().children('.panel-collapse');
    const displayIcon = $panelHeading.children('.display-icon');
    const toggleChevron = $panelHeading.parent().find('.glyphicon-chevron-down, .glyphicon-chevron-up');

    // perform AJAX only if existing students' spreadsheet is empty
    if (getSpreadsheetLength(dataHandsontable.getData()) === 0) {
        getAjaxStudentList(displayIcon)
                .then((data) => {
                    updateDataHandsontableColumnHeaders();
                    if (data.students.length === 0) {
                        displayNoExistingStudents(displayIcon);
                    } else {
                        loadExistingStudentsData(data.students);
                        toggleStudentsPanel($panelHeading, panelCollapse,
                                            displayIcon, toggleChevron, panelName);
                        // needed as the view is buggy after collapsing the panel
                        dataHandsontable.render();
                        // keep a copy of the current existing students data upon AJAX load
                        existingStudentsData = dataHandsontable.getData();
                    }
                }).catch(() => {
                    displayErrorExecutingAjax(displayIcon);
                });
    } else {
        toggleStudentsPanel($panelHeading, panelCollapse, displayIcon, toggleChevron);
        dataHandsontable.render(); // needed as the view is buggy after collapsing the panel
    }
}

/**
 * Expands "New students" panel. Spreadsheet interface would be shown after expansion.
 * The panel will be be collapsed otherwise if the spreadsheet interface is already shown.
 */
function expandCollapseNewStudentsPanel() {
    const $panelHeading = $(this);
    const panelName = 'New students';
    const panelCollapse = $panelHeading.parent().children('.panel-collapse');
    const displayIcon = $panelHeading.children('.display-icon');
    const toggleChevron = $panelHeading.parent().find('.glyphicon-chevron-down, .glyphicon-chevron-up');

    toggleStudentsPanel($panelHeading, panelCollapse, displayIcon, toggleChevron, panelName);
    enrollHandsontable.render();
}

$(document).ready(() => {
    prepareInstructorPages();
    $('#enroll-spreadsheet').on('click', expandCollapseNewStudentsPanel);
    $('#enroll-spreadsheet').trigger('click');

    $('#existing-data-spreadsheet').click(expandCollapseExistingStudentsPanel);

    if ($('#enrollstudents').val()) {
        const allData = $('#enrollstudents').val().split('\n'); // data in the table including column headers (string format)

        const columnHeaders = allData[0].split('|');
        enrollHandsontable.updateSettings({
            colHeaders: columnHeaders,
        });

        const spreadsheetDataRows = allData.slice(1);
        if (spreadsheetDataRows.length > 0) {
            const data = getUpdatedData(spreadsheetDataRows);
            enrollHandsontable.loadData(data); // Reset all cells in the grid to contain data from the data array
        }
    }

    $('#button_add_empty_rows').click(() => {
        const emptyRowsCount = $('#number-of-rows').val();
        enrollHandsontable.alter('insert_row', null, emptyRowsCount);
    });

    $('#button_enroll').click(updateEnrollDataDump);
    $('#button_updatestudents').bind('click', (event) => {
        updateExistingStudentsDataDump();
        showUpdateModalBox($('#massupdatestudents').text(), event);
        errorMessagesMap.clear();
        successfulMessagesMap.clear();
    });
});
