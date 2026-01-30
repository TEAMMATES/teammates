import { Component, Input } from '@angular/core';
import {
  Instructor,
  Student,
} from '../../../types/api-output';
import { NgClass, NgFor, NgIf, NgTemplateOutlet } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TeammatesRouterDirective } from '../teammates-router/teammates-router.directive';
import { NgbTooltip } from '@ng-bootstrap/ng-bootstrap';

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
    NgFor,
    NgIf,
    TeammatesRouterDirective,
    NgbTooltip,
    NgTemplateOutlet,
  ],
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
