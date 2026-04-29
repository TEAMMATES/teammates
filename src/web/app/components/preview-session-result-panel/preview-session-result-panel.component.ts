import { NgTemplateOutlet } from '@angular/common';
import { Component, Input } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbTooltip } from '@ng-bootstrap/ng-bootstrap';
import {
  Instructor,
  Student,
} from '../../../types/api-output';
import { TeammatesRouterDirective } from '../teammates-router/teammates-router.directive';

/**
 * Displaying the preview session panel.
 */
@Component({
  selector: 'tm-preview-session-result-panel',
  templateUrl: './preview-session-result-panel.component.html',
  styleUrls: ['./preview-session-result-panel.component.scss'],
  imports: [
    FormsModule,
    TeammatesRouterDirective,
    NgbTooltip,
    NgTemplateOutlet,
],
})
export class PreviewSessionResultPanelComponent {

  @Input()
  courseId = '';

  @Input()
  feedbackSessionName = '';

  @Input()
  feedbackSessionId = '';

  @Input()
  studentsOfCourse: Student[] = [];

  @Input()
  emailOfStudentToPreview = '';

  @Input()
  instructorsOfCourse: Instructor[] = [];

  @Input()
  emailOfInstructorToPreview = '';

  @Input()
  forDisplayOnly = false;

}
