import { Component, Input, OnChanges, OnInit } from '@angular/core';
import * as d3 from 'd3';
import { LogsHistogramDataModel } from './logs-histogram-model';

@Component({
  selector: 'tm-logs-histogram',
  templateUrl: './logs-histogram.component.html',
  styleUrls: ['./logs-histogram.component.scss']
})
export class LogsHistogramComponent implements OnInit, OnChanges {

  @Input()
  data: LogsHistogramDataModel[] = [];

  private svg: any;
  private chart: any;
  private margin: number = 30;
  private width: number = 900 - (this.margin * 2);
  private height: number = 400 - (this.margin * 2);
  private xScale: any;
  private yScale: any;
  private yAxis: any;

  constructor() { }

  ngOnInit(): void {
    const data1: LogsHistogramDataModel = {
      sourceLocation: {file: 'file1', function: 'func', line: 10},
      numberOfTimes: 10,
    }
    const data2: LogsHistogramDataModel = {
      sourceLocation: {file: 'file2', function: 'func', line: 10},
      numberOfTimes: 3,
    }
    const data3: LogsHistogramDataModel = {
      sourceLocation: {file: 'file3', function: 'func', line: 10},
      numberOfTimes: 100,
    }
    this.data = [data1, data2, data3];
    this.createSvg();
    this.drawBars();
  }

  ngOnChanges() {
    if (this.chart) {
      this.drawBars();
    }
  }

  private createSvg(): void {
    this.svg = d3.select('figure#histogram')
      .append('svg')
      .attr('width', this.width + (this.margin * 2))
      .attr('height', this.height + (this.margin * 2))
    
    this.chart = this.svg.append('g')
      .attr('class', 'bars')
      .attr('transform', `translate(${this.margin}, ${this.margin})`);

    this.xScale = d3.scaleBand()
      .domain(this.data.map(d => d.sourceLocation.file + d.sourceLocation.function))
      .range([0, this.width])
      .padding(0.2);
    
    this.yScale = d3.scaleLinear()
      .domain([0, d3.max(this.data, (d: LogsHistogramDataModel) => d.numberOfTimes)])
      .range([this.height, 0]);

    this.svg.append('g')
      .attr('class', 'axis axis-x')
      .attr('transform', `translate(${this.margin}, ${this.margin + this.height})`)
    
    this.yAxis = this.svg.append('g')
      .attr('class', 'axis axis-y')
      .attr('transform', `translate(${this.margin}, ${this.margin})`)
      .call(d3.axisLeft(this.yScale));
  }

  private drawBars(): void {
    this.xScale.domain(this.data.map(d => d.sourceLocation.file + d.sourceLocation.function));
    this.yScale.domain([0, d3.max(this.data, (d: LogsHistogramDataModel) => d.numberOfTimes)]);
    this.yAxis.call(d3.axisLeft(this.yScale));

    const tooltip = d3.select("body")
      .append("div")
      .style("position", "absolute")
      .style("z-index", "10")
      .style("visibility", "hidden")
      .style("padding", "15px")
      .style("background", "rgba(0,0,0,0.5)")
      .style("border-radius", "5px")
      .style("color", "#fff");

    let update = this.chart.selectAll('.bar')
      .data(this.data);

    // remove exiting bars
    update.exit().remove();

    this.chart.selectAll('.bar')
      .attr('x', (d: LogsHistogramDataModel) => this.xScale(d.sourceLocation.file + d.sourceLocation.function))
      .attr('y', (d: LogsHistogramDataModel) => this.yScale(d.numberOfTimes))
      .attr('height', (d: LogsHistogramDataModel) => this.height - this.yScale(d.numberOfTimes))
      .attr('width', this.xScale.bandwidth());

    update
      .enter()
      .append('rect')
      .attr('class', 'bar')
      .attr('x', (d: LogsHistogramDataModel) => this.xScale(d.sourceLocation.file  + d.sourceLocation.function))
      .attr('y', (d: LogsHistogramDataModel) => this.yScale(d.numberOfTimes))
      .attr('height', (d: LogsHistogramDataModel) => this.height - this.yScale(d.numberOfTimes))
      .attr('width', this.xScale.bandwidth())
      .style('fill', 'steelblue')
      .on("mouseover", function(d: LogsHistogramDataModel) {
        tooltip
          .html(`File: ${d.sourceLocation.file} <br> Function: ${d.sourceLocation.function} <br> Frequency: ${d.numberOfTimes}`)
          .style("visibility", "visible");
      })
      .on("mousemove", () => tooltip
        .style("top", (d3.event.pageY-10)+"px")
        .style("left",(d3.event.pageX+10)+"px")
      )
      .on("mouseout", function() {
        tooltip.html(``).style("visibility", "hidden");
      })
  }
}
