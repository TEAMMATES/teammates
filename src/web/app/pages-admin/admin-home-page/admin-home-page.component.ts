import {Component, OnInit} from '@angular/core';
import { finalize } from 'rxjs/operators';
import { AccountService } from '../../../services/account.service';
import { JoinLink } from '../../../types/api-output';
import { ErrorMessageOutput } from '../../error-message-output';

import * as d3 from 'd3';

interface InstructorData {
  name: string;
  email: string;
  institution: string;
  status: string;
  joinLink?: string;
  message?: string;
}

/**
 * Admin home page.
 */
@Component({
  selector: 'tm-admin-home-page',
  templateUrl: './admin-home-page.component.html',
  styleUrls: ['./admin-home-page.component.scss'],
  styles: ['.line-chart { border: 1px solid lightgray; }'],
})
export class AdminHomePageComponent implements OnInit {

  instructorDetails: string = '';
  instructorName: string = '';
  instructorEmail: string = '';
  instructorInstitution: string = '';

  instructorsConsolidated: InstructorData[] = [];
  activeRequests: number = 0;

  isAddingInstructors: boolean = false;

  constructor(private accountService: AccountService) {}

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

  /**
   * Validates and adds the instructor details filled with first form.
   */
  validateAndAddInstructorDetails(): void {
    const invalidLines: string[] = [];
    for (const instructorDetail of this.instructorDetails.split(/\r?\n/)) {
      const instructorDetailSplit: string[] = instructorDetail.split(/[|\t]/).map((item: string) => item.trim());
      if (instructorDetailSplit.length < 3) {
        // TODO handle error
        invalidLines.push(instructorDetail);
        continue;
      }
      if (!instructorDetailSplit[0] || !instructorDetailSplit[1] || !instructorDetailSplit[2]) {
        // TODO handle error
        invalidLines.push(instructorDetail);
        continue;
      }
      this.instructorsConsolidated.push({
        name: instructorDetailSplit[0],
        email: instructorDetailSplit[1],
        institution: instructorDetailSplit[2],
        status: 'PENDING',
      });
    }
    this.instructorDetails = invalidLines.join('\r\n');
  }

  /**
   * Validates and adds the instructor detail filled with second form.
   */
  validateAndAddInstructorDetail(): void {
    if (!this.instructorName || !this.instructorEmail || !this.instructorInstitution) {
      // TODO handle error
      return;
    }
    this.instructorsConsolidated.push({
      name: this.instructorName,
      email: this.instructorEmail,
      institution: this.instructorInstitution,
      status: 'PENDING',
    });
    this.instructorName = '';
    this.instructorEmail = '';
    this.instructorInstitution = '';
  }

  /**
   * Adds the instructor at the i-th index.
   */
  addInstructor(i: number): void {
    const instructor: InstructorData = this.instructorsConsolidated[i];
    if (instructor.status !== 'PENDING' && instructor.status !== 'FAIL') {
      return;
    }
    this.activeRequests += 1;
    instructor.status = 'ADDING';

    this.isAddingInstructors = true;
    this.accountService.createAccount({
      instructorEmail: instructor.email,
      instructorName: instructor.name,
      instructorInstitution: instructor.institution,
    })
        .pipe(finalize(() => this.isAddingInstructors = false))
        .subscribe((resp: JoinLink) => {
          instructor.status = 'SUCCESS';
          instructor.joinLink = resp.joinLink;
          this.activeRequests -= 1;
        }, (resp: ErrorMessageOutput) => {
          instructor.status = 'FAIL';
          instructor.message = resp.error.message;
          this.activeRequests -= 1;
        });
  }

  /**
   * Cancels the instructor at the i-th index.
   */
  cancelInstructor(i: number): void {
    this.instructorsConsolidated.splice(i, 1);
  }

  /**
   * Adds all the pending and failed-to-add instructors.
   */
  addAllInstructors(): void {
    for (let i: number = 0; i < this.instructorsConsolidated.length; i += 1) {
      this.addInstructor(i);
    }
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
