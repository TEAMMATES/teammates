import { Component, Input } from '@angular/core';
import {
  Instructor,
  Student,
} from '../../../types/api-output';

/**
 * Displaying the preview session panel.
 */
@Component({
  selector: 'tm-preview-session-result-panel',
  templateUrl: './preview-session-result-panel.component.html',
  styleUrls: ['./preview-session-result-panel.component.scss'],
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
