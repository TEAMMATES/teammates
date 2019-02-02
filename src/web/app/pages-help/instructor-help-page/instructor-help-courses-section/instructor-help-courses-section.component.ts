import { Component, OnInit } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { InstructorHelpSectionComponent } from '../instructor-help-section.component';

/**
 * Courses section of the Instructor Help Page
 */
@Component({
  selector: 'tm-instructor-help-courses-section',
  templateUrl: './instructor-help-courses-section.component.html',
  styleUrls: ['./instructor-help-courses-section.component.scss'],
})
export class InstructorHelpCoursesSectionComponent extends InstructorHelpSectionComponent implements OnInit {

  readonly supportEmail: string = environment.supportEmail;

  constructor() {
    super();
  }

  /**
   * Returns a boolean value if any question in the subsection is to be displayed after the search
   */
  displaySubsection(array: Boolean[], firstPoint: number, lastPoint: number): boolean {
    return array.length === 0 || array.slice(firstPoint, lastPoint)
        .reduce((x: any, y: any) => x || y, false);
  }

  ngOnInit(): void {

  }
}
