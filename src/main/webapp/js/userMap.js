/**
 * Parse and Clean the user data, pruning invalid and duplicate entries
 * @param  {Array} userData original user data, each entry in the format of ["name, country"]
 * @return {Array} result   cleaned user data, each entry in the format of [name, country]
 */
function parseAndCleanUserData(userData) {
    var institutionList = [];
    var result = [];
    for (var i = 0; i < userData.length; i++) {
        var entry = parseAndTrimEntry(userData[i]);
        if (isEntryValid(entry) && !isEntryDuplicate(entry, institutionList)) {
            result.push(entry);
            institutionList.push(entry[0]);
        }
    }
    return result;
}

/**
 * Parse the entry into format of [name, country], trim the entry to remove extra spaces
 * @param  {Array} rawEntry entry to be parsed and trimmed, in the format of ["name, country"]
 * @return {Array} fields   parsed and trimmed entry, in the format of [name, country]
 */
function parseAndTrimEntry(rawEntry) {
    var fields = rawEntry[0].split(',');
    for (var i = 0; i < fields.length; i++) {
        fields[i] = fields[i].trim();
    }
    // Remove the middle field (usually the state or city) at index 1 if the array length is 3
    if (fields.length === 3) {
        fields.splice(1, 1);
    }
    return fields;
}

/**
 * Check if an institution entry is valid
 * @param  {Array}   entry  array representing the institution, in the format of [name, country]
 * @return {Boolean} result if the entry is valid
 */
function isEntryValid(entry) {
    if (entry.length !== 2) {
        return false;
    } else if (entry[0] === '' || entry[1] === '') {
        return false;
    }
    return true;
}

/**
 * Check if an institution entry is a duplicate
 * @param  {Array}   entry              array representing the institution, in the format of [name, country]
 * @param  {Array}   institutionList    representing the institution, in the format of [name, country]
 * @return {Boolean} result             if the entry is a duplicate
 */
function isEntryDuplicate(entry, institutionList) {
    if (institutionList.indexOf(entry[0]) !== -1) {
        return true;
    }
    return false;
}

document.addEventListener('DOMContentLoaded', function(event) {
    // based on example from https://github.com/markmarkoh/datamaps/blob/master/src/examples/highmaps_world.html
    // Country code: https://en.wikipedia.org/wiki/ISO_3166-1_alpha-3

    var countriesObj = {};
    var countriesArr = [];

    var userDataCleaned = parseAndCleanUserData(userData);

    userDataCleaned.forEach(function(entry) {
        var countryName = entry[entry.length - 1];
        var countryCode = getCountryCode(countryName);
        if (countryCode != null) {
            countriesObj[countryCode] = countriesObj[countryCode] ? countriesObj[countryCode] + 1 : 1;
        }
    });

    for (var countryCode in countriesObj) {
        if (countriesObj.hasOwnProperty(countryCode)) {
            countriesArr.push([countryCode, countriesObj[countryCode]]);
        }
    }

    d3.select('#totalCount').text(userDataCleaned.length + ' institutions from ' + countriesArr.length + ' countries');

    // Data format example
    // var series = [
    //     ['BLR', 1], ['BLZ', 1], ['RUS', 1], ['RWA', 1], ['SRB', 1], ['TLS', 1],
    //     ['REU', 1], ['TKM', 1], ['TJK', 1], ['ROU', 1], ['TKL', 1], ['GNB', 1],
    //     ['GUM', 1], ['GTM', 1], ['SGS', 1], ['GRC', 1], ['GNQ', 1], ['GLP', 1],
    //     ['JPN', 1], ['GUY', 1], ['GGY', 1], ['GUF', 1], ['GEO', 1], ['GRD', 1],
    //     ['GBR', 1], ['GAB', 1], ['SLV', 1], ['GIN', 1], ['GMB', 1], ['SGP', 1],
    //     ['IND', 1], ['CHN', 1], ['USA', 1], ['CAN', 1]];

    var dataset = {};
    var onlyValues = countriesArr.map(function(obj) {
        return obj[1];
    });
    var minValue = Math.min.apply(null, onlyValues);
    var maxValue = Math.max.apply(null, onlyValues);
    var paletteScale = d3.scale.linear()
            .domain([minValue, maxValue])
            .range(['#428bca', '#428bca']); // Choropleth effect: .range(['#C1F0F6","#4895AE"]);
    countriesArr.forEach(function(item) {
        var iso = item[0];
        var value = item[1];
        dataset[iso] = { numOfInstitutions: value, fillColor: paletteScale(value) };
    });

    // Word map
    new Datamap({
        scope: 'world',
        element: document.getElementById('container'),
        setProjection: function(element) {
            var projection = d3.geo.mercator()
              .center([0, 20])
              .rotate([-5, 0])
              .scale(130)
              .translate([element.offsetWidth / 2, element.offsetHeight / 2]);
            var path = d3.geo.path()
                .projection(projection);
            return { path: path, projection: projection };
        },
        // Set height and width to avoid overlapping with border
        height: 500,
        width: 800,
        // countries don't listed in dataset will be painted with this color
        fills: { defaultFill: '#F5F5F5' },
        data: dataset,
        geographyConfig: {
            borderColor: '#DEDEDE',
            borderWidth: 0.7,
            // don't change color on mouse hover
            highlightFillColor: function(geo) {
                return geo['fillColor'] || '#F5F5F5';
            },
            dataUrl: '/js/lib/world.hires.topo.json',
            // only change border
            highlightBorderColor: '#a4a4a4',
            highlightBorderWidth: 1,
            highlightBorderOpacity: 1,
            // show desired information in tooltip
            popupTemplate: function(geo, data) {
                // don't show tooltip if country don't present in dataset
                if (!data) {
                    return;
                }
                // tooltip content
                return '<div class="hoverinfo">'
                         + '<p>'
                             + '<span class="bold">'
                             + geo.properties.name
                             + '</span>'
                             + '<br>'
                             + 'Institution(s): '
                             + data.numOfInstitutions
                         + '</p>'
                     + '</div>';
            }
        }
    });
});
