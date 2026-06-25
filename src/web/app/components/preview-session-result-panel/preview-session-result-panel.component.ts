import { NgTemplateOutlet } from '@angular/common';
import { Component, Input } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbTooltip } from '@ng-bootstrap/ng-bootstrap/tooltip';
import { Instructor, Student } from '../../../types/api-output';
import { RouterLink } from '@angular/router';

/**
 * Displaying the preview session panel.
 */
@Component({
  selector: 'tm-preview-session-result-panel',
  templateUrl: './preview-session-result-panel.component.html',
  styleUrls: ['./preview-session-result-panel.component.scss'],
  imports: [FormsModule, RouterLink, NgbTooltip, NgTemplateOutlet],
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
  userIdOfStudentToPreview = '';

  @Input()
  instructorsOfCourse: Instructor[] = [];

  @Input()
  userIdOfInstructorToPreview = '';

  @Input()
  forDisplayOnly = false;
}
