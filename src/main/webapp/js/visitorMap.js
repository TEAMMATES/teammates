document.addEventListener("DOMContentLoaded", function(event) {
    // based on example from https://github.com/markmarkoh/datamaps/blob/master/src/examples/highmaps_world.html
    // Country code: https://en.wikipedia.org/wiki/ISO_3166-1_alpha-3
    var series = [
        ["BLR",75],["BLZ",43],["RUS",50],["RWA",88],["SRB",21],["TLS",43],
        ["REU",21],["TKM",19],["TJK",60],["ROU",4],["TKL",44],["GNB",38],
        ["GUM",67],["GTM",2],["SGS",95],["GRC",60],["GNQ",57],["GLP",53],
        ["JPN",59],["GUY",24],["GGY",4],["GUF",21],["GEO",42],["GRD",65],
        ["GBR",14],["GAB",47],["SLV",15],["GIN",19],["GMB",63],["SGP",100],
        ["IND",95],["CHN",16],["USA",93],["CAN",78]
        ];

    var dataset = {};
    var onlyValues = series.map(function(obj){ return obj[1]; });
    var minValue = Math.min.apply(null, onlyValues);
    var maxValue = Math.max.apply(null, onlyValues);
    var paletteScale = d3.scale.linear()
            .domain([minValue,maxValue])
            .range(["#C1F0F6","#4895AE"]);
    series.forEach(function(item){ //
        var iso = item[0];
        var value = item[1];
        dataset[iso] = { numOfVisitors: value, fillColor: paletteScale(value) };
    });
    // render map
    new Datamap({
        element: document.getElementById('container'),
        // projection: 'mercator', // big world map
        setProjection: function(element) {
            var projection = d3.geo.mercator()
              .center([0, 0])
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
                if (!data) { return ; }
                // tooltip content
                return ['<div class="hoverinfo">',
                    '<p><span class="bold">', geo.properties.name, '</span>',
                    '<br>Visitors: ', data.numOfVisitors,
                    '</p></div>'].join('');
            }
        }
    });
});