import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { forkJoin, Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { CourseService } from '../../../services/course.service';
import { FeedbackQuestionsService } from '../../../services/feedback-questions.service';
import { FeedbackSessionActionsService } from '../../../services/feedback-session-actions.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { InstructorService } from '../../../services/instructor.service';
import { NavigationService } from '../../../services/navigation.service';
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
  InstructorPermissionSet,
} from '../../../types/api-output';
import { DEFAULT_INSTRUCTOR_PRIVILEGE } from '../../../types/default-instructor-privilege';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import {
  CopySessionResult,
  SessionsTableColumn,
  SessionsTableHeaderColorScheme,
  SessionsTableRowModel,
} from '../../components/sessions-table/sessions-table-model';
import { SimpleModalType } from '../../components/simple-modal/simple-modal-type';
import { collapseAnim } from '../../components/teammates-common/collapse-anim';
import { ErrorMessageOutput } from '../../error-message-output';
import { InstructorSessionModalPageComponent } from '../instructor-session-modal-page.component';

/**
 * Data model for the course tab.
 */
export interface CourseTabModel {
  course: Course;
  instructorPrivilege: InstructorPermissionSet;
  sessionsTableRowModels: SessionsTableRowModel[];
  sessionsTableRowModelsSortBy: SortBy;
  sessionsTableRowModelsSortOrder: SortOrder;

  hasPopulated: boolean;
  isAjaxSuccess: boolean;
  isTabExpanded: boolean;
  hasLoadingFailed: boolean;
}

/**
 * Instructor home page.
 */
@Component({
  selector: 'tm-instructor-home-page',
  templateUrl: './instructor-home-page.component.html',
  styleUrls: ['./instructor-home-page.component.scss'],
  animations: [collapseAnim],
})
export class InstructorHomePageComponent extends InstructorSessionModalPageComponent implements OnInit {

  private static readonly coursesToLoad: number = 3;
  // enum
  SessionsTableColumn: typeof SessionsTableColumn = SessionsTableColumn;
  SessionsTableHeaderColorScheme: typeof SessionsTableHeaderColorScheme = SessionsTableHeaderColorScheme;
  SortBy: typeof SortBy = SortBy;

  instructorCoursesSortBy: SortBy = SortBy.COURSE_CREATION_DATE;

  // data
  courseTabModels: CourseTabModel[] = [];

  hasCoursesLoaded: boolean = false;
  hasCoursesLoadingFailed: boolean = false;
  isNewUser: boolean = false;
  isCopyLoading: boolean = false;

  @ViewChild('modifiedTimestampsModal') modifiedTimestampsModal!: TemplateRef<any>;

  constructor(statusMessageService: StatusMessageService,
              navigationService: NavigationService,
              feedbackSessionsService: FeedbackSessionsService,
              feedbackQuestionsService: FeedbackQuestionsService,
              ngbModal: NgbModal,
              studentService: StudentService,
              instructorService: InstructorService,
              tableComparatorService: TableComparatorService,
              simpleModalService: SimpleModalService,
              progressBarService: ProgressBarService,
              feedbackSessionActionsService: FeedbackSessionActionsService,
              timezoneService: TimezoneService,
              private courseService: CourseService) {
    super(instructorService, statusMessageService, navigationService, feedbackSessionsService,
        feedbackQuestionsService, tableComparatorService, ngbModal, simpleModalService,
        progressBarService, feedbackSessionActionsService, timezoneService, studentService);
  }

  ngOnInit(): void {
    this.loadCourses();
  }

  /**
   * Gets a list of courses belong to current user.
   */
  get courseCandidates(): Course[] {
    return this.courseTabModels.map((m: CourseTabModel) => m.course);
  }

  /**
   * Handles click events on the course tab model.
   */
  handleClick(event: Event, courseTabModel: CourseTabModel): boolean {
    if (event.target && !(event.target as HTMLElement).className.includes('dropdown-toggle')) {
      return !courseTabModel.isTabExpanded;
    }
    return courseTabModel.isTabExpanded;
  }

  /**
   * Archives the entire course from the instructor
   */
  archiveCourse(courseId: string): void {
    const modalContent: string =
        'This action can be reverted by going to the "Courses" tab and unarchiving the desired course(s).';

    const modalRef: NgbModalRef =
        this.simpleModalService.openConfirmationModal(
            `Archive course <strong>${courseId}</strong>?`, SimpleModalType.INFO, modalContent);
    modalRef.result.then(() => {
      this.courseService.changeArchiveStatus(courseId, {
        archiveStatus: true,
      }).subscribe({
        next: (courseArchive: CourseArchive) => {
          this.courseTabModels = this.courseTabModels.filter((model: CourseTabModel) => {
            return model.course.courseId !== courseId;
          });
          this.statusMessageService.showSuccessToast(`The course ${courseArchive.courseId} has been archived.
            You can retrieve it from the Courses page.`);
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
    }, () => {});
  }

  /**
   * Deletes the entire course from the instructor
   */
  deleteCourse(courseId: string): void {
    const modalContent: string =
        'This action can be reverted by going to the "Courses" tab and restoring the desired course(s).';

    const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
        `Move course <strong>${courseId}</strong> to Recycle Bin`, SimpleModalType.WARNING, modalContent);
    modalRef.result.then(() => {
      this.courseService.binCourse(courseId).subscribe({
        next: (course: Course) => {
          this.courseTabModels = this.courseTabModels.filter((model: CourseTabModel) => {
            return model.course.courseId !== courseId;
          });
          this.statusMessageService.showSuccessToast(
              `The course ${course.courseId} has been deleted. You can restore it from the Recycle Bin manually.`);
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
    }, () => {});
  }
  /**
   * Loads courses of current instructor.
   */
  loadCourses(): void {
    this.hasCoursesLoaded = false;
    this.hasCoursesLoadingFailed = false;
    this.courseTabModels = [];
    this.courseService.getInstructorCoursesThatAreActive()
        .pipe(finalize(() => {
          this.hasCoursesLoaded = true;
        }))
        .subscribe({
          next: (courses: Courses) => {
            courses.courses.forEach((course: Course) => {
              const model: CourseTabModel = {
                course,
                instructorPrivilege: course.privileges || DEFAULT_INSTRUCTOR_PRIVILEGE(),
                sessionsTableRowModels: [],
                isTabExpanded: false,
                isAjaxSuccess: true,
                hasPopulated: false,
                hasLoadingFailed: false,
                sessionsTableRowModelsSortBy: SortBy.NONE,
                sessionsTableRowModelsSortOrder: SortOrder.ASC,
              };

              this.courseTabModels.push(model);
            });
            this.isNewUser = !courses.courses.some((course: Course) => !/-demo\d*$/.test(course.courseId));
            this.sortCoursesBy(this.instructorCoursesSortBy);
          },
          error: (resp: ErrorMessageOutput) => {
            this.hasCoursesLoadingFailed = true;
            this.statusMessageService.showErrorToast(resp.error.message);
          },
        });
  }

  /**
   * Loads the feedback session in the course and sorts them according to end date.
   */
  loadFeedbackSessions(index: number): void {
    const model: CourseTabModel = this.courseTabModels[index];
    model.hasLoadingFailed = false;
    if (!model.hasPopulated) {
      this.feedbackSessionsService.getFeedbackSessionsForInstructor(model.course.courseId)
          .subscribe({
            next: (response: FeedbackSessions) => {
              response.feedbackSessions.forEach((feedbackSession: FeedbackSession) => {
                const m: SessionsTableRowModel = {
                  feedbackSession,
                  responseRate: '',
                  isLoadingResponseRate: false,
                  instructorPrivilege: feedbackSession.privileges || DEFAULT_INSTRUCTOR_PRIVILEGE(),
                };
                model.sessionsTableRowModels.push(m);
              });
              model.hasPopulated = true;
              if (!model.isAjaxSuccess) {
                model.isAjaxSuccess = true;
              }
            },
            error: (resp: ErrorMessageOutput) => {
              model.hasLoadingFailed = true;
              this.statusMessageService.showErrorToast(resp.error.message);
            },
            complete: () => this.sortSessionsTableRowModelsEvent(index, SortBy.SESSION_END_DATE),
          });
    }
  }

  /**
   * Checks the option selected to sort courses.
   */
  isSelectedForSorting(by: SortBy): boolean {
    return this.instructorCoursesSortBy === by;
  }

  /**
   * Sorts the courses according to selected option.
   */
  sortCoursesBy(by: SortBy): void {
    this.instructorCoursesSortBy = by;

    if (this.courseTabModels.length > 1) {
      const modelCopy: CourseTabModel[] = JSON.parse(JSON.stringify(this.courseTabModels));
      modelCopy.sort(this.sortPanelsBy(by));
      this.courseTabModels = modelCopy;
    }
    this.loadLatestCourses();
  }

  /**
   * Loads and expand the latest number of courses.
   */
  loadLatestCourses(): void {
    for (let i: number = 0; i < this.courseTabModels.length; i += 1) {
      if (i >= InstructorHomePageComponent.coursesToLoad) {
        break;
      }
      this.courseTabModels[i].isTabExpanded = true;
      this.loadFeedbackSessions(i);
    }
  }

  /**
   * Sorts the panels of courses in order.
   */
  sortPanelsBy(by: SortBy):
      ((a: { course: Course }, b: { course: Course }) => number) {
    return ((a: { course: Course }, b: { course: Course }): number => {
      let strA: string;
      let strB: string;
      let order: SortOrder;
      switch (by) {
        case SortBy.COURSE_NAME:
          strA = a.course.courseName;
          strB = b.course.courseName;
          order = SortOrder.ASC;
          break;
        case SortBy.COURSE_ID:
          strA = a.course.courseId;
          strB = b.course.courseId;
          order = SortOrder.ASC;
          break;
        case SortBy.COURSE_CREATION_DATE:
          strA = String(a.course.creationTimestamp);
          strB = String(b.course.creationTimestamp);
          order = SortOrder.DESC;
          break;
        default:
          strA = '';
          strB = '';
          order = SortOrder.ASC;
      }
      return this.tableComparatorService.compare(by, order, strA, strB);
    });
  }

  /**
   * Sorts the list of feedback session row.
   */
  sortSessionsTableRowModelsEvent(tabIndex: number, by: SortBy): void {
    const tab: CourseTabModel = this.courseTabModels[tabIndex];

    tab.sessionsTableRowModelsSortBy = by;
    // reverse the sort order
    tab.sessionsTableRowModelsSortOrder =
        tab.sessionsTableRowModelsSortOrder === SortOrder.DESC ? SortOrder.ASC : SortOrder.DESC;
    tab.sessionsTableRowModels.sort(this.sortModelsBy(by, tab.sessionsTableRowModelsSortOrder));
  }

  /**
   * Loads response rate of a feedback session.
   */
  loadResponseRateEventHandler(tabIndex: number, rowIndex: number): void {
    this.loadResponseRate(this.courseTabModels[tabIndex].sessionsTableRowModels[rowIndex]);
  }

  /**
   * Moves the feedback session to the recycle bin.
   */
  moveSessionToRecycleBinEventHandler(tabIndex: number, rowIndex: number): void {
    const model: SessionsTableRowModel = this.courseTabModels[tabIndex].sessionsTableRowModels[rowIndex];

    this.feedbackSessionsService.moveSessionToRecycleBin(
        model.feedbackSession.courseId,
        model.feedbackSession.feedbackSessionName,
    )
        .subscribe({
          next: () => {
            this.courseTabModels[tabIndex].sessionsTableRowModels.splice(
                this.courseTabModels[tabIndex].sessionsTableRowModels.indexOf(model), 1);
            this.statusMessageService.showSuccessToast(
                "The feedback session has been deleted. You can restore it from the 'Sessions' tab.");
          },
          error: (resp: ErrorMessageOutput) => {
            this.statusMessageService.showErrorToast(resp.error.message);
          },
        });
  }

  /**
   * Copies the feedback session.
   */
  copySessionEventHandler(tabIndex: number, result: CopySessionResult): void {
    this.isCopyLoading = true;
    this.failedToCopySessions = {};
    this.coursesOfModifiedSession = [];
    this.modifiedSession = {};
    const requestList: Observable<FeedbackSession>[] = this.createSessionCopyRequestsFromRowModel(
        this.courseTabModels[tabIndex].sessionsTableRowModels[result.sessionToCopyRowIndex], result);
    if (requestList.length === 1) {
      this.copySingleSession(requestList[0], this.modifiedTimestampsModal);
    }
    if (requestList.length > 1) {
      forkJoin(requestList).pipe(finalize(() => {
          this.isCopyLoading = false;
        }))
        .subscribe((newSessions: FeedbackSession[]) => {
          if (newSessions.length > 0) {
            newSessions.forEach((session: FeedbackSession) => {
              const model: SessionsTableRowModel = {
                feedbackSession: session,
                responseRate: '',
                isLoadingResponseRate: false,
                instructorPrivilege: session.privileges || DEFAULT_INSTRUCTOR_PRIVILEGE(),
              };
              const courseModel: CourseTabModel | undefined = this.courseTabModels.find((tabModel: CourseTabModel) =>
                  tabModel.course.courseId === session.courseId);
              if (courseModel && courseModel.hasPopulated) {
                courseModel.sessionsTableRowModels.push(model);
              }
            });
          }
          this.showCopyStatusMessage(this.modifiedTimestampsModal);
        });
    }
  }

  /**
   * Submits the feedback session as instructor.
   */
  submitSessionAsInstructorEventHandler(tabIndex: number, rowIndex: number): void {
    this.submitSessionAsInstructor(this.courseTabModels[tabIndex].sessionsTableRowModels[rowIndex]);
  }

  /**
   * Publishes a feedback session.
   */
  publishSessionEventHandler(tabIndex: number, rowIndex: number): void {
    this.publishSession(this.courseTabModels[tabIndex].sessionsTableRowModels[rowIndex]);
  }

  /**
   * Unpublishes a feedback session.
   */
  unpublishSessionEventHandler(tabIndex: number, rowIndex: number): void {
    this.unpublishSession(this.courseTabModels[tabIndex].sessionsTableRowModels[rowIndex]);
  }

  /**
   * Downloads the result of a feedback session in csv.
   */
  downloadSessionResultEventHandler(tabIndex: number, rowIndex: number): void {
    this.downloadSessionResult(this.courseTabModels[tabIndex].sessionsTableRowModels[rowIndex]);
  }
}
