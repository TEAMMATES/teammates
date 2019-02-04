import { Component, OnInit } from '@angular/core';
import { environment } from '../../../../environments/environment';

import { InstructorHelpSectionComponent } from '../instructor-help-section.component';

/**
 * Students Section of the Instructor Help Page.
 */
@Component({
  selector: 'tm-instructor-help-students-section',
  templateUrl: './instructor-help-students-section.component.html',
  styleUrls: ['./instructor-help-students-section.component.scss'],
})
export class InstructorHelpStudentsSectionComponent extends InstructorHelpSectionComponent implements OnInit {

  readonly supportEmail: string = environment.supportEmail;

  constructor() {
    super();
  }

  ngOnInit(): void {

  }
}
