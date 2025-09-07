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

  @Input()
  preselectedCourseIds: string[] = [];

  newFeedbackSessionName: string = '';
  copyToCourseSet: Set<string> = new Set<string>();

  private lastAutoName: string | null = null;
  nameClashError: string | null = null;

  constructor(public activeModal: NgbActiveModal) {}

  ngOnInit(): void {
    this.newFeedbackSessionName = this.baseSessionName || this.newFeedbackSessionName;
    this.lastAutoName = this.newFeedbackSessionName;

    if (this.preselectedCourseIds?.length) {
      this.copyToCourseSet = new Set(this.preselectedCourseIds);
      this.prefillNameForSelection();
    }
  }

  /**
   * Called when user types to clear and error and stop auto-overwrites
   */
  onNameChange(): void {
    this.nameClashError = null;
    this.lastAutoName = null;
  }

  /**
   * Only update name if the user has not edited it away from the last auto value.
   */
  private prefillNameForSelection(): void {
    const auto = this.generateUniqueNameForCourses(
      this.baseSessionName,
      Array.from(this.copyToCourseSet),
    );
    if (
      this.newFeedbackSessionName === this.baseSessionName
        || this.newFeedbackSessionName === this.lastAutoName
        || !this.newFeedbackSessionName
    ) {
      this.newFeedbackSessionName = auto;
      this.lastAutoName = auto;
    }
  }

  /**
   * Fires the copy event.
   */
  copy(): void {
    const name = (this.newFeedbackSessionName || '').trim();
    const targetCourseIds = Array.from(this.copyToCourseSet);

    if (!name || targetCourseIds.length === 0) {
      return;
    }

    const conflicts = this.findConflictingCourses(name, targetCourseIds);
    if (conflicts.length > 0) {
      this.nameClashError =
        `A session named "${name}" already exists in: ${conflicts.join(', ')}`;
      return;
    }

    this.activeModal.close({
      newFeedbackSessionName: name,
      sessionToCopyCourseId: this.sessionToCopyCourseId,
      copyToCourseList: targetCourseIds,
    });
  }

  /**
   * Finds courses with sessions with the same name as the session to be copied.
   */
  private findConflictingCourses(name: string, targetCourseIds: string[]): string[] {
    const namesByCourse = new Map<string, Set<string>>();
    for (const cid of targetCourseIds) {
      const set = new Set(
        this.existingFeedbackSession
          .filter((s) => s.courseId === cid)
          .map((s) => s.feedbackSessionName.trim()),
      );
      namesByCourse.set(cid, set);
    }
    const conflicts: string[] = [];
    for (const cid of targetCourseIds) {
      if (namesByCourse.get(cid)?.has(name)) {
        conflicts.push(cid);
      }
    }
    return conflicts;
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
    this.prefillNameForSelection();
    this.nameClashError = null;
  }

  private generateUniqueNameForCourses(baseName: string, targetCourseIds: string[]): string {
    const base = (baseName || '').trim();
    if (!base || targetCourseIds.length === 0) return base;

    const namesByCourse = new Map<string, Set<string>>();
    for (const cid of targetCourseIds) {
      const set = new Set(
        this.existingFeedbackSession
          .filter((s) => s.courseId === cid)
          .map((s) => s.feedbackSessionName.trim()),
      );
      namesByCourse.set(cid, set);
    }

    const isFreeEverywhere = [...namesByCourse.values()].every(set => !set.has(base));
    if (isFreeEverywhere) return base;

    let i = 1;
    while (true) {
      const candidate = `${base} (${i})`;
      if ([...namesByCourse.values()].every((set) => !set.has(candidate))) return candidate;
      i += 1;
    }
  }
}
