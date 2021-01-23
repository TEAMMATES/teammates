import {Component, Input, OnInit} from '@angular/core';
import * as d3 from 'd3';
import {ResponseTimeSeriesChartModel} from "./response-time-series-chart.model";
import {FeedbackResponseStatsService} from "../../../services/feedback-response-stats.service";
import {interval} from "rxjs";
import {finalize} from "rxjs/operators";
import {FeedbackResponseStats} from "../../../types/api-output";
import {ErrorMessageOutput} from "../../error-message-output";

/**
 * Response-tracking time series chart for admin user.
 */
@Component({
  selector: 'tm-response-time-series-chart',
  templateUrl: './response-time-series-chart.component.html',
  styleUrls: ['./response-time-series-chart.component.scss']
})
export class ResponseTimeSeriesChartComponent implements OnInit {

  @Input()
  model: ResponseTimeSeriesChartModel = {
    durationMinutes: 60,
    intervalSeconds: 60
  }

  constructor(private feedbackResponseStatsService: FeedbackResponseStatsService) { }

  ngOnInit(): void {
    // var data: any = {
    //   "datapoints": {}
    // }
    // var now = Date.now() - 12 * 60 * 60 * 1000;
    // for (var i = 0; i < 1000; i++) {
    //   data.datapoints[new Date(now).toJSON()] = Math.round(Math.random()*100);
    //   now = now + 5 * 60 * 1000;
    // }

    // interval(this.model.duration).pipe(
    //     finalize(() => {
    //       this.feedbackResponseStatsService.loadResponseStats(
    //           this.model.duration.toString(), this.model.interval.toString())
    //     }))
    //     .subscribe((data: FeedbackResponseStats) => {
    //       console.log(data);
    //       // parse data
    //       // draw chart
    //     }, (resp: ErrorMessageOutput) => {
    //       console.log((resp.error));
    //     });

    // var parsedData = this.parseData(data);
    // this.drawChart(parsedData, this.model.durationMinutes * 60 * 1000);
  }

  /**
   * Handles a change in duration set by the drop-down
   */
  setDurationHandler(duration: number): void {
    this.model.durationMinutes = duration;
    console.log(this.model.durationMinutes);
  }

  drawChart(data: object[], duration: number): void {

    // constants
    const svgWidth: number = 800;
    const svgHeight: number = 400;
    const margin = { top: 20, right: 20, bottom: 30, left: 50 };
    const width: number = svgWidth - margin.left - margin.right;
    const height: number = svgHeight - margin.top - margin.bottom;

    // set width and height
    var svg = d3.select('svg')
        .attr("width", svgWidth)
        .attr("height", svgHeight);

    var g = svg.append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

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
