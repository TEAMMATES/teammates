import { showModalAlert } from '../common/bootboxWrapper.es6';
import { StatusType } from '../common/const.es6';
import { prepareInstructorPages } from '../common/instructor.es6';

function isUserTyping(str) {
    return str.indexOf('\t') === -1 && str.indexOf('|') === -1;
}

window.isUserTyping = isUserTyping;

const loadUpFunction = function () {
    const typingErrMsg = 'Please use | character ( shift+\\ ) to seperate fields, or copy from your existing spreadsheet.';
    let notified = false;

    const ENTER_KEYCODE = 13;
    let enrolTextbox = $('#enrollstudents');
    if (enrolTextbox.length) {
        enrolTextbox = enrolTextbox[0];
        $(enrolTextbox).keydown((e) => {
            const keycode = e.which || e.keyCode;
            if (keycode === ENTER_KEYCODE) {
                if (isUserTyping(e.target.value) && !notified) {
                    notified = true;
                    showModalAlert('Invalid separator', typingErrMsg, null, StatusType.WARNING);
                }
            }
        });
    }
};

if (window.addEventListener) {
    window.addEventListener('load', loadUpFunction);
} else {
    window.attachEvent('load', loadUpFunction);
}

$(document).ready(() => {
    prepareInstructorPages();
});

export {
    isUserTyping,
};

/* Handsontable Implementation code starts here */
const container = document.getElementById('spreadsheet');

$('#toggle-interface').click((e) => {
    $(e.target).text($(e.target).text() === 'Textarea Interface'
        ? 'Spreadsheet Interface' : 'Textarea Interface');
    $('.student-data-textarea, #student-data-spreadsheet').toggle();
});

const hot = new Handsontable(container, {
    rowHeaders: true,
    colHeaders: ['Section', 'Team', 'Name', 'Email', 'Comment'],
    contextMenu: true,
    columnSorting: true,
    className: 'htCenter',
    manualColumnResize: true,
    sortIndicator: true,
    maxRows: 100,
    maxCols: 100,
    stretchH: 'all',
    minSpareRows: 2,
    manualColumnMove: true,
});

function updateHeaderOrder() {
    const colHeader = hot.getColHeader();
    let headerString = '';
    for (let itr = 0; itr < colHeader.length; itr += 1) {
        headerString += colHeader[itr];
        if (itr < colHeader.length - 1) {
            headerString += ' | ';
        }
    }
    headerString += '\n';
    return headerString;
}

function updateDataDump() {
    const spreadsheetData = hot.getData();
    let dataPushToTextarea = updateHeaderOrder();
    let i = 0;
    let j = 0;
    let countEmptyColumns = 0;
    let rowData = '';
    for (i = 0; i < spreadsheetData.length; i += 1) {
        countEmptyColumns = 0; rowData = '';
        for (j = 0; j < spreadsheetData[i].length; j += 1) {
            rowData += spreadsheetData[i][j] !== null ? spreadsheetData[i][j] : '';
            if ((spreadsheetData[i][j] === '' || spreadsheetData[i][j] === null)
             && j < spreadsheetData[i].length - 1) {
                countEmptyColumns += 1;
            }
            if (j < spreadsheetData[i].length - 1) {
                rowData += ' | ';
            }
        }
        if (countEmptyColumns < spreadsheetData[i].length - 1) {
            dataPushToTextarea += rowData;
            dataPushToTextarea += '\n';
        }
    }
    $('#enrollstudents').text(dataPushToTextarea);
}

Handsontable.hooks.add('afterChange', () => {
    updateDataDump();
});

Handsontable.hooks.add('afterColumnMove', () => {
    updateDataDump();
});
