
function handleError() {
    var contentHolder = d3.select('#contentHolder');
    contentHolder.html('');
    contentHolder.append('p')
        .text('An error has occured in getting data, please try reloading.');
    contentHolder.append('p')
        .html('If the problem persists after a few retries, please <a href="/contact.jsp">contact us</a>.');
}

function handleData(err, countryCoordinates, userData) {
    // based on example from https://github.com/markmarkoh/datamaps/blob/master/src/examples/highmaps_world.html
    // Country code: https://en.wikipedia.org/wiki/ISO_3166-1_alpha-3
    if (err) {
        handleError();
        return;
    }
    var countriesObj = {};
    var countriesArr = [];
    userData.forEach(function(entry) {
        var countryName = entry[entry.length - 1];
        var countryCode = getCountryCode(countryName);
        if (countryCode !== undefined) {
            countriesObj[countryCode] = countriesObj[countryCode] ? countriesObj[countryCode] + 1 : 1;
        }
    });

    for (var countryCode in countriesObj) {
        if (countriesObj.hasOwnProperty(countryCode)) {
            countriesArr.push([countryCode, countriesObj[countryCode]]);
        }
    }

    // set the institution count in the page
    document.getElementById('totalUserCount').innerHTML = userData.length;
    // set the country count in the page
    document.getElementById('totalCountryCount').innerHTML = countriesArr.length;
    
    // Data format example
    // var series = [
    //     ['BLR', 1], ['BLZ', 1], ['RUS', 1], ['RWA', 1], ['SRB', 1], ['TLS', 1],
    //     ['REU', 1], ['TKM', 1], ['TJK', 1], ['ROU', 1], ['TKL', 1], ['GNB', 1],
    //     ['GUM', 1], ['GTM', 1], ['SGS', 1], ['GRC', 1], ['GNQ', 1], ['GLP', 1],
    //     ['JPN', 1], ['GUY', 1], ['GGY', 1], ['GUF', 1], ['GEO', 1], ['GRD', 1],
    //     ['GBR', 1], ['GAB', 1], ['SLV', 1], ['GIN', 1], ['GMB', 1], ['SGP', 1],
    //     ['IND', 1], ['CHN', 1], ['USA', 1], ['CAN', 1]];

    var dataset = {};
    var pins = [];
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
        var coordinates = countryCoordinates[iso];
        dataset[iso] = {
            numOfInstitutions: value,
            fillColor: paletteScale(value)
        };
        pins.push({
            name: getCountryNameByCode(iso),
            numOfInstitutions: value,
            latitude: coordinates.lat,
            longitude: coordinates.lon
        });
    });

    // Word map
    var map = new Datamap({
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
            return {
                path: path,
                projection: projection
            };
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
                return geo.fillColor || '#F5F5F5';
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
                return getTooltipContent({
                    name: geo.properties.name,
                    numOfInstitutions: data.numOfInstitutions
                });
            }
        }
    });

    map.addPlugin('pins', function(layer, data, options) {
        var self = this;
        var svg = this.svg;

        if (!data || data && !data.slice) {
            handleError();
            return;
        }

        var markers = layer.selectAll('image.datamaps-pins').data(data, JSON.stringify);

        markers
        .enter()
        .append('image')
        .attr('class', 'datamaps-pin')
        .attr('xlink:href', 'images/pin.png')
        .attr('height', 20)
        .attr('width', 20)
        .attr('x', getX)
        .attr('y', getY)
        .on('mouseover', function(datum) {
            var $this = d3.select(this);

            if (options.popupOnHover) {
                self.updatePopup($this, datum, options, svg);
            }
        })
        .on('mouseout', function() {
            var $this = d3.select(this);

            if (options.highlightOnHover) {
                var previousAttributes = JSON.parse($this.attr('data-previousAttributes'));
                $.each(previousAttributes, function(i, attr) {
                    $this.style(i, attr);
                });
            }
            d3.selectAll('.datamaps-hoverover').style('display', 'none');
        });

        markers.exit()
        .transition()
        .delay(options.exitDelay)
        .attr('height', 0)
        .remove();

        function getCoordinates(datum) {
            return datumHasCoords(datum) ? self.latLngToXY(datum.latitude, datum.longitude)
                                         : self.path.centroid(svg.select('path.' + datum.centered).data()[0]);
        }

        function getX(datum) {
            return getCoordinates(datum)[0];
        }

        function getY(datum) {
            return getCoordinates(datum)[1];
        }

        function datumHasCoords(datum) {
            return datum && datum.latitude && datum.longitude;
        }
    });

    map.pins(pins, {
        popupOnHover: true,
        popupTemplate: getTooltipContent
    });
}

function getTooltipContent(data) {
    return '<div class="hoverinfo">'
            + '<p>'
                + '<span class="bold">'
                + data.name
                + '</span>'
                + '<br>'
                + 'Institution(s): '
                + data.numOfInstitutions
            + '</p>'
         + '</div>';
}

document.addEventListener('DOMContentLoaded', function() {
    d3.json('/js/countryCoordinates.json', function(countryCoordinates) {
        d3.json('/js/userMapData.json', function(err, userData) {
            handleData(err, countryCoordinates, userData);
        });
    });
});
