import { Component, Input, OnInit } from '@angular/core';
import * as d3 from 'd3';
import { ResponseTimeSeriesChartModel } from "./response-time-series-chart.model";
import { FeedbackResponseStatsService } from "../../../services/feedback-response-stats.service";
import { BehaviorSubject, interval } from "rxjs";
import { switchMap, tap } from "rxjs/operators";
import { FeedbackResponseStats } from "../../../types/api-output";
import { ErrorMessageOutput } from "../../error-message-output";

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
    intervalMinutes: 1
  }

  interval: BehaviorSubject<number> = new BehaviorSubject<number>(1);

  constructor(private feedbackResponseStatsService: FeedbackResponseStatsService) { }

  ngOnInit(): void {
    this.interval.pipe(
        switchMap(value => interval(value * 1000)), // in seconds (for easier testing), actual one will be in minutes
        tap(() => this.refresh())
    ).subscribe();
  }

  refresh() {
    this.feedbackResponseStatsService.loadResponseStats(this.model.durationMinutes.toString(), this.model.intervalMinutes.toString())
        .subscribe((data: FeedbackResponseStats) => {
          console.log(data);
          // parse data
          const parsedData = this.parseData(data);
          // draw chart
          this.drawChart(parsedData, this.model.durationMinutes * 60 * 1000);
        }, (resp: ErrorMessageOutput) => {
          console.log((resp.error));
        });
  }

  /**
   * Handles a change in duration
   */
  setDurationHandler(duration: number): void {
    this.model.durationMinutes = duration;
  }

  /**
   * Handles a change in interval
   */
  setIntervalHandler(interval: number): void {
    this.model.intervalMinutes = interval;
    this.interval.next(interval);
  }

  drawChart(data: object[], duration: number): void {

    let svg = d3.select('svg');

    // clear all content
    svg.selectAll("*").remove();

    const svgWidth: number = 800;
    const svgHeight: number = 400;
    const margin = { top: 20, right: 20, bottom: 30, left: 50 };
    const width: number = svgWidth - margin.left - margin.right;
    const height: number = svgHeight - margin.top - margin.bottom;

    svg.attr("width", svgWidth);
    svg.attr("height", svgHeight);

    let container = svg.append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    let x = d3.scaleTime()
        .rangeRound([0, width])
        .nice();

    let y = d3.scaleLinear()
        .rangeRound([height, 0]);

    x.domain([Date.now() - duration, Date.now()]);
    y.domain(d3.extent(data, (d: any) => d.resCount));

    let line = d3.line()
        .defined((d: any) => d.timestamp >= Date.now() - duration && d.timestamp <= Date.now())
        .x((d: any) => x(d.timestamp))
        .y((d: any) => y(d.resCount));

    container.append("g")
        .attr("transform", "translate(0," + height + ")")
        .call(d3.axisBottom(x));

    container.append("g")
        .call(d3.axisLeft(y))
        .append("text")
        .attr("fill", "#000")
        .attr("transform", "rotate(-90)")
        .attr("y", 6)
        .attr("dy", "0.71em")
        .attr("text-anchor", "end")
        .text("No. of responses");

    container.append("path")
        .datum(data)
        .attr("fill", "none")
        .attr("stroke", "steelblue")
        .attr("stroke-linejoin", "round")
        .attr("stroke-linecap", "round")
        .attr("stroke-width", 1.5)
        .attr("d", line);
  }

  parseData(data: any): any[] {
    let arr: any[] = [];
    for (let i in data.datapoints) {
      arr.push({
        timestamp: new Date(i),
        resCount: +data.datapoints[i]
      });
    }
    return arr;
  }
}
