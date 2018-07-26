/**
 * Holds Handsontable settings, reference and other information for the spreadsheet interface.
 */
/* global Handsontable:false */
import {
    prepareInstructorPages,
} from '../common/instructor';

import {
    ParamsNames,
} from '../common/const';

import {
    getUpdatedHeaderString,
    getUserDataRows,
    ajaxDataToHandsontableData,
    spreadsheetDataRowsToHandsontableData,
    displayNoExistingStudents,
    displayErrorExecutingAjax,
    getSpreadsheetLength,
    toggleStudentsPanel,
} from '../common/instructorEnroll';

const dataContainer = document.getElementById('existingDataSpreadsheet');
const dataHandsontable = new Handsontable(dataContainer, {
    readOnly: true,
    height: 400,
    autoWrapRow: true,
    preventOverflow: 'horizontal',
    manualColumnResize: true,
    manualRowResize: true,
    rowHeaders: true,
    colHeaders: ['Section', 'Team', 'Name', 'Email', 'Comments'],
    columnSorting: true,
    sortIndicator: true,
    minRows: 20,
    maxCols: 5,
    stretchH: 'all',
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

const enrollErrorMessagesMap = new Map();

/**
 * Function to update the enrollHandsontable cell settings according to a custom renderer.
 */
function updateEnrollHandsontableCellSettings(targetRenderer) {
    enrollHandsontable.updateSettings({
        cells: () => {
            const cellProperties = {};
            cellProperties.renderer = targetRenderer; // uses function directly
            return cellProperties;
        },
    });
}

/**
 * Custom renderer to reset any cell styles and tooltips in the current Handsontable instance.
 */
/* eslint-disable prefer-rest-params */
function resetDefaultViewRenderer(instance, td) {
    Handsontable.renderers.TextRenderer.apply(this, arguments);
    $(td).tooltip('destroy');
    td.style.background = '#FFFFFF';
}

/**
 * Custom renderer to update the rows of the Handsontable instance to the respective cell styles and tooltips.
 */
function statusMessageRowsRenderer(instance, td, row) {
    Handsontable.renderers.TextRenderer.apply(this, arguments);
    let text = '';

    if (enrollErrorMessagesMap.has(row)) {
        td.style.background = '#ff6666';
        text = enrollErrorMessagesMap.get(row);
    } else {
        td.style.background = '#FFFFFF';
        return;
    }
    $(td).tooltip({
        trigger: 'hover active',
        title: text,
        placement: 'auto',
        container: 'body',
        template: '<div class="tooltip" role="tooltip"><div class="tooltip-arrow">'
        + '</div><div class="tooltip-inner"></div></div>',
    });
}

/**
 * Updates the student data from the spreadsheet when the user clicks "Enroll Students" button.
 * Pushes the output data into the textarea (used for form submission).
 */
function updateDataDump() {
    const spreadsheetData = enrollHandsontable.getData();
    const dataPushToTextarea = getUpdatedHeaderString(enrollHandsontable.getColHeader());
    const userDataRows = getUserDataRows(spreadsheetData);
    $('#enrollstudents').text(userDataRows === ''
            ? '' : dataPushToTextarea + userDataRows); // only pushes header string if userDataRows is not empty
}

/**
 * Loads existing student data into the spreadsheet interface.
 */
function loadExistingStudentsData(studentsData) {
    dataHandsontable.loadData(ajaxDataToHandsontableData(
            studentsData, dataHandsontable.getColHeader()));
}

/**
 * Gets list of student data through an AJAX request.
 * @returns {Promise} the state of the result from the AJAX request
 */
function getAjaxStudentList(displayStatus) {
    return new Promise((resolve, reject) => {
        const $spreadsheetForm = $('#student-spreadsheet-form');
        $.ajax({
            type: 'POST',
            url: '/page/instructorCourseEnrollAjaxPage',
            cache: false,
            data: {
                courseid: $spreadsheetForm.children(`input[name="${ParamsNames.COURSE_ID}"]`).val(),
            },
            beforeSend() {
                displayStatus.html('<img height="25" width="25" src="/images/ajax-preload.gif">');
            },
        })
                .done(resolve)
                .fail(reject);
    });
}

/**
 * Gets enroll status through an AJAX request.
 * @returns {Promise} the state of the result from the AJAX request
 */
function getAjaxEnrollStatus() {
    return new Promise((resolve, reject) => {
        const $spreadsheetForm = $('#student-spreadsheet-form');
        $.ajax({
            type: 'POST',
            url: '/page/instructorCourseEnrollAjaxEnrollStatusPage',
            cache: false,
            data: {
                courseid: $spreadsheetForm.children(`input[name='${ParamsNames.COURSE_ID}']`).val(),
                enrollstudents: $spreadsheetForm.find('#enrollstudents').val(),
            },
        })
                .done(resolve)
                .fail(reject);
    });
}

/**
 * Handles how the panels are displayed, including rendering the spreadsheet interface.
 */
function adjustStudentsPanelView($panelHeading, panelCollapse,
        displayStatus, toggleChevron) {
    toggleStudentsPanel($panelHeading, panelCollapse,
            displayStatus, toggleChevron);
    dataHandsontable.render(); // needed as the view is buggy after collapsing the panel
}

/**
 * Adds all error messages returned from the backend into a map.
 */
function addEnrollErrorMessages(enrollErrorLines) {
    // Updates any error messages to process later
    if (!jQuery.isEmptyObject(enrollErrorLines)) {
        Object.keys(enrollErrorLines).forEach(key => (
                enrollErrorMessagesMap.set(key, enrollErrorLines[key])
        ));
    }

    enrollHandsontable.getData().forEach((row, index) => {
        const currRowLine = row.join('|');
        if (enrollErrorMessagesMap.has(currRowLine)) {
            enrollErrorMessagesMap.set(index,
                    enrollErrorMessagesMap.get(currRowLine));
        }
    });
}
/**
 * Expands "Existing students" panel and loads existing students' data (if spreadsheet is not empty)
 * into the spreadsheet interface. Spreadsheet interface would be shown after expansion.
 * The panel will be collapsed otherwise if the spreadsheet interface is already shown.
 */
function expandCollapseExistingStudentsPanel() {
    const $panelHeading = $(this);
    const panelCollapse = $panelHeading.parent().children('.panel-collapse');
    const displayStatus = $panelHeading.children('.display-status');
    const toggleChevron = $panelHeading.parent().find('.glyphicon-chevron-down, .glyphicon-chevron-up');

    // perform AJAX only if existing students' spreadsheet is empty
    if (getSpreadsheetLength(dataHandsontable.getData()) === 0) {
        getAjaxStudentList(displayStatus)
                .then((data) => {
                    if (data.students.length === 0) {
                        displayNoExistingStudents(displayStatus);
                    } else {
                        loadExistingStudentsData(data.students);
                        adjustStudentsPanelView($panelHeading, panelCollapse,
                                displayStatus, toggleChevron);
                    }
                }).catch(() => {
                    displayErrorExecutingAjax(displayStatus);
                });
    } else {
        adjustStudentsPanelView($panelHeading, panelCollapse,
                displayStatus, toggleChevron);
    }
}

/**
 * Expands "New students" panel. Spreadsheet interface would be shown after expansion, including its affiliated buttons.
 * The panel will be collapsed otherwise if the spreadsheet interface is already shown.
 */
function expandCollapseNewStudentsPanel() {
    const $panelHeading = $(this);
    const panelCollapse = $panelHeading.parent().children('.panel-collapse');
    const displayStatus = $panelHeading.children('.display-icon');
    const toggleChevron = $panelHeading.parent().find('.glyphicon-chevron-down, .glyphicon-chevron-up');

    if (toggleStudentsPanel($panelHeading, panelCollapse, displayStatus, toggleChevron)) { // if panel is expanded
        $('.enroll-students-spreadsheet-buttons').show();
    } else {
        $('.enroll-students-spreadsheet-buttons').hide();
    }
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
            const data = spreadsheetDataRowsToHandsontableData(spreadsheetDataRows);
            enrollHandsontable.loadData(data); // Reset all cells in the grid to contain data from the data array
        }
    }

    $('#button_add_empty_rows').click(() => {
        const emptyRowsCount = $('#number-of-rows').val();
        enrollHandsontable.alter('insert_row', null, emptyRowsCount);
    });

    $('#student-spreadsheet-form').submit(updateDataDump);
});
