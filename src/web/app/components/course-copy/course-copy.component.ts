import { Component, EventEmitter, Input, Output, TemplateRef, ViewChild } from '@angular/core';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import moment from 'moment-timezone';
import { finalize, Observable } from 'rxjs';
import { CourseService } from 'src/web/services/course.service';
import { ProgressBarService } from 'src/web/services/progress-bar.service';
import { SimpleModalService } from 'src/web/services/simple-modal.service';
import { FeedbackSessionCreateRequest, SessionVisibleSetting } from 'src/web/types/api-request';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { TimezoneService } from '../../../services/timezone.service';
import { Course, FeedbackSession, FeedbackSessions, ResponseVisibleSetting } from '../../../types/api-output';
import { ErrorMessageOutput } from '../../error-message-output';
import { CopyCourseModalResult } from '../copy-course-modal/copy-course-modal-model';
import { CopyCourseModalComponent } from '../copy-course-modal/copy-course-modal.component';
import { SimpleModalType } from '../simple-modal/simple-modal-type';
import { CourseAddFormModel, DEFAULT_COURSE_ADD_FORM_MODEL } from './course-copy-model';

interface SessionTimestampData {
  submissionStartTimestamp: string;
  submissionEndTimestamp: string;
  sessionVisibleTimestamp: string;
  responseVisibleTimestamp: string;
}

interface TweakedTimestampData {
  oldTimestamp: SessionTimestampData;
  newTimestamp: SessionTimestampData;
}

/**
 * Course edit form component.
 */
@Component({
  selector: '[course-copy]',
  templateUrl: './course-copy.component.html',
  styleUrls: ['./course-copy.component.scss'],
})
export class CourseCopyComponent {
  @ViewChild('modifiedTimestampsModal') modifiedTimestampsModal!: TemplateRef<any>;

  @Output() isCopyingCourse = new EventEmitter<boolean>(false);
  @Output() onCourseCopy = new EventEmitter<Course>(false);

  @Input() allCoursesList: Course[] = [];

  hasLoadingFailed: boolean = false;
  modifiedSessions: Record<string, TweakedTimestampData> = {};
  copyProgressPercentage: number = 0;
  totalNumberOfSessionsToCopy: number = 0;
  numberOfSessionsCopied: number = 0;
  courseFormModel: CourseAddFormModel = DEFAULT_COURSE_ADD_FORM_MODEL();

  constructor(private ngbModal: NgbModal,
    private statusMessageService: StatusMessageService,
    private courseService: CourseService,
    private simpleModalService: SimpleModalService,
    private feedbackSessionsService: FeedbackSessionsService,
    private progressBarService: ProgressBarService,
    private timezoneService: TimezoneService) {}

  setIsCopyingCourse(value: boolean): void {
    this.isCopyingCourse.emit(value);
    this.courseFormModel.isCopying = value;
  }

  /**
   * Creates a copy of a course including the selected sessions.
   */
  onCopy(courseId: string, courseName: string, timeZone: string): void {
    if (!courseId) {
      this.statusMessageService.showErrorToast('Course is not found!');
      return;
    }

    this.feedbackSessionsService.getFeedbackSessionsForInstructor(courseId).subscribe({
      next: (response: FeedbackSessions) => {
        const modalRef: NgbModalRef = this.ngbModal.open(CopyCourseModalComponent);
        modalRef.componentInstance.oldCourseId = courseId;
        modalRef.componentInstance.oldCourseName = courseName;
        modalRef.componentInstance.allCourses = this.allCoursesList;
        modalRef.componentInstance.newTimeZone = timeZone;
        modalRef.componentInstance.courseToFeedbackSession[courseId] = response.feedbackSessions;
        modalRef.componentInstance.selectedFeedbackSessions = new Set(response.feedbackSessions);
        modalRef.result.then((result: CopyCourseModalResult) => this.createCopiedCourse(result), () => {
        });
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
  }

  /**
   * Creates a new course with the selected feedback sessions
   */
  createCopiedCourse(result: CopyCourseModalResult): void {
    this.setIsCopyingCourse(true);
    this.modifiedSessions = {};
    this.numberOfSessionsCopied = 0;
    this.totalNumberOfSessionsToCopy = result.totalNumberOfSessions;
    this.copyProgressPercentage = 0;

    this.courseService.createCourse(result.newCourseInstitute, {
      courseName: result.newCourseName,
      timeZone: result.newTimeZone,
      courseId: result.newCourseId,
    })
    .subscribe({
      next: () => {
        // Wrap in a Promise to wait for all feedback sessions to be copied
        const promise: Promise<void> = new Promise<void>((resolve: () => void) => {
          if (result.selectedFeedbackSessionList.size === 0) {
            this.progressBarService.updateProgress(100);
            resolve();

            return;
          }

          result.selectedFeedbackSessionList.forEach((session: FeedbackSession) => {
            this.copyFeedbackSession(session, result.newCourseId, result.newTimeZone, result.oldCourseId)
                .pipe(finalize(() => {
                  this.numberOfSessionsCopied += 1;
                  this.copyProgressPercentage =
                      Math.round(100 * this.numberOfSessionsCopied / this.totalNumberOfSessionsToCopy);
                  this.progressBarService.updateProgress(this.copyProgressPercentage);

                  if (this.numberOfSessionsCopied === this.totalNumberOfSessionsToCopy) {
                    resolve();
                  }
                }))
                .subscribe();
          });
        });

        promise.then(() => {
          this.courseService
              .getCourseAsInstructor(result.newCourseId)
              .subscribe((course: Course) => {
                this.onCourseCopy.emit(course);
                this.setIsCopyingCourse(false);

                if (Object.keys(this.modifiedSessions).length > 0) {
                  this.simpleModalService.openInformationModal('Note On Modified Session Timings',
                      SimpleModalType.WARNING, this.modifiedTimestampsModal);
                } else {
                  this.statusMessageService.showSuccessToast('The course has been added.');
                }
              });
        });
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
        this.setIsCopyingCourse(false);
        this.hasLoadingFailed = true;
      },
    });
  }

  /**
   * Copies a feedback session.
   */
  private copyFeedbackSession(fromFeedbackSession: FeedbackSession, newCourseId: string,
                              newTimeZone: string, oldCourseId: string): Observable<FeedbackSession> {
    return this.feedbackSessionsService.createFeedbackSession(newCourseId,
        this.toFbSessionCreationReqWithName(fromFeedbackSession, newTimeZone, oldCourseId));
  }

  /**
   * Creates a FeedbackSessionCreateRequest with the provided name.
   */
  private toFbSessionCreationReqWithName(fromFeedbackSession: FeedbackSession, newTimeZone: string,
                                         oldCourseId: string): FeedbackSessionCreateRequest {
    // Local constants
    const twoHoursBeforeNow = moment().tz(newTimeZone).subtract(2, 'hours')
        .valueOf();
    const twoDaysFromNowRoundedUp = moment().tz(newTimeZone).add(2, 'days').startOf('hour')
        .valueOf();
    const sevenDaysFromNowRoundedUp = moment().tz(newTimeZone).add(7, 'days').startOf('hour')
        .valueOf();
    const ninetyDaysFromNow = moment().tz(newTimeZone).add(90, 'days')
        .valueOf();
    const ninetyDaysFromNowRoundedUp = moment().tz(newTimeZone).add(90, 'days').startOf('hour')
        .valueOf();
    const oneHundredAndEightyDaysFromNow = moment().tz(newTimeZone).add(180, 'days')
        .valueOf();
    const oneHundredAndEightyDaysFromNowRoundedUp = moment().tz(newTimeZone).add(180, 'days')
        .startOf('hour')
        .valueOf();

    // Preprocess timestamps to adhere to feedback session timestamps constraints
    let isModified = false;

    let copiedSubmissionStartTimestamp = fromFeedbackSession.submissionStartTimestamp;
    if (copiedSubmissionStartTimestamp < twoHoursBeforeNow) {
      copiedSubmissionStartTimestamp = twoDaysFromNowRoundedUp;
      isModified = true;
    } else if (copiedSubmissionStartTimestamp > ninetyDaysFromNow) {
      copiedSubmissionStartTimestamp = ninetyDaysFromNowRoundedUp;
      isModified = true;
    }

    let copiedSubmissionEndTimestamp = fromFeedbackSession.submissionEndTimestamp;
    if (copiedSubmissionEndTimestamp < copiedSubmissionStartTimestamp) {
      copiedSubmissionEndTimestamp = sevenDaysFromNowRoundedUp;
      isModified = true;
    } else if (copiedSubmissionEndTimestamp > oneHundredAndEightyDaysFromNow) {
      copiedSubmissionEndTimestamp = oneHundredAndEightyDaysFromNowRoundedUp;
      isModified = true;
    }

    let copiedSessionVisibleSetting = fromFeedbackSession.sessionVisibleSetting;
    let copiedCustomSessionVisibleTimestamp = fromFeedbackSession.customSessionVisibleTimestamp!;
    const thirtyDaysBeforeSubmissionStart = moment(copiedSubmissionStartTimestamp)
        .tz(newTimeZone).subtract(30, 'days')
        .valueOf();
    const thirtyDaysBeforeSubmissionStartRoundedUp = moment(copiedSubmissionStartTimestamp)
        .tz(newTimeZone).subtract(30, 'days').startOf('hour')
        .valueOf();
    if (copiedSessionVisibleSetting === SessionVisibleSetting.CUSTOM) {
      if (copiedCustomSessionVisibleTimestamp < thirtyDaysBeforeSubmissionStart) {
        copiedCustomSessionVisibleTimestamp = thirtyDaysBeforeSubmissionStartRoundedUp;
        isModified = true;
      } else if (copiedCustomSessionVisibleTimestamp > copiedSubmissionStartTimestamp) {
        copiedSessionVisibleSetting = SessionVisibleSetting.AT_OPEN;
        isModified = true;
      }
    }

    let copiedResponseVisibleSetting = fromFeedbackSession.responseVisibleSetting;
    const copiedCustomResponseVisibleTimestamp = fromFeedbackSession.customResponseVisibleTimestamp!;
    if (copiedResponseVisibleSetting === ResponseVisibleSetting.CUSTOM
        && ((copiedSessionVisibleSetting === SessionVisibleSetting.AT_OPEN
            && copiedCustomResponseVisibleTimestamp < copiedSubmissionStartTimestamp)
            || copiedCustomResponseVisibleTimestamp < copiedCustomSessionVisibleTimestamp)) {
      copiedResponseVisibleSetting = ResponseVisibleSetting.LATER;
      isModified = true;
    }

    if (isModified) {
      this.modifiedSessions[fromFeedbackSession.feedbackSessionName] = {
        oldTimestamp: {
          submissionStartTimestamp: this.formatTimestamp(fromFeedbackSession.submissionStartTimestamp,
              fromFeedbackSession.timeZone),
          submissionEndTimestamp: this.formatTimestamp(fromFeedbackSession.submissionEndTimestamp,
              fromFeedbackSession.timeZone),
          sessionVisibleTimestamp: fromFeedbackSession.sessionVisibleSetting === SessionVisibleSetting.AT_OPEN
              ? 'On submission opening time'
              : this.formatTimestamp(fromFeedbackSession.customSessionVisibleTimestamp!, fromFeedbackSession.timeZone),
          responseVisibleTimestamp: '',
        },
        newTimestamp: {
          submissionStartTimestamp: this.formatTimestamp(copiedSubmissionStartTimestamp, fromFeedbackSession.timeZone),
          submissionEndTimestamp: this.formatTimestamp(copiedSubmissionEndTimestamp, fromFeedbackSession.timeZone),
          sessionVisibleTimestamp: copiedSessionVisibleSetting === SessionVisibleSetting.AT_OPEN
              ? 'On submission opening time'
              : this.formatTimestamp(copiedCustomSessionVisibleTimestamp!, fromFeedbackSession.timeZone),
          responseVisibleTimestamp: '',
        },
      };

      if (fromFeedbackSession.responseVisibleSetting === ResponseVisibleSetting.AT_VISIBLE) {
        this.modifiedSessions[fromFeedbackSession.feedbackSessionName].oldTimestamp.responseVisibleTimestamp =
            'On session visible time';
      } else if (fromFeedbackSession.responseVisibleSetting === ResponseVisibleSetting.LATER) {
        this.modifiedSessions[fromFeedbackSession.feedbackSessionName].oldTimestamp.responseVisibleTimestamp =
            'Not now (publish manually)';
      } else {
        this.modifiedSessions[fromFeedbackSession.feedbackSessionName].oldTimestamp.responseVisibleTimestamp =
            this.formatTimestamp(fromFeedbackSession.customResponseVisibleTimestamp!, fromFeedbackSession.timeZone);
      }

      if (copiedResponseVisibleSetting === ResponseVisibleSetting.AT_VISIBLE) {
        this.modifiedSessions[fromFeedbackSession.feedbackSessionName].newTimestamp.responseVisibleTimestamp =
            'On session visible time';
      } else if (copiedResponseVisibleSetting === ResponseVisibleSetting.LATER) {
        this.modifiedSessions[fromFeedbackSession.feedbackSessionName].newTimestamp.responseVisibleTimestamp =
            'Not now (publish manually)';
      } else {
        this.modifiedSessions[fromFeedbackSession.feedbackSessionName].newTimestamp.responseVisibleTimestamp =
            this.formatTimestamp(copiedCustomResponseVisibleTimestamp!, fromFeedbackSession.timeZone);
      }
    }

    return {
      feedbackSessionName: fromFeedbackSession.feedbackSessionName,
      toCopyCourseId: oldCourseId,
      toCopySessionName: fromFeedbackSession.feedbackSessionName,
      instructions: fromFeedbackSession.instructions,

      submissionStartTimestamp: copiedSubmissionStartTimestamp,
      submissionEndTimestamp: copiedSubmissionEndTimestamp,
      gracePeriod: fromFeedbackSession.gracePeriod,

      sessionVisibleSetting: copiedSessionVisibleSetting,
      customSessionVisibleTimestamp: copiedCustomSessionVisibleTimestamp,

      responseVisibleSetting: copiedResponseVisibleSetting,
      customResponseVisibleTimestamp: fromFeedbackSession.customResponseVisibleTimestamp,

      isClosingEmailEnabled: fromFeedbackSession.isClosingEmailEnabled,
      isPublishedEmailEnabled: fromFeedbackSession.isPublishedEmailEnabled,
    };
  }

  private formatTimestamp(timestamp: number, timeZone: string): string {
    return this.timezoneService.formatToString(timestamp, timeZone, 'D MMM YYYY h:mm A');
  }
}
