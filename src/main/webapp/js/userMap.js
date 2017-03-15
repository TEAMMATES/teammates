'use strict';

function handleError() {
    var contentHolder = d3.select('.container');
    contentHolder.html('');
    contentHolder.append('p')
        .text('An error has occured in getting data, please try reloading.');
    contentHolder.append('p')
        .html('If the problem persists after a few retries, please <a href="/contact.jsp">contact us</a>.');
}

function initializeMap(err, countryCoordinates, userData) {
    // based on example from https://github.com/markmarkoh/datamaps/blob/master/src/examples/highmaps_world.html
    // Country code: https://en.wikipedia.org/wiki/ISO_3166-1_alpha-3
    if (err) {
        handleError();
        return;
    }
    var userCountries = Object.keys(userData.institutes);
    var countriesArr = [];
    var total = 0;
    var date = userData.lastUpdated;
    userCountries.forEach(function(countryName) {
        var countryTotal = userData.institutes[countryName].length;

        countriesArr.push([countryName, countryTotal]);
        total += countryTotal;
    });

    // set the last updated date in the page
    document.getElementById('lastUpdate').innerHTML = date;
    // set the institution count in the page
    document.getElementById('totalUserCount').innerHTML = total;
    // set the country count in the page
    document.getElementById('totalCountryCount').innerHTML = userCountries.length;

    // Data format example
    // var series = [
    //     ['United States', 1], ['Bulgaria', 1], ['Russia', 1], ['France', 1], ['Singapore', 1]

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
        var countryName = item[0];
        var iso = getCountryCode(countryName);
        var value = item[1];
        var coordinates = countryCoordinates[iso];
        dataset[iso] = {
            numOfInstitutions: value,
            fillColor: paletteScale(value)
        };
        pins.push({
            name: countryName,
            numOfInstitutions: value,
            latitude: coordinates.lat,
            longitude: coordinates.lon
        });
    });

    // World-map
    var map = new Datamap({
        scope: 'world',
        element: document.getElementById('world-map'),
        responsive: true,
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
            dataUrl: geoDataUrl,
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

    return map;
}

function getTooltipContent(data) {
    return '<div class="hoverinfo">'
            + '<p>'
                + '<b>'
                + data.name
                + '</b>'
                + '<br>'
                + 'Institutions: '
                + data.numOfInstitutions
            + '</p>'
         + '</div>';
}

document.addEventListener('DOMContentLoaded', function() {
    var map;
    d3.json('/js/countryCoordinates.json', function(countryCoordinates) {
        d3.json('/js/userMapData.json', function(err, userData) {
            map = initializeMap(err, countryCoordinates, userData);

        });
    });

    d3.select(window).on('resize', function() {
        map.resize();
    });
});
