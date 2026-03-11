import {
  Component,
  Input,
  OnChanges,
  OnInit,
  OnDestroy,
  Output,
  EventEmitter,
} from "@angular/core";
import * as d3 from "d3";
import { LogsHistogramDataModel } from "./logs-histogram-model";
import { SourceLocation } from "../../../types/api-output";

/**
 * Histogram for displaying logs frequency.
 */
@Component({
  selector: "tm-logs-histogram",
  templateUrl: "./logs-histogram.component.html",
  styleUrls: ["./logs-histogram.component.scss"],
})
export class LogsHistogramComponent implements OnInit, OnChanges, OnDestroy {
  @Input()
  data: LogsHistogramDataModel[] = [];

  @Input()
  frequencyThreshold: number = 0;

  @Output()
  sourceLocationSelected = new EventEmitter<SourceLocation>();

  private svg: any;
  private chart: any;
  private margin: number = 30;
  private width: number = 0;
  private height: number = 0;
  private xScale: any;
  private yScale: any;
  private yAxis: any;
  private colorScale: any;
  private tooltip: any;

  ngOnInit(): void {
    this.createSvg();
    this.drawBars();
  }

  ngOnChanges(): void {
    if (this.chart) {
      this.drawBars();
    }
  }

  ngOnDestroy(): void {
    if (this.tooltip) {
      this.tooltip.remove();
    }
  }

  private createSvg(): void {
    this.width =
      (document.getElementById("histogram") as HTMLInputElement).offsetWidth -
      this.margin * 2;
    this.height =
      (document.getElementById("histogram") as HTMLInputElement).offsetHeight -
      this.margin * 2;

    this.svg = d3
      .select("figure#histogram")
      .append("svg")
      .attr("width", this.width + this.margin * 2)
      .attr("height", this.height + this.margin * 2 + 80);

    this.chart = this.svg
      .append("g")
      .attr("class", "bars")
      .attr("transform", `translate(${this.margin}, ${this.margin})`);

    this.xScale = d3
      .scaleBand()
      .domain(
        this.data.map(
          (d: LogsHistogramDataModel) =>
            d.sourceLocation.file + d.sourceLocation.function,
        ),
      )
      .range([0, this.width])
      .padding(0.2);

    this.yScale = d3
      .scaleLinear()
      .domain([
        0,
        d3.max(this.data, (d: LogsHistogramDataModel) => d.numberOfTimes) || 0,
      ])
      .range([this.height, 0]);

    this.svg
      .append("g")
      .attr("class", "axis axis-x")
      .attr(
        "transform",
        `translate(${this.margin}, ${this.margin + this.height})`,
      );

    this.yAxis = this.svg
      .append("g")
      .attr("class", "axis axis-y")
      .attr("transform", `translate(${this.margin}, ${this.margin})`)
      .call(d3.axisLeft(this.yScale));

    // Create color scale based on frequency quantiles
    const frequencies = this.data.map((d) => d.numberOfTimes);
    this.colorScale = d3
      .scaleQuantile<string>()
      .domain(frequencies)
      .range(["#1f77b4", "#ff7f0e", "#d62728"]);

    this.drawLegend();

    this.tooltip = d3
      .select("body")
      .append("div")
      .style("position", "absolute")
      .style("z-index", "10")
      .style("visibility", "hidden")
      .style("padding", "10px")
      .style("background", "#000")
      .style("border-radius", "5px")
      .style("color", "#fff");
  }

  private drawLegend(): void {
    // Remove existing legend
    this.svg.selectAll(".legend").remove();

    const quantiles = this.colorScale.quantiles();
    const legendData = [
      { color: "#1f77b4", label: `Low (0-${Math.round(quantiles[0] || 0)})` },
      {
        color: "#ff7f0e",
        label: `Medium (${Math.round(quantiles[0] || 0)}-${Math.round(quantiles[1] || 0)})`,
      },
      { color: "#d62728", label: `High (${Math.round(quantiles[1] || 0)}+)` },
    ];

    const legend = this.svg
      .append("g")
      .attr("class", "legend")
      .attr("transform", `translate(${this.width - 240}, ${this.height + 50})`);

    const legendItems = legend
      .selectAll(".legend-item")
      .data(legendData)
      .enter()
      .append("g")
      .attr("class", "legend-item")
      .attr(
        "transform",
        (_d: { color: string; label: string }, i: number) =>
          `translate(${i * 100}, 0)`,
      );

    legendItems
      .append("rect")
      .attr("width", 18)
      .attr("height", 18)
      .style("fill", (d: { color: string; label: string }) => d.color);

    legendItems
      .append("text")
      .attr("x", 24)
      .attr("y", 9)
      .attr("dy", ".35em")
      .style("text-anchor", "start")
      .style("font-size", "12px")
      .text((d: { color: string; label: string }) => d.label);
  }

  private drawBars(): void {
    this.xScale.domain(
      this.data.map(
        (d: LogsHistogramDataModel) =>
          d.sourceLocation.file + d.sourceLocation.function,
      ),
    );
    this.yScale.domain([
      0,
      d3.max(this.data, (d: LogsHistogramDataModel) => d.numberOfTimes),
    ]);
    this.yAxis.call(d3.axisLeft(this.yScale));

    // Update color scale
    const frequencies = this.data.map((d) => d.numberOfTimes);
    this.colorScale.domain(frequencies);

    this.drawLegend();

    const update: any = this.chart
      .selectAll(".bar")
      .data(
        this.data,
        (d: LogsHistogramDataModel) =>
          d.sourceLocation.file + d.sourceLocation.function,
      );

    // remove exiting bars
    update.exit().remove();

    this.chart
      .selectAll(".bar")
      .attr("x", (d: LogsHistogramDataModel) =>
        this.xScale(d.sourceLocation.file + d.sourceLocation.function),
      )
      .attr("y", (d: LogsHistogramDataModel) => this.yScale(d.numberOfTimes))
      .attr(
        "height",
        (d: LogsHistogramDataModel) =>
          this.height - this.yScale(d.numberOfTimes),
      )
      .attr("width", this.xScale.bandwidth())
      .style("fill", (d: LogsHistogramDataModel) =>
        this.colorScale(d.numberOfTimes),
      );

    update
      .enter()
      .append("rect")
      .attr("class", "bar")
      .attr("x", (d: LogsHistogramDataModel) =>
        this.xScale(d.sourceLocation.file + d.sourceLocation.function),
      )
      .attr("y", (d: LogsHistogramDataModel) => this.yScale(d.numberOfTimes))
      .attr(
        "height",
        (d: LogsHistogramDataModel) =>
          this.height - this.yScale(d.numberOfTimes),
      )
      .attr("width", this.xScale.bandwidth())
      .style("fill", (d: LogsHistogramDataModel) =>
        this.colorScale(d.numberOfTimes),
      )
      .on("mouseover", (_: any, d: LogsHistogramDataModel) =>
        this.tooltip
          .html(
            `File: ${d.sourceLocation.file} <br> Function: ${d.sourceLocation.function}` +
              ` <br> Frequency: ${d.numberOfTimes}`,
          )
          .style("visibility", "visible"),
      )
      .on("mousemove", (event: any) => {
        const top: number = event.pageY - 10;
        const left: number = event.pageX + 10;
        this.tooltip.style("top", `${top}px`).style("left", `${left}px`);
      })
      .on("mouseout", () => this.tooltip.html("").style("visibility", "hidden"))
      .on("click", (_event: any, d: LogsHistogramDataModel) =>
        this.sourceLocationSelected.emit(d.sourceLocation),
      );
  }
}
