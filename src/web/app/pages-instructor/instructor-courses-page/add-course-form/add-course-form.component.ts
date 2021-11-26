import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { finalize } from 'rxjs/operators';
import { CourseService } from '../../../../services/course.service';
import { FeedbackSessionsService } from '../../../../services/feedback-sessions.service';
import { StatusMessageService } from '../../../../services/status-message.service';
import { TimezoneService } from '../../../../services/timezone.service';
import { Course, FeedbackSessions } from '../../../../types/api-output';
import { CopyCourseModalResult } from '../../../components/copy-course-modal/copy-course-modal-model';
import { CopyCourseModalComponent } from '../../../components/copy-course-modal/copy-course-modal.component';
import { ErrorMessageOutput } from '../../../error-message-output';

interface Timezone {
  id: string;
  offset: string;
}

const formatTwoDigits: Function = (n: number): string => {
  if (n < 10) {
    return `0${n}`;
  }
  return String(n);
};

/**
 * Instructor add new course form
 */
@Component({
  selector: 'tm-add-course-form',
  templateUrl: './add-course-form.component.html',
  styleUrls: ['./add-course-form.component.scss'],
})
export class AddCourseFormComponent implements OnInit {

  @Input() isEnabled: boolean = true;
  @Input() isCopyingCourse: boolean = false;
  @Input() activeCourses: Course[] = [];
  @Input() allCourses: Course[] = [];
  @Output() courseAdded: EventEmitter<void> = new EventEmitter<void>();
  @Output() closeCourseFormEvent: EventEmitter<void> = new EventEmitter<void>();
  @Output() copyCourseEvent: EventEmitter<CopyCourseModalResult> = new EventEmitter<CopyCourseModalResult>();

  timezones: Timezone[] = [];
  timezone: string = '';
  newCourseId: string = '';
  newCourseName: string = '';
  isAddingCourse: boolean = false;

  constructor(private statusMessageService: StatusMessageService,
              private courseService: CourseService,
              private timezoneService: TimezoneService,
              private feedbackSessionsService: FeedbackSessionsService,
              private ngbModal: NgbModal) { }

  ngOnInit(): void {
    for (const [id, offset] of Object.entries(this.timezoneService.getTzOffsets())) {
      const hourOffset: number = Math.floor(Math.abs(offset) / 60);
      const minOffset: number = Math.abs(offset) % 60;
      const sign: string = offset < 0 ? '-' : '+';
      this.timezones.push({
        id,
        offset: offset === 0 ? 'UTC' : `UTC ${sign}${formatTwoDigits(hourOffset)}:${formatTwoDigits(minOffset)}`,
      });
    }

    this.timezone = this.timezoneService.guessTimezone();
  }

  /**
   * Auto-detects timezone for instructor.
   */
  onAutoDetectTimezone(): void {
    if (!this.isEnabled) {
      return;
    }
    this.timezone = this.timezoneService.guessTimezone();
  }

  /**
   * Submits the data to add the new course.
   */
  onSubmit(): void {
    if (!this.isEnabled) {
      return;
    }
    if (!this.newCourseId || !this.newCourseName) {
      this.statusMessageService.showErrorToast(
          'Please make sure you have filled in both Course ID and Name before adding the course!');
      return;
    }

    this.isAddingCourse = true;
    this.courseService.createCourse({
      courseName: this.newCourseName,
      timeZone: this.timezone,
      courseId: this.newCourseId,
    }).pipe(finalize(() => this.isAddingCourse = false)).subscribe(() => {
      this.courseAdded.emit();
      this.statusMessageService.showSuccessToast('The course has been added.');
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorToast(resp.error.message);
    });
    this.newCourseId = '';
    this.newCourseName = '';
    this.timezone = this.timezoneService.guessTimezone();
  }

  /**
   * Handles closing of the edit form.
   */
  closeEditFormHandler(): void {
    this.closeCourseFormEvent.emit();
  }

  /**
   * Handles copying from other courses event
   */
  onCopy(): void {
    const modalRef: NgbModalRef = this.ngbModal.open(CopyCourseModalComponent);
    modalRef.componentInstance.isCopyFromOtherSession = true;
    modalRef.componentInstance.allCourses = this.allCourses;
    modalRef.componentInstance.activeCourses = this.activeCourses;

    modalRef.componentInstance.fetchFeedbackSessionsEvent.subscribe((courseId: string) => {
      this.feedbackSessionsService
        .getFeedbackSessionsForInstructor(courseId)
        .subscribe((feedbackSessions: FeedbackSessions) => {
          modalRef.componentInstance.courseToFeedbackSession[courseId] = [...feedbackSessions.feedbackSessions];
        });
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorToast(resp.error.message);
    });

    modalRef.result.then((result: CopyCourseModalResult) => this.copyCourseEvent.emit(result),
      (resp: ErrorMessageOutput) => this.statusMessageService.showErrorToast(resp.error.message));
  }
}
