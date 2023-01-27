import { Component, Input, OnChanges, OnInit } from '@angular/core';
import * as d3 from 'd3';
import { LogsHistogramDataModel } from './logs-histogram-model';

/**
 * Histogram for displaying logs frequency.
 */
@Component({
  selector: 'tm-logs-histogram',
  templateUrl: './logs-histogram.component.html',
  styleUrls: ['./logs-histogram.component.scss'],
})
export class LogsHistogramComponent implements OnInit, OnChanges {

  @Input()
  data: LogsHistogramDataModel[] = [];

  private svg: any;
  private chart: any;
  private margin: number = 30;
  private width: number = 0;
  private height: number = 0;
  private xScale: any;
  private yScale: any;
  private yAxis: any;

  ngOnInit(): void {
    this.createSvg();
    this.drawBars();
  }

  ngOnChanges(): void {
    if (this.chart) {
      this.drawBars();
    }
  }

  private createSvg(): void {
    this.width = (document.getElementById('histogram') as HTMLInputElement).offsetWidth - (this.margin * 2);
    this.height = (document.getElementById('histogram') as HTMLInputElement).offsetHeight - (this.margin * 2);

    this.svg = d3.select('figure#histogram')
      .append('svg')
      .attr('width', this.width + (this.margin * 2))
      .attr('height', this.height + (this.margin * 2));

    this.chart = this.svg.append('g')
      .attr('class', 'bars')
      .attr('transform', `translate(${this.margin}, ${this.margin})`);

    this.xScale = d3.scaleBand()
      .domain(this.data.map((d: LogsHistogramDataModel) => d.sourceLocation.file + d.sourceLocation.function))
      .range([0, this.width])
      .padding(0.2);

    this.yScale = d3.scaleLinear()
      .domain([0, d3.max(this.data, (d: LogsHistogramDataModel) => d.numberOfTimes) || 0])
      .range([this.height, 0]);

    this.svg.append('g')
      .attr('class', 'axis axis-x')
      .attr('transform', `translate(${this.margin}, ${this.margin + this.height})`);

    this.yAxis = this.svg.append('g')
      .attr('class', 'axis axis-y')
      .attr('transform', `translate(${this.margin}, ${this.margin})`)
      .call(d3.axisLeft(this.yScale));
  }

  private drawBars(): void {
    this.xScale.domain(this.data.map((d: LogsHistogramDataModel) => d.sourceLocation.file + d.sourceLocation.function));
    this.yScale.domain([0, d3.max(this.data, (d: LogsHistogramDataModel) => d.numberOfTimes)]);
    this.yAxis.call(d3.axisLeft(this.yScale));

    const tooltip: any = d3.select('body')
      .append('div')
      .style('position', 'absolute')
      .style('z-index', '10')
      .style('visibility', 'hidden')
      .style('padding', '10px')
      .style('background', '#000')
      .style('border-radius', '5px')
      .style('color', '#fff');

    const update: any = this.chart.selectAll('.bar').data(this.data);

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
      .attr('x', (d: LogsHistogramDataModel) => this.xScale(d.sourceLocation.file + d.sourceLocation.function))
      .attr('y', (d: LogsHistogramDataModel) => this.yScale(d.numberOfTimes))
      .attr('height', (d: LogsHistogramDataModel) => this.height - this.yScale(d.numberOfTimes))
      .attr('width', this.xScale.bandwidth())
      .style('fill', 'steelblue')
      .on('mouseover', (_: any, d: LogsHistogramDataModel) =>
        tooltip
          .html(`File: ${d.sourceLocation.file} <br> Function: ${d.sourceLocation.function}`
              + ` <br> Frequency: ${d.numberOfTimes}`)
          .style('visibility', 'visible'))
      .on('mousemove', (event: any) => {
        const top: number = event.pageY - 10;
        const left: number = event.pageX + 10;
        tooltip
          .style('top', `${top}px`)
          .style('left', `${left}px`);
      })
      .on('mouseout', () => tooltip.html('').style('visibility', 'hidden'));
  }
}
