import { Component, Input } from '@angular/core';
import {
  Instructor,
  Student,
} from '../../../types/api-output';

/**
 * Displaying the preview session panel.
 */
@Component({
  selector: 'tm-preview-session-panel',
  templateUrl: './preview-session-panel.component.html',
  styleUrls: ['./preview-session-panel.component.scss'],
})
export class PreviewSessionPanelComponent {

  @Input()
  courseId: string = '';

  @Input()
  feedbackSessionName: string = '';

  @Input()
  emailOfStudentToPreview: string = '';

  @Input()
  studentsOfCourse: Student[] = [];

  @Input()
  instructorsCanBePreviewedAs: Instructor[] = [];

  @Input()
  emailOfInstructorToPreview: string = '';

  @Input()
  forDisplayOnly: boolean = false;

}
