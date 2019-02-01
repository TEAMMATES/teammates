import { Component, OnInit } from '@angular/core';
import { InstructorHelpSectionComponent } from "../instructor-help-section.component";

/**
 * Questions Section of the Instructor Help Page.
 */
@Component({
  selector: 'tm-instructor-help-questions-section',
  templateUrl: './instructor-help-questions-section.component.html',
  styleUrls: ['./instructor-help-questions-section.component.scss']
})
export class InstructorHelpQuestionsSectionComponent extends InstructorHelpSectionComponent implements OnInit {

  constructor() {
    super();
  }

  ngOnInit(): void {
  }

}
