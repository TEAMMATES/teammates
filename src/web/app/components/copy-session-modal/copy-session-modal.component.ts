import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Course, FeedbackSession } from '../../../types/api-output';
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

  @Input()
  baseSessionName: string = '';

  @Input()
  existingFeedbackSession: FeedbackSession[] = [];

  newFeedbackSessionName: string = '';
  copyToCourseSet: Set<string> = new Set<string>();

  constructor(public activeModal: NgbActiveModal) {}

  ngOnInit(): void {
    this.newFeedbackSessionName = this.baseSessionName || this.newFeedbackSessionName;
  }

  /**
   * Fires the copy event.
   */
  copy(): void {
    const base = this.baseSessionName;
    const userTyped = this.newFeedbackSessionName;

    // Only auto-rename if the user hasn't changed the prefill
    const needsAuto = !userTyped || userTyped === base;

    const finalName = needsAuto
      ? this.generateUniqueNameForCourses(base || userTyped, Array.from(this.copyToCourseSet))
      : userTyped;

    this.activeModal.close({
      newFeedbackSessionName: finalName,
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

    this.newFeedbackSessionName = this.generateUniqueNameForCourses(
      this.baseSessionName || this.newFeedbackSessionName,
      Array.from(this.copyToCourseSet),
    );
  }

  private generateUniqueNameForCourses(baseName: string, targetCourseIds: string[]): string {
    if (!baseName || targetCourseIds.length === 0) {
      return baseName;
    }

    const namesByCourse = new Map<string, Set<string>>();
    for (const cid of targetCourseIds) {
      const set = new Set(
        this.existingFeedbackSession
          .filter((s) => s.courseId === cid)
          .map((s) => s.feedbackSessionName),
      );
      namesByCourse.set(cid, set);
    }

    const isFreeEverywhere = [...namesByCourse.values()].every((set) => !set.has(baseName));
    if (isFreeEverywhere) return baseName;

    let i = 1;
    while (true) {
      const candidate = `${baseName} (${i})`;
      const ok = [...namesByCourse.values()].every((set) => !set.has(candidate));
      if (ok) return candidate;
      i += 1;
    }
  }
}
