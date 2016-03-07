document.addEventListener("DOMContentLoaded", function(event) {
    // based on example from https://github.com/markmarkoh/datamaps/blob/master/src/examples/highmaps_world.html
    // Country code: https://en.wikipedia.org/wiki/ISO_3166-1_alpha-3

    var countriesObj = {};
    var countriesArr = [];

    userData.forEach(function(user){
        fields = user[0].split(',');
        if (fields.length === 2 && fields[1] != '') {
            var countryName = fields[1].trim();
            countriesObj[countryName] = countriesObj[countryName] ? countriesObj[countryName] + 1 : 1;
        }
    })

    for (var countryName in countriesObj) {
        if (countriesObj.hasOwnProperty(countryName)) {
            var countryCode = getCountryCode(countryName);
            if (countryCode != null) {
                countriesArr.push([countryCode, countriesObj[countryName]]);    
            }
        }
    }

    // Data format example
    // var series = [
    //     ["BLR",1],["BLZ",1],["RUS",1],["RWA",1],["SRB",1],["TLS",1],
    //     ["REU",1],["TKM",1],["TJK",1],["ROU",1],["TKL",1],["GNB",1],
    //     ["GUM",1],["GTM",1],["SGS",1],["GRC",1],["GNQ",1],["GLP",1],
    //     ["JPN",1],["GUY",1],["GGY",1],["GUF",1],["GEO",1],["GRD",1],
    //     ["GBR",1],["GAB",1],["SLV",1],["GIN",1],["GMB",1],["SGP",1],
    //     ["IND",1],["CHN",1],["USA",1],["CAN",1]
    //     ];

    var dataset = {};
    var onlyValues = countriesArr.map(function(obj) { 
        return obj[1]; 
    });
    var minValue = Math.min.apply(null, onlyValues);
    var maxValue = Math.max.apply(null, onlyValues);
    var paletteScale = d3.scale.linear()
            .domain([minValue,maxValue])
            .range(["#4895AE","#4895AE"]); // Choropleth effect: .range(["#C1F0F6","#4895AE"]);
    countriesArr.forEach(function(item) {
        var iso = item[0];
        var value = item[1];
        dataset[iso] = { numOfInstitutions: value, fillColor: paletteScale(value) };
    });

    // render map
    new Datamap({
        element: document.getElementById('container'),
        setProjection: function(element) {
            var projection = d3.geo.mercator()
              .center([0, 20])
              .rotate([0, 0])
              .scale(120)
              .translate([element.offsetWidth / 2, element.offsetHeight / 2]);
            var path = d3.geo.path()
                .projection(projection);
            return {path: path, projection: projection};
        },
        // countries don't listed in dataset will be painted with this color
        fills: { defaultFill: '#F5F5F5' },
        data: dataset,
        geographyConfig: {
            borderColor: '#DEDEDE',
            // don't change color on mouse hover
            highlightFillColor: function(geo) {
                return geo['fillColor'] || '#F5F5F5';
            },
            // only change border
            highlightBorderColor: '#B7B7B7',
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
                    + '<p><span class="bold">'
                    + geo.properties.name
                    + '</span>'
                    + '<br>'
                    + 'Institution(s): '
                    + data.numOfInstitutions
                    + '</p></div>';
            }
        }
    });
});