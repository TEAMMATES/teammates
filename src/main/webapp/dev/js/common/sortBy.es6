import { isDate, isNumber } from './helper.es6';

/**
 * The base comparator (ascending)
 *
 * @param x
 * @param y
 * @returns
 */
function sortBase(x, y) {
    // Text sorting
    if (x < y) {
        return -1;
    }
    return x > y ? 1 : 0;
}

/**
 * Comparator for numbers (integer, double) (ascending)
 *
 * @param x
 * @param y
 * @returns
 */
function sortNum(x, y) {
    return x - y;
}

/**
 * Comparator for date. Allows for the same format as isDate()
 *
 * @param x
 * @param y
 * @returns 1 if Date x is after y, 0 if same and -1 if before
 */
function sortDate(x, y) {
    const x0 = Date.parse(x);
    const y0 = Date.parse(y);
    if (x0 > y0) {
        return 1;
    }
    return x0 < y0 ? -1 : 0;
}

// http://stackoverflow.com/questions/7558182/sort-a-table-fast-by-its-first-column-with-javascript-or-jquery
/**
 * Sorts a table based on certain column and comparator
 *
 * @param oneOfTableCell
 *     One of the table cell
 * @param colIdx
 *     The column index (1-based) as key for the sort
 * @param ascending
 *     if this is true, it will be ascending order, else it will be descending order
 */
function sortTable(oneOfTableCell, colIdx, comp, ascending, row) {
    // Get the table
    let $table = $(oneOfTableCell);

    if (!$table.is('table')) {
        $table = $table.parents('table');
    }

    let columnType = 0;
    let store = [];
    const $RowList = $('tr', $table);
    // For date comparisons in instructor home page we should use
    // the tool-tip value instead of display text since display text does not contain the year.
    const shouldConsiderToolTipYear = comp && comp.toString().includes('instructorHomeDateComparator');

    // Iterate through column's contents to decide which comparator to use
    let textToCompare;
    for (let i = row; i < $RowList.length; i += 1) {
        if ($RowList[i].cells[colIdx - 1] !== undefined) {
            // $.trim trims leading/trailing whitespaces
            // jQuery(...).text() works like .innerText, but works in Firefox (.innerText does not)
            // $RowList[i].cells[colIdx - 1] is where we get the table cell from
            // If shouldConsiderToolTipYear is true, we consider the tooltip value instead of innerText
            textToCompare = shouldConsiderToolTipYear
                    ? $.trim($($RowList[i].cells[colIdx - 1]).find('span').attr('data-original-title'))
                    : $.trim($($RowList[i].cells[colIdx - 1]).text());

            // Store rows together with the innerText to compare
            store.push([textToCompare, $RowList[i], i]);

            if ((columnType === 0 || columnType === 1) && isNumber(textToCompare)) {
                columnType = 1;
            } else if ((columnType === 0 || columnType === 2) && isDate(textToCompare)) {
                columnType = 2;
            } else {
                columnType = 3;
            }
        }
    }

    let comparator = comp;
    if (comparator === null || comparator === undefined) {
        if (columnType === 1) {
            comparator = sortNum;
        } else if (columnType === 2) {
            comparator = sortDate;
        } else {
            comparator = sortBase;
        }
    }

    store.sort((x, y) => {
        const compareResult = ascending ? comparator(x[0].toUpperCase(), y[0].toUpperCase())
                                      : comparator(y[0].toUpperCase(), x[0].toUpperCase());
        if (compareResult === 0) {
            return x[2] - y[2];
        }
        return compareResult;
    });

    // Must rewrap because .get() does not return a jQuery wrapped DOM node
    // and hence does not have the .children() function
    let $tbody = $($table.get(0)).children('tbody');

    if ($tbody.size < 1) {
        $tbody = $table;
    }

    // Must push to target tbody else it will generate a new tbody for the table
    for (let j = 0; j < store.length; j += 1) {
        $tbody.get(0).appendChild(store[j][1]);
    }

    store = null;
}

/**
 * Sorts a table
 * @param divElement
 *     The sort button
 * @param comparator
 *     The function to compare 2 elements
 */
function toggleSort(divElement, comparator) {
    // The column index (1-based) as key for the sort
    const colIdx = $(divElement).parent().children().index($(divElement)) + 1;

    // Row to start sorting from (0-based), set to 1 since <th> occupies the first row
    const row = 1;

    const $selectedDivElement = $(divElement);

    if ($selectedDivElement.attr('class') === 'button-sort-none') {
        sortTable(divElement, colIdx, comparator, true, row);
        $selectedDivElement.parent().find('.button-sort-ascending').attr('class', 'button-sort-none');
        $selectedDivElement.parent().find('.button-sort-descending').attr('class', 'button-sort-none');
        $selectedDivElement.parent().find('.icon-sort').attr('class', 'icon-sort unsorted');
        $selectedDivElement.attr('class', 'button-sort-ascending');
        $selectedDivElement.find('.icon-sort').attr('class', 'icon-sort sorted-ascending');
    } else if ($selectedDivElement.attr('class') === 'button-sort-ascending') {
        sortTable(divElement, colIdx, comparator, false, row);
        $selectedDivElement.attr('class', 'button-sort-descending');
        $selectedDivElement.find('.icon-sort').attr('class', 'icon-sort sorted-descending');
    } else {
        sortTable(divElement, colIdx, comparator, true, row);
        $selectedDivElement.attr('class', 'button-sort-ascending');
        $selectedDivElement.find('.icon-sort').attr('class', 'icon-sort sorted-ascending');
    }
}

/**
 * To get point value from a formatted string
 *
 * @param s
 *     A table cell (td tag) that contains the formatted string
 * @param ditchZero
 *     Whether 0% should be treated as lower than -90 or not
 * @returns
 */
function getPointValue(s, ditchZero) {
    let s0 = s;
    const baseValue = 100;

    if (s0.indexOf('/') !== -1) {
        // magic expressions below as these cases will only be compared with
        // case E +(-)X% (0 <= X <= 100)
        if (s0.indexOf('S') !== -1) {
            return (2 * baseValue) + 1; // Case N/S (feedback contribution not sure)
        }

        return (2 * baseValue) + 2; // Case N/A
    }

    if (s0 === '0%') { // Case 0%
        if (ditchZero) {
            return 0;
        }
        return baseValue;
    }

    s0 = s0.replace('E', '').replace('%', ''); // Case E +(-)X%

    if (s0 === '') {
        return baseValue; // Case E
    }

    return baseValue + parseFloat(s0); // Other typical cases
}

/**
 * Comparator to sort strings in format: E([+-]x%) | N/A | N/S | 0% with
 * possibly a tag that surrounds it.
 *
 * @param a
 * @param b
 */
function sortByPoint(a, b) {
    const a0 = getPointValue(a, true);
    const b0 = getPointValue(b, true);

    if (isNumber(a0) && isNumber(b0)) {
        return sortNum(a0, b0);
    }
    return sortBase(a0, b0);
}

/**
 * Comparator to sort strings in format: [+-]x% | N/A with possibly a tag that
 * surrounds it.
 *
 * @param a
 * @param b
 */
function sortByDiff(a, b) {
    const a0 = getPointValue(a, false);
    const b0 = getPointValue(b, false);

    if (isNumber(a0) && isNumber(b0)) {
        return sortNum(a0, b0);
    }
    return sortBase(a0, b0);
}

export default {
    toggleSort,
    sortByPoint,
    sortByDiff,
};
