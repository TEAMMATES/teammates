/* global d3:false Datamap:false */

import {
    getCountryCode,
} from '../common/countryCodes';

function getTooltipContent(data) {
    return `<div class="hoverinfo">
                <p>
                    <b>${data.name}</b>
                    <br>Institutions: ${data.numOfInstitutions}
                </p>
            </div>`;
}

function handleError() {
    const contentHolder = d3.select('.container');
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
        return null;
    }
    const userCountries = Object.keys(userData.institutes);
    const countriesArr = [];
    let total = 0;
    const date = userData.lastUpdated;
    userCountries.forEach((countryName) => {
        const countryTotal = userData.institutes[countryName].length;

        countriesArr.push([countryName, countryTotal]);
        total += countryTotal;
    });

    // set the last updated date in the page
    $('#lastUpdate').html(date);
    // set the institution count in the page
    $('#totalUserCount').html(total);
    // set the country count in the page
    $('#totalCountryCount').html(userCountries.length);

    // Data format example
    // var series = [
    //     ['United States', 1], ['Bulgaria', 1], ['Russia', 1], ['France', 1], ['Singapore', 1]

    const dataset = {};
    const pins = [];
    const onlyValues = countriesArr.map(obj => obj[1]);
    const minValue = Math.min.apply(null, onlyValues);
    const maxValue = Math.max.apply(null, onlyValues);
    const paletteScale = d3.scale.linear()
            .domain([minValue, maxValue])
            .range(['#428bca', '#428bca']); // Choropleth effect: .range(['#C1F0F6","#4895AE"]);
    countriesArr.forEach((item) => {
        const countryName = item[0];
        const iso = getCountryCode(countryName);
        const value = item[1];
        const coordinates = countryCoordinates[iso];
        dataset[iso] = {
            numOfInstitutions: value,
            fillColor: paletteScale(value),
        };
        pins.push({
            name: countryName,
            numOfInstitutions: value,
            latitude: coordinates.lat,
            longitude: coordinates.lon,
        });
    });

    // World-map
    const map = new Datamap({
        scope: 'world',
        element: $('#world-map').get(0),
        responsive: true,
        setProjection(element) {
            const projection = d3.geo.mercator()
                    .center([0, 20])
                    .rotate([-5, 0])
                    .scale(130)
                    .translate([element.offsetWidth / 2, element.offsetHeight / 2]);
            const path = d3.geo.path()
                    .projection(projection);
            return {
                path,
                projection,
            };
        },
        // countries don't listed in dataset will be painted with this color
        fills: { defaultFill: '#F5F5F5' },
        data: dataset,
        geographyConfig: {
            borderColor: '#DEDEDE',
            borderWidth: 0.7,
            // don't change color on mouse hover
            highlightFillColor(geo) {
                return geo.fillColor || '#F5F5F5';
            },
            dataUrl: $('#geo-data-url').val(),
            // only change border
            highlightBorderColor: '#a4a4a4',
            highlightBorderWidth: 1,
            highlightBorderOpacity: 1,
            // show desired information in tooltip
            popupTemplate(geo, data) {
                if (data) {
                    return getTooltipContent({
                        name: geo.properties.name,
                        numOfInstitutions: data.numOfInstitutions,
                    });
                }
                // don't show tooltip if country is not present in dataset
                return null;
            },
        },
    });

    map.addPlugin('pins', function (layer, data, options) {
        const self = this;
        const { svg } = this;

        function datumHasCoords(datum) {
            return datum && datum.latitude && datum.longitude;
        }

        function getCoordinates(datum) {
            return datumHasCoords(datum) ? self.latLngToXY(datum.latitude, datum.longitude)
                    : self.path.centroid(svg.select(`path.${datum.centered}`).data()[0]);
        }

        function getX(datum) {
            return getCoordinates(datum)[0];
        }

        function getY(datum) {
            return getCoordinates(datum)[1];
        }

        if (!data || data && !data.slice) {
            handleError();
            return;
        }

        const markers = layer.selectAll('image.datamaps-pins').data(data, JSON.stringify);

        markers
                .enter()
                .append('image')
                .attr('class', 'datamaps-pin')
                .attr('xlink:href', 'images/pin.png')
                .attr('height', 20)
                .attr('width', 20)
                .attr('x', getX)
                .attr('y', getY)
                .on('mouseover', function (datum) {
                    const $this = d3.select(this);

                    if (options.popupOnHover) {
                        self.updatePopup($this, datum, options, svg);
                    }
                })
                .on('mouseout', function () {
                    const $this = d3.select(this);

                    if (options.highlightOnHover) {
                        const previousAttributes = JSON.parse($this.attr('data-previousAttributes'));
                        $.each(previousAttributes, (i, attr) => {
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
    });

    map.pins(pins, {
        popupOnHover: true,
        popupTemplate: getTooltipContent,
    });

    return map;
}

document.addEventListener('DOMContentLoaded', () => {
    let map;
    d3.json('/data/countryCoordinates.json', (countryCoordinates) => {
        d3.json('/data/userMapData.json', (err, userData) => {
            map = initializeMap(err, countryCoordinates, userData);
        });
    });

    d3.select(window).on('resize', () => {
        map.resize();
    });
});
