import { Component, Input, OnInit } from '@angular/core';
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
export class CopySessionModalComponent implements OnInit {

  // const
  FEEDBACK_SESSION_NAME_MAX_LENGTH: number = FEEDBACK_SESSION_NAME_MAX_LENGTH;

  @Input()
  courseCandidates: Course[] = [];

  @Input()
  sessionToCopyCourseId: string = '';

  newFeedbackSessionName: string = '';
  copyToCourseSet: Set<string> = new Set<string>();
  originalSessionName: string = '';
  isNameCollision: boolean = false;

  constructor(public activeModal: NgbActiveModal) {}

  ngOnInit(): void {
    this.originalSessionName = this.newFeedbackSessionName;
    this.checkNameCollision();
  }

  /**
   * Fires the copy event.
   */
  copy(): void {
    if (this.isNameCollision) {
      return;
    }
    this.activeModal.close({
      newFeedbackSessionName: this.newFeedbackSessionName,
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
    this.checkNameCollision();
  }

  /**
   * Checks for name collision.
   */
  checkNameCollision(): void {
    this.isNameCollision = this.newFeedbackSessionName === this.originalSessionName
      && this.copyToCourseSet.has(this.sessionToCopyCourseId);
  }
}
