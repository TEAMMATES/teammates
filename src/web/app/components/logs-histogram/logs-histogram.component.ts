import { Component, Input, OnInit } from '@angular/core';
import * as d3 from 'd3';
import { LogsHistogramDataModel } from './logs-histogram-model';

@Component({
  selector: 'tm-logs-histogram',
  templateUrl: './logs-histogram.component.html',
  styleUrls: ['./logs-histogram.component.scss']
})
export class LogsHistogramComponent implements OnInit {

  @Input()
  data: LogsHistogramDataModel[] = [];

  private svg: any;
  private margin = 50;
  private width = 750 - (this.margin * 2);
  private height = 400 - (this.margin * 2);

  private createSvg(): void {
    this.svg = d3.select("figure#bar")
      .append("svg")
      .attr("width", this.width + (this.margin * 2))
      .attr("height", this.height + (this.margin * 2))
      .append("g")
      .attr("transform", "translate(" + this.margin + "," + this.margin + ")");
  }

  private drawBars(data: LogsHistogramDataModel[]): void {
    // Create the X-axis band scale
    const x = d3.scaleBand()
      .range([0, this.width])
      .domain(data.map(d => d.sourceLocation))
      .padding(0.2);

    // Draw the X-axis on the DOM
    this.svg.append("g")
      .attr("transform", "translate(0," + this.height + ")")
      .call(d3.axisBottom(x))
      .selectAll("text")
      .attr("transform", "translate(-10,0)rotate(-45)")
      .style("text-anchor", "end");

    // Create the Y-axis band scale
    const y = d3.scaleLinear()
      .domain([0, 100])
      .range([this.height, 0]);

    // Draw the Y-axis on the DOM
    this.svg.append("g")
      .call(d3.axisLeft(y));

    // Create and fill the bars
    this.svg.selectAll("bars")
      .data(data)
      .enter()
      .append("rect")
      .attr("y", (d: { numberOfTimes: number; }) => y(d.numberOfTimes))
      .attr("width", x.bandwidth())
      .attr("height", (d: { numberOfTimes: number; }) => this.height - y(d.numberOfTimes))
      .attr("fill", "#d04a35");
  }

  constructor() { }

  ngOnInit(): void {
    this.createSvg();
    this.drawBars(this.data);
  }
}
