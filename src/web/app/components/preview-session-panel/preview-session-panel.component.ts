import { NgClass, NgTemplateOutlet } from '@angular/common';
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
  selector: 'tm-preview-session-panel',
  templateUrl: './preview-session-panel.component.html',
  styleUrls: ['./preview-session-panel.component.scss'],
  imports: [
    NgClass,
    FormsModule,
    TeammatesRouterDirective,
    NgbTooltip,
    NgTemplateOutlet,
],
})
export class PreviewSessionPanelComponent {

  @Input()
  courseId = '';

  @Input()
  feedbackSessionName = '';

  @Input()
  feedbackSessionId = '';

  @Input()
  emailOfStudentToPreview = '';

  @Input()
  studentsOfCourse: Student[] = [];

  @Input()
  instructorsCanBePreviewedAs: Instructor[] = [];

  @Input()
  emailOfInstructorToPreview = '';

  @Input()
  forDisplayOnly = false;

}
