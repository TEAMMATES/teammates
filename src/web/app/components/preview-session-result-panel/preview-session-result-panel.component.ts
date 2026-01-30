import { Component, Input } from '@angular/core';
import {
  Instructor,
  Student,
} from '../../../types/api-output';
import { FormsModule } from '@angular/forms';
import { NgFor, NgIf, NgTemplateOutlet } from '@angular/common';
import { TeammatesRouterDirective } from '../teammates-router/teammates-router.directive';
import { NgbTooltip } from '@ng-bootstrap/ng-bootstrap';

/**
 * Displaying the preview session panel.
 */
@Component({
  selector: 'tm-preview-session-result-panel',
  templateUrl: './preview-session-result-panel.component.html',
  styleUrls: ['./preview-session-result-panel.component.scss'],
  imports: [
    FormsModule,
    NgFor,
    NgIf,
    TeammatesRouterDirective,
    NgbTooltip,
    NgTemplateOutlet,
  ],
})
export class PreviewSessionResultPanelComponent {

  @Input()
  courseId: string = '';

  @Input()
  feedbackSessionName: string = '';

  @Input()
  studentsOfCourse: Student[] = [];

  @Input()
  emailOfStudentToPreview: string = '';

  @Input()
  instructorsOfCourse: Instructor[] = [];

  @Input()
  emailOfInstructorToPreview: string = '';

  @Input()
  forDisplayOnly: boolean = false;

}
