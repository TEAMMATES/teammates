import { Component, EventEmitter, OnInit, Output, TemplateRef, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import moment from 'moment-timezone';
import { forkJoin, Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { CourseService } from '../../../services/course.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { ProgressBarService } from '../../../services/progress-bar.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { TableComparatorService } from '../../../services/table-comparator.service';
import { TimezoneService } from '../../../services/timezone.service';
import {
  Course,
  CourseArchive,
  Courses,
  FeedbackSession,
  FeedbackSessions,
  JoinState,
  MessageOutput,
  ResponseVisibleSetting,
  SessionVisibleSetting,
  Student,
  Students,
} from '../../../types/api-output';
import { FeedbackSessionCreateRequest } from '../../../types/api-request';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import { CopyCourseModalResult } from '../../components/copy-course-modal/copy-course-modal-model';
import { CopyCourseModalComponent } from '../../components/copy-course-modal/copy-course-modal.component';
import {
  CourseAddFormModel,
  CourseEditFormMode,
  DEFAULT_COURSE_ADD_FORM_MODEL,
} from '../../components/course-edit-form/course-edit-form-model';
import { SimpleModalType } from '../../components/simple-modal/simple-modal-type';
import { collapseAnim } from '../../components/teammates-common/collapse-anim';
import { ErrorMessageOutput } from '../../error-message-output';

interface CourseModel {
  course: Course;
  canModifyCourse: boolean;
  canModifyStudent: boolean;
  isLoadingCourseStats: boolean;
}

/**
 * Instructor courses list page.
 */
@Component({
  selector: 'tm-instructor-courses-page',
  templateUrl: './instructor-courses-page.component.html',
  styleUrls: ['./instructor-courses-page.component.scss'],
  animations: [collapseAnim],
})
export class InstructorCoursesPageComponent implements OnInit {

  activeCourses: CourseModel[] = [];
  archivedCourses: CourseModel[] = [];
  softDeletedCourses: CourseModel[] = [];
  allCoursesList: Course[] = [];
  activeCoursesList: Course[] = [];
  courseStats: Record<string, Record<string, number>> = {};
  courseFormModel: CourseAddFormModel = DEFAULT_COURSE_ADD_FORM_MODEL();
  resetCourseForm: EventEmitter<void> = new EventEmitter();

  activeTableSortOrder: SortOrder = SortOrder.ASC;
  activeTableSortBy: SortBy = SortBy.COURSE_CREATION_DATE;
  archivedTableSortOrder: SortOrder = SortOrder.ASC;
  archivedTableSortBy: SortBy = SortBy.COURSE_NAME;
  deletedTableSortOrder: SortOrder = SortOrder.ASC;
  deletedTableSortBy: SortBy = SortBy.COURSE_NAME;

  // enum
  SortBy: typeof SortBy = SortBy;
  SortOrder: typeof SortOrder = SortOrder;
  CourseEditFormMode: typeof CourseEditFormMode = CourseEditFormMode;

  isLoading: boolean = false;
  hasLoadingFailed: boolean = false;
  isRecycleBinExpanded: boolean = false;
  canDeleteAll: boolean = true;
  canRestoreAll: boolean = true;
  isAddNewCourseFormExpanded: boolean = false;
  isArchivedCourseExpanded: boolean = false;
  isCopyingCourse: boolean = false;

  copyProgressPercentage: number = 0;
  totalNumberOfSessionsToCopy: number = 0;
  numberOfSessionsCopied: number = 0;

  modifiedSessions: Record<string, TweakedTimestampData> = {};

  @Output() courseAdded: EventEmitter<void> = new EventEmitter<void>();

  @ViewChild('modifiedTimestampsModal') modifiedTimestampsModal!: TemplateRef<any>;

  constructor(private ngbModal: NgbModal,
              private route: ActivatedRoute,
              private statusMessageService: StatusMessageService,
              private courseService: CourseService,
              private studentService: StudentService,
              private simpleModalService: SimpleModalService,
              private tableComparatorService: TableComparatorService,
              private feedbackSessionsService: FeedbackSessionsService,
              private progressBarService: ProgressBarService,
              private timezoneService: TimezoneService) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      if (queryParams.isAddNewCourse) {
        this.isAddNewCourseFormExpanded = queryParams.isAddNewCourse;
      }
      this.loadInstructorCourses();
    });
  }

  setIsCopyingCourse(value: boolean): void {
    this.isCopyingCourse = value;
    this.courseFormModel.isCopying = value;
  }

  /**
   * Loads instructor courses required for this page.
   */
  loadInstructorCourses(): void {
    this.hasLoadingFailed = false;
    this.isLoading = true;
    this.activeCourses = [];
    this.archivedCourses = [];
    this.softDeletedCourses = [];
    this.activeCoursesList = [];
    this.allCoursesList = [];
    this.courseService.getAllCoursesAsInstructor('active').subscribe({
      next: (resp: Courses) => {
        resp.courses.forEach((course: Course) => {
          this.allCoursesList.push(course);
          this.activeCoursesList.push(course);
          let canModifyCourse: boolean = false;
          let canModifyStudent: boolean = false;
          if (course.privileges) {
            canModifyCourse = course.privileges.canModifyCourse;
            canModifyStudent = course.privileges.canModifyStudent;
          }
          const isLoadingCourseStats: boolean = false;
          const activeCourse: CourseModel = {
            course, canModifyCourse, canModifyStudent, isLoadingCourseStats,
          };
          this.activeCourses.push(activeCourse);
        });
        this.activeCoursesDefaultSort();
        this.isLoading = false;
      },
      error: (resp: ErrorMessageOutput) => {
        this.isLoading = false;
        this.hasLoadingFailed = true;
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });

    this.courseService.getAllCoursesAsInstructor('archived').subscribe({
      next: (resp: Courses) => {
        for (const course of resp.courses) {
          this.allCoursesList.push(course);
          let canModifyCourse: boolean = false;
          let canModifyStudent: boolean = false;
          if (course.privileges) {
            canModifyCourse = course.privileges.canModifyCourse;
            canModifyStudent = course.privileges.canModifyStudent;
          }
          const isLoadingCourseStats: boolean = false;
          const archivedCourse: CourseModel = {
            course, canModifyCourse, canModifyStudent, isLoadingCourseStats,
          };
          this.archivedCourses.push(archivedCourse);
          this.archivedCoursesDefaultSort();
        }
      },
      error: (resp: ErrorMessageOutput) => {
        this.hasLoadingFailed = true;
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });

    this.courseService.getAllCoursesAsInstructor('softDeleted').subscribe({
      next: (resp: Courses) => {
        for (const course of resp.courses) {
          this.allCoursesList.push(course);
          let canModifyCourse: boolean = false;
          let canModifyStudent: boolean = false;
          if (course.privileges) {
            canModifyCourse = course.privileges.canModifyCourse;
            canModifyStudent = course.privileges.canModifyStudent;
          }
          const isLoadingCourseStats: boolean = false;
          const softDeletedCourse: CourseModel = {
            course, canModifyCourse, canModifyStudent, isLoadingCourseStats,
          };
          this.softDeletedCourses.push(softDeletedCourse);
          this.deletedCoursesDefaultSort();
          if (!softDeletedCourse.canModifyCourse) {
            this.canDeleteAll = false;
            this.canRestoreAll = false;
          }
        }
      },
      error: (resp: ErrorMessageOutput) => {
        this.hasLoadingFailed = true;
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });

    this.courseFormModel.activeCourses = this.activeCoursesList;
    this.courseFormModel.allCourses = this.allCoursesList;
  }

  /**
   * Constructs the url for course stats from the given course id.
   */
  getCourseStats(idx: number): void {
    const course: CourseModel = this.activeCourses[idx];
    const courseId: string = course.course.courseId;
    if (!courseId) {
      this.statusMessageService.showErrorToast(`Course ${courseId} is not found!`);
      return;
    }
    course.isLoadingCourseStats = true;
    this.studentService.getStudentsFromCourse({ courseId })
        .pipe(finalize(() => {
          course.isLoadingCourseStats = false;
        }))
        .subscribe({
          next: (students: Students) => {
            this.courseStats[courseId] = {
              sections: (new Set(students.students.map((value: Student) => value.sectionName))).size,
              teams: (new Set(students.students.map((value: Student) => value.teamName))).size,
              students: students.students.length,
              unregistered: students.students
                  .filter((value: Student) => value.joinState === JoinState.NOT_JOINED).length,
            };
          },
          error: (resp: ErrorMessageOutput) => {
            this.statusMessageService.showErrorToast(resp.error.message);
          },
        });
  }

  /**
   * Changes the status of an archived course.
   */
  changeArchiveStatus(courseId: string, toArchive: boolean): void {
    if (!courseId) {
      this.statusMessageService.showErrorToast(`Course ${courseId} is not found!`);
      return;
    }
    this.courseService.changeArchiveStatus(courseId, {
      archiveStatus: toArchive,
    }).subscribe({
      next: (courseArchive: CourseArchive) => {
        if (courseArchive.isArchived) {
          this.changeModelFromActiveToArchived(courseId);
          this.statusMessageService.showSuccessToast(`The course ${courseId} has been archived.
          It will not appear on the home page anymore.`);
        } else {
          this.changeModelFromArchivedToActive(courseId);
          this.statusMessageService.showSuccessToast('The course has been unarchived.');
        }
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
  }

  /**
   * Moves a course model from active courses list to archived list.
   * This is to reduce the need to refresh the entire list of courses multiple times.
   */
  changeModelFromActiveToArchived(courseId: string): void {
    const courseToBeRemoved: CourseModel | undefined = this.findCourse(this.activeCourses, courseId);
    this.activeCourses = this.removeCourse(this.activeCourses, courseId);
    this.activeCoursesList = this.activeCourses.map((courseModel: CourseModel) => courseModel.course);
    if (courseToBeRemoved !== undefined) {
      this.archivedCourses.push(courseToBeRemoved);
      this.archivedCourses.sort(this.sortBy(this.archivedTableSortBy, this.archivedTableSortOrder));
    }
  }

  /**
   * Moves a course model from archived courses list to active list.
   * This is to reduce the need to refresh the entire list of courses multiple times.
   */
  changeModelFromArchivedToActive(courseId: string): void {
    const courseToBeRemoved: CourseModel | undefined = this.findCourse(this.archivedCourses, courseId);
    this.archivedCourses = this.removeCourse(this.archivedCourses, courseId);
    if (courseToBeRemoved !== undefined) {
      this.activeCourses.push(courseToBeRemoved);
      this.activeCoursesList = this.activeCourses.map((courseModel: CourseModel) => courseModel.course);
      this.activeCourses.sort(this.sortBy(this.activeTableSortBy, this.activeTableSortOrder));
    }
  }

  /**
   * Creates new course
   */
  createNewCourse(): void {
    this.courseFormModel.isSaving = true;
    this.courseService.createCourse(this.courseFormModel.course.institute, {
      courseName: this.courseFormModel.course.courseName,
      timeZone: this.courseFormModel.course.timeZone,
      courseId: this.courseFormModel.course.courseId,
    }).pipe(finalize(() => {
      this.courseFormModel.isSaving = false;
    })).subscribe({
      next: () => {
        this.statusMessageService.showSuccessToast('The course has been added.');
        this.courseFormModel.course.courseId = '';
        this.courseFormModel.course.courseName = '';
        this.resetCourseForm.emit();
        this.loadInstructorCourses();
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
  }

  /**
   * Finds and returns a course from the target course list.
   */
  findCourse(targetList: CourseModel[], courseId: string): CourseModel | undefined {
    return targetList.find((model: CourseModel) => {
      return model.course.courseId === courseId;
    });
  }

  /**
   * Removes a course from the target course list and returns the result list.
   */
  removeCourse(targetList: CourseModel[], courseId: string): CourseModel[] {
    return targetList.filter((model: CourseModel) => {
      return model.course.courseId !== courseId;
    });
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
                this.activeCourses.push(this.getCourseModelFromCourse(course));
                this.activeCoursesList.push(course);
                this.allCoursesList.push(course);
                this.activeCoursesDefaultSort();
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
   * Gets a CourseModel from courseID
   */
  private getCourseModelFromCourse(course: Course): CourseModel {
    let canModifyCourse: boolean = false;
    let canModifyStudent: boolean = false;
    if (course.privileges) {
      canModifyCourse = course.privileges.canModifyCourse;
      canModifyStudent = course.privileges.canModifyStudent;
    }
    const isLoadingCourseStats: boolean = false;
    return { course, canModifyCourse, canModifyStudent, isLoadingCourseStats };
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

  /**
   * Moves an active/archived course to Recycle Bin.
   */
  onDelete(courseId: string): Promise<void> {
    if (!courseId) {
      this.statusMessageService.showErrorToast(`Course ${courseId} is not found!`);
      return Promise.resolve();
    }
    const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
        'Warning: The course will be moved to the recycle bin.',
        SimpleModalType.WARNING, 'Are you sure you want to continue?');
    return modalRef.result.then(() => {
      this.courseService.binCourse(courseId).subscribe({
        next: (course: Course) => {
          this.moveCourseToRecycleBin(courseId, course.deletionTimestamp);
          this.statusMessageService.showSuccessToast(
              `The course ${course.courseId} has been deleted. You can restore it from the Recycle Bin manually.`);
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
    }).catch(() => {});
  }

  /**
   * Moves an active/archived course to Recycle Bin.
   * This is to reduce the need to refresh the entire list of courses multiple times.
   */
  moveCourseToRecycleBin(courseId: string, deletionTimeStamp: number): void {
    const activeCourseToBeRemoved: CourseModel | undefined = this.findCourse(this.activeCourses, courseId);
    this.activeCourses = this.removeCourse(this.activeCourses, courseId);
    this.activeCoursesList = this.activeCourses.map((courseModel: CourseModel) => courseModel.course);
    if (activeCourseToBeRemoved) {
      activeCourseToBeRemoved.course.deletionTimestamp = deletionTimeStamp;
      this.softDeletedCourses.push(activeCourseToBeRemoved);
      this.softDeletedCourses.sort(this.sortBy(this.deletedTableSortBy, this.deletedTableSortOrder));
    } else {
      const archivedCourseToBeRemoved: CourseModel | undefined = this.findCourse(this.archivedCourses, courseId);
      this.archivedCourses = this.removeCourse(this.archivedCourses, courseId);
      if (archivedCourseToBeRemoved !== undefined) {
        archivedCourseToBeRemoved.course.deletionTimestamp = deletionTimeStamp;
        this.softDeletedCourses.push(archivedCourseToBeRemoved);
        this.softDeletedCourses.sort(this.sortBy(this.deletedTableSortBy, this.deletedTableSortOrder));
      }
    }
  }

  /**
   * Permanently deletes a soft-deleted course in Recycle Bin.
   */
  onDeletePermanently(courseId: string): Promise<void> {
    if (!courseId) {
      this.statusMessageService.showErrorToast(`Course ${courseId} is not found!`);
      return Promise.resolve();
    }

    const institute: string = this.allCoursesList.find(
      (course: Course) => course.courseId === courseId)?.institute ?? '';
    const numTotalCourses: number = this.allCoursesList.length;
    const numCoursesFromSameInstitute: number = this.allCoursesList.filter(
      (course: Course) => course.institute === institute).length;

    const modalContent: string = `<strong>Are you sure you want to permanently delete ${courseId}?</strong><br>
      This operation will delete all students and sessions in these courses.
      All instructors of these courses will not be able to access them hereafter as well.`;

    const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
      `Delete course <strong>${courseId}</strong> permanently?`, SimpleModalType.DANGER, modalContent);
    modalRef.componentInstance.courseId = courseId;

    return modalRef.result.then(() => {
     if (numTotalCourses === 1 || numCoursesFromSameInstitute === 1) {
        const finalConfModalContent = numTotalCourses === 1
          ? `This is your last course on TEAMMATES for which you have instructor access. 
            Deleting this course will <mark><strong>remove your instructor access</strong></mark> to TEAMMATES.<br>
            Are you sure you want to delete the course <strong>${courseId}</strong>?`
          : `If you delete all courses of institute <strong>${institute}</strong>, 
            you will <mark><strong>lose instructor access</strong></mark> 
            to TEAMMATES under the institution <strong>${institute}</strong>. 
            To retain access, ensure you keep at least one course for each institution you are an instructor of.<br>
            Are you sure you want to delete the course <strong>${courseId}</strong> in institution
            <strong>${institute}</strong>?`;
        return this.simpleModalService.openConfirmationModal(
          'This action will cause you to <mark><strong>lose access</strong></mark> to TEAMMATES!',
          SimpleModalType.DANGER, finalConfModalContent).result;
      }
      return Promise.resolve();
    }).then(() => {
      this.courseService.deleteCourse(courseId).subscribe({
        next: () => {
          this.softDeletedCourses = this.removeCourse(this.softDeletedCourses, courseId);
          this.allCoursesList = this.allCoursesList.filter((course: Course) => course.courseId !== courseId);
          this.statusMessageService.showSuccessToast(`The course ${courseId} has been permanently deleted.`);
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
    }).catch(() => {});
  }

  /**
   * Restores a soft-deleted course from Recycle Bin.
   */
  onRestore(courseId: string): void {
    if (!courseId) {
      this.statusMessageService.showErrorToast(`Course ${courseId} is not found!`);
      return;
    }

    this.courseService.restoreCourse(courseId).subscribe({
      next: (resp: MessageOutput) => {
        this.loadInstructorCourses();
        this.statusMessageService.showSuccessToast(resp.message);
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
  }

  /**
   * Permanently deletes all soft-deleted courses in Recycle Bin.
   */
  onDeleteAll(): void {
    const modalContent: string =
        `<strong>Are you sure you want to permanently delete all the courses in the Recycle Bin?</strong><br>
        This operation will delete all students and sessions in these courses.
        All instructors of these courses will not be able to access them hereafter as well.`;

    const lastCourseRemaining: boolean = this.allCoursesList.length === this.softDeletedCourses.length;

    const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
      'Deleting all courses permanently?', SimpleModalType.DANGER, modalContent);

    modalRef.result.then(() => {
      if (lastCourseRemaining) {
        const modalContentCnf: string =
          `These are your last courses registered on TEAMMATES for which you have instructor access. 
          Deleting these courses will <mark><strong>remove your instructor access</strong></mark> to TEAMMATES.<br>
          Are you sure you want to permanently delete these courses?`;

        return this.simpleModalService.openConfirmationModal(
          'This action will cause you to <mark><strong>lose access</strong></mark> to TEAMMATES!',
          SimpleModalType.DANGER, modalContentCnf).result;
      }
      return Promise.resolve();
    }).then(() => {
      const deleteRequests: Observable<MessageOutput>[] = [];
      this.softDeletedCourses.forEach((courseToDelete: CourseModel) => {
        deleteRequests.push(this.courseService.deleteCourse(courseToDelete.course.courseId));
      });

      forkJoin(deleteRequests).subscribe({
        next: () => {
          this.softDeletedCourses = [];
          this.allCoursesList = [];
          this.allCoursesList.push(...this.activeCourses.map((courseModel: CourseModel) => courseModel.course));
          this.allCoursesList.push(...this.archivedCourses.map((courseModel: CourseModel) => courseModel.course));
          this.statusMessageService.showSuccessToast('All courses have been permanently deleted.');
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
    }).catch(() => {});
  }

  /**
   * Restores all soft-deleted courses from Recycle Bin.
   */
  onRestoreAll(): void {
    const restoreRequests: Observable<MessageOutput>[] = [];
    this.softDeletedCourses.forEach((courseToRestore: CourseModel) => {
      restoreRequests.push(this.courseService.restoreCourse(courseToRestore.course.courseId));
    });

    forkJoin(restoreRequests).subscribe({
      next: () => {
        this.loadInstructorCourses();
        this.statusMessageService.showSuccessToast('All courses have been restored.');
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
  }

  /**
   * Sorts the active courses table
   */
  sortCoursesEvent(by: SortBy): void {
    this.activeTableSortOrder = this.activeTableSortBy === by && this.activeTableSortOrder === SortOrder.ASC
        ? SortOrder.DESC : SortOrder.ASC;
    this.activeTableSortBy = by;
    this.activeCourses.sort(this.sortBy(by, this.activeTableSortOrder));
  }

  /**
   * Active courses default sort on page load
   */
  activeCoursesDefaultSort(): void {
    this.activeTableSortBy = SortBy.COURSE_CREATION_DATE;
    this.activeTableSortOrder = SortOrder.DESC;
    this.activeCourses.sort(this.sortBy(this.activeTableSortBy, this.activeTableSortOrder));
  }

  /**
   * Sorts the archived courses table
   */
  sortArchivedCoursesEvent(by: SortBy): void {
    this.archivedTableSortOrder = this.archivedTableSortBy === by && this.archivedTableSortOrder === SortOrder.ASC
        ? SortOrder.DESC : SortOrder.ASC;
    this.archivedTableSortBy = by;
    this.archivedCourses.sort(this.sortBy(by, this.archivedTableSortOrder));
  }

  /**
   * Archived courses default sort on page load
   */
  archivedCoursesDefaultSort(): void {
    this.archivedTableSortBy = SortBy.COURSE_CREATION_DATE;
    this.archivedTableSortOrder = SortOrder.DESC;
    this.archivedCourses.sort(this.sortBy(this.archivedTableSortBy, this.archivedTableSortOrder));
  }

  /**
   * Sorts the soft-deleted courses table
   */
  sortDeletedCoursesEvent(by: SortBy): void {
    this.deletedTableSortOrder = this.deletedTableSortBy === by && this.deletedTableSortOrder === SortOrder.ASC
        ? SortOrder.DESC : SortOrder.ASC;
    this.deletedTableSortBy = by;
    this.softDeletedCourses.sort(this.sortBy(by, this.deletedTableSortOrder));
  }

  /**
   * Deleted courses default sort on page load
   */
  deletedCoursesDefaultSort(): void {
    this.deletedTableSortBy = SortBy.COURSE_DELETION_DATE;
    this.deletedTableSortOrder = SortOrder.DESC;
    this.softDeletedCourses.sort(this.sortBy(this.deletedTableSortBy, this.deletedTableSortOrder));
  }

  /**
   * Returns a function to determine the order of sort
   */
  sortBy(by: SortBy, order: SortOrder):
      ((a: CourseModel, b: CourseModel) => number) {
    return (a: CourseModel, b: CourseModel): number => {
      let strA: string;
      let strB: string;
      switch (by) {
        case SortBy.COURSE_ID:
          strA = a.course.courseId ? a.course.courseId : '';
          strB = b.course.courseId ? b.course.courseId : '';
          break;
        case SortBy.COURSE_NAME:
          strA = a.course.courseName;
          strB = b.course.courseName;
          break;
        case SortBy.COURSE_CREATION_DATE:
          strA = a.course.creationTimestamp.toString();
          strB = b.course.creationTimestamp.toString();
          break;
        default:
          strA = '';
          strB = '';
      }
      return this.tableComparatorService.compare(by, order, strA, strB);
    };
  }
}

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
