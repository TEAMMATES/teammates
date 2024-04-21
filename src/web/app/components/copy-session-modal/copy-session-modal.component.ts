import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Course } from '../../../types/api-output';
import { FEEDBACK_SESSION_NAME_MAX_LENGTH } from '../../../types/field-validator';

/**
 * Copy current session modal.
 */
@Component({
  selector: 'tm-copy-session-modal',
  templateUrl: './copy-session-modal.component.html',
  styleUrls: ['./copy-session-modal.component.scss'],
})
export class CopySessionModalComponent {

  // const
  FEEDBACK_SESSION_NAME_MAX_LENGTH: number = FEEDBACK_SESSION_NAME_MAX_LENGTH;

  @Input()
  courseCandidates: Course[] = [];

  @Input()
  sessionToCopyCourseId: string = '';

  newFeedbackSessionName: string = '';
  copyToCourseSet: Set<string> = new Set<string>();

  constructor(public activeModal: NgbActiveModal) {}

  /**
   * Fires the copy event.
   */
  copy(): void {
    if(this.validatenewFeedbackSessionName(this.newFeedbackSessionName)){
      this.activeModal.close({
        newFeedbackSessionName: this.newFeedbackSessionName,
        sessionToCopyCourseId: this.sessionToCopyCourseId,
        copyToCourseList: Array.from(this.copyToCourseSet),
      });
    }
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
  /**
   * validation of newFeedbackSessionName for a course to copy to in set.
   */
  validatenewFeedbackSessionName(newFeedbackSessionName: string): boolean {
    if (newFeedbackSessionName !== null) { // Add a null check
        const regex_pattern = /^(?!\s*$).+/;
        if (regex_pattern.test(newFeedbackSessionName)) {
          return true;
          } else {
          return false;
        }
    } else {
       return true; // If newFeedbackSessionName is null, consider it invalid
    }
}
}
