import { NgClass } from '@angular/common';
import { Component, Input, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { Course } from '../../../types/api-output';
import { FEEDBACK_SESSION_NAME_MAX_LENGTH } from '../../../types/field-validator';

/**
 * Copy current session modal.
 */
@Component({
  selector: 'tm-copy-session-modal',
  templateUrl: './copy-session-modal.component.html',
  styleUrls: ['./copy-session-modal.component.scss'],
  imports: [FormsModule, NgClass],
})
export class CopySessionModalComponent {
  activeModal = inject(NgbActiveModal);

  // const
  FEEDBACK_SESSION_NAME_MAX_LENGTH!: number;

  @Input()
  courseCandidates: Course[] = [];

  @Input()
  sessionToCopyCourseId = '';

  @Input()
  sessionToCopyName = '';

  newFeedbackSessionName = '';
  copyToCourseSet: Set<string> = new Set<string>();

  constructor() {
    this.FEEDBACK_SESSION_NAME_MAX_LENGTH = FEEDBACK_SESSION_NAME_MAX_LENGTH;
  }

  /**
   * Whether copied session name is non-empty after trimming whitespace.
   */
  get isNewFeedbackSessionNameValid(): boolean {
    return this.newFeedbackSessionName.trim().length > 0;
  }

  /**
   * Whether the new session name is identical to the source session name when copying to the same course.
   */
  get isNewFeedbackSessionNameSameAsSource(): boolean {
    return this.copyToCourseSet.has(this.sessionToCopyCourseId) &&
      this.newFeedbackSessionName.trim().toLowerCase() === this.sessionToCopyName.trim().toLowerCase();
  }

  /**
   * Fires the copy event.
   */
  copy(): void {
    this.activeModal.close({
      newFeedbackSessionName: this.newFeedbackSessionName.trim(),
      sessionToCopyCourseId: this.sessionToCopyCourseId,
      copyToCourseList: Array.from(this.copyToCourseSet),
    });
  }

  /**
   * Toggles selection of course to copy to in set.
   */
  select(courseId: string): void {
    if (this.copyToCourseSet.has(courseId)) {
      this.copyToCourseSet.delete(courseId);
    } else {
      this.copyToCourseSet.add(courseId);
    }
  }
}
