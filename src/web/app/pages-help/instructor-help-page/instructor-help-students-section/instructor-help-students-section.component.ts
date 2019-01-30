import { Component, OnInit } from '@angular/core';

import { InstructorHelpSectionComponent } from '../instructor-help-section/instructor-help-section.component';

/**
 * Students Section of the Instructor Help Page.
 */
@Component({
  selector: 'tm-instructor-help-students-section',
  templateUrl: './instructor-help-students-section.component.html',
  styleUrls: ['./instructor-help-students-section.component.scss'],
})
export class InstructorHelpStudentsSectionComponent extends InstructorHelpSectionComponent implements OnInit {

  constructor() {
    super();
  }

  ngOnInit(): void {

  }

}
