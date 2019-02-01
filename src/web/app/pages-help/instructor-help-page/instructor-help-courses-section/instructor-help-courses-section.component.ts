import { Component, OnInit } from '@angular/core';
import {environment} from "../../../../environments/environment";
import {InstructorHelpSectionComponent} from "../instructor-help-section.component";

@Component({
  selector: 'tm-instructor-help-courses-section',
  templateUrl: './instructor-help-courses-section.component.html',
  styleUrls: ['./instructor-help-courses-section.component.scss']
})
export class InstructorHelpCoursesSectionComponent extends InstructorHelpSectionComponent implements OnInit {

  readonly supportEmail: string = environment.supportEmail;

  constructor() {
    super();
  }

  displaySubsection(array:any, firstPoint: number, lastPoint:number) {
    return array.length == 0 || this.showQuestion.slice(firstPoint, lastPoint).reduce((x, y) => x || y, false);
  }

  ngOnInit(): void {

  }
}
