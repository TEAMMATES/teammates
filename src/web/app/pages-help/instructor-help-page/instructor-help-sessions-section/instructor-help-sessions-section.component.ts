import { Component, OnInit } from '@angular/core';
import { InstructorHelpSectionComponent } from '../instructor-help-section.component';

/**
 * Sessions Section of the Instructor Help Page.
 */
@Component({
  selector: 'tm-instructor-help-sessions-section',
  templateUrl: './instructor-help-sessions-section.component.html',
  styleUrls: ['./instructor-help-sessions-section.component.scss'],
})
export class InstructorHelpSessionsSectionComponent extends InstructorHelpSectionComponent implements OnInit {

  constructor() {
    super();
  }

  ngOnInit(): void {
  }

}
