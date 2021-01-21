import { Component, OnInit } from '@angular/core';
import * as d3 from 'd3';

@Component({
  selector: 'tm-response-time-series-chart',
  templateUrl: './response-time-series-chart.component.html',
  styleUrls: ['./response-time-series-chart.component.scss']
})
export class ResponseTimeSeriesChartComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
    var data: any = {
      "datapoints": {}
    }
    var now = Date.now() - 12 * 60 * 60 * 1000;
    for (var i = 0; i < 1000; i++) {
      data.datapoints[new Date(now).toJSON()] = Math.round(Math.random()*100);
      now = now + 5 * 60 * 1000;
    }

    console.log(data);

    var parsedData = this.parseData(data);
    this.drawChart(parsedData);
  }

  drawChart(data: object[]): void {
    var svgWidth: number = 800;
    var svgHeight: number = 400;
    var margin = { top: 20, right: 20, bottom: 30, left: 50 };
    var width: number = svgWidth - margin.left - margin.right;
    var height: number = svgHeight - margin.top - margin.bottom;

    var svg = d3.select('svg')
        .attr("width", svgWidth)
        .attr("height", svgHeight);

    var g = svg.append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    var duration = 12 * 60 * 60 * 1000;

    var x = d3.scaleTime()
        .rangeRound([0, width])
        .nice();

    var y = d3.scaleLinear()
        .rangeRound([height, 0]);

    x.domain([Date.now() - duration, Date.now()]);
    y.domain(d3.extent(data, function(d: any) {
      return d.resCount
    }));

    var line = d3.line()
        .defined(function(d: any) {
          const belowXLimit: boolean = d.timestamp < Date.now() - duration;
          const aboveXLimit: boolean = d.timestamp > Date.now();
          return !belowXLimit && !aboveXLimit;
        })
        .x(function(d: any) {
          return x(d.timestamp);
        })
        .y(function(d: any) {
          return y(d.resCount)
        });

    g.append("g")
        .attr("transform", "translate(0," + height + ")")
        .call(d3.axisBottom(x));

    g.append("g")
        .call(d3.axisLeft(y))
        .append("text")
        .attr("fill", "#000")
        .attr("transform", "rotate(-90)")
        .attr("y", 6)
        .attr("dy", "0.71em")
        .attr("text-anchor", "end")
        .text("No. of responses");

    g.append("path")
        .datum(data)
        .attr("fill", "none")
        .attr("stroke", "steelblue")
        .attr("stroke-linejoin", "round")
        .attr("stroke-linecap", "round")
        .attr("stroke-width", 1.5)
        .attr("d", line);
  }

  parseData(data: any): any[] {
    var arr: any[] = [];
    for (var i in data.datapoints) {
      arr.push({
        timestamp: new Date(i),
        resCount: +data.datapoints[i]
      });
    }
    return arr;
  }
}
