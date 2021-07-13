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
  private margin: number = 30;
  private width: number = 900 - (this.margin * 2);
  private height: number = 400 - (this.margin * 2);
  private xScale: any;
  private yScale: any;

  constructor() { }

  ngOnInit(): void {
    // const data1: LogsHistogramDataModel = {
    //   sourceLocation: {file: 'file1', function: 'func', line: 10},
    //   numberOfTimes: 10,
    // }
    // const data2: LogsHistogramDataModel = {
    //   sourceLocation: {file: 'file2', function: 'func', line: 10},
    //   numberOfTimes: 3,
    // }
    // const data3: LogsHistogramDataModel = {
    //   sourceLocation: {file: 'file3', function: 'func', line: 10},
    //   numberOfTimes: 17,
    // }
    // this.data = [data1, data2, data3];
    this.createSvg();
    this.drawBars();
  }

  ngOnChanges() {
    this.drawBars();
  }

  private createSvg(): void {
    this.svg = d3.select('figure#histogram')
      .append('svg')
      .attr('width', this.width + (this.margin * 2))
      .attr('height', this.height + (this.margin * 2))
      .append('g')
      .attr("transform", "translate(" + this.margin + "," + this.margin + ")")
    
    this.svg.append('g')
      .attr("transform", "translate(0," + this.height + ")")
  }

  private drawBars(): void {
    this.xScale = d3.scaleBand()
      .domain(this.data.map(d => d.sourceLocation.file + d.sourceLocation.function))
      .range([0, this.width])
      .padding(0.2);
    
    this.yScale = d3.scaleLinear()
      .domain([0, d3.max(this.data, (d: LogsHistogramDataModel) => d.numberOfTimes)])
      .range([this.height, 0]);
    
    this.svg.append('g')
      .call(d3.axisLeft(this.yScale));

    this.svg
      .selectAll('bars')
      .data(this.data)
      .enter()
      .append('rect')
      .attr('x', (d: LogsHistogramDataModel) => this.xScale(d.sourceLocation.file  + d.sourceLocation.function))
      .attr('y', (d: LogsHistogramDataModel) => this.yScale(d.numberOfTimes))
      .attr('height', (d: LogsHistogramDataModel) => this.height - this.yScale(d.numberOfTimes))
      .attr('width', 40)
      .style('fill', 'steelblue');
  }
}
