import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { CourseService } from '../../../services/course.service';
import { FeedbackQuestionsService } from '../../../services/feedback-questions.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { HttpRequestService } from '../../../services/http-request.service';
import { NavigationService } from '../../../services/navigation.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { TimezoneService } from '../../../services/timezone.service';
import {
  Course,
  Courses,
  FeedbackSession,
  FeedbackSessions,
  InstructorPrivilege,
  MessageOutput,
} from '../../../types/api-output';
import { DEFAULT_INSTRUCTOR_PRIVILEGE } from '../../../types/instructor-privilege';
import {
  CopySessionResult,
  SessionsTableColumn,
  SessionsTableHeaderColorScheme,
  SessionsTableRowModel,
  SortBy,
  SortOrder,
} from '../../components/sessions-table/sessions-table-model';
import { ErrorMessageOutput } from '../../error-message-output';
import { InstructorSessionModalPageComponent } from '../instructor-session-modal-page.component';

interface CourseTabModel {
  course: Course;
  instructorPrivilege: InstructorPrivilege;
  sessionsTableRowModels: SessionsTableRowModel[];
  sessionsTableRowModelsSortBy: SortBy;
  sessionsTableRowModelsSortOrder: SortOrder;

  hasPopulated: boolean;
  isAjaxSuccess: boolean;
  isTabExpanded: boolean;
}

/**
 * Instructor home page.
 */
@Component({
  selector: 'tm-instructor-home-page',
  templateUrl: './instructor-home-page.component.html',
  styleUrls: ['./instructor-home-page.component.scss'],
})
export class InstructorHomePageComponent extends InstructorSessionModalPageComponent implements OnInit {

  private static readonly coursesToLoad: number = 3;
  // enum
  SessionsTableColumn: typeof SessionsTableColumn = SessionsTableColumn;
  SessionsTableHeaderColorScheme: typeof SessionsTableHeaderColorScheme = SessionsTableHeaderColorScheme;
  SortBy: typeof SortBy = SortBy;

  user: string = '';
  studentSearchkey: string = '';
  instructorCoursesSortBy: SortBy = SortBy.COURSE_CREATION_DATE;

  // data
  courseTabModels: CourseTabModel[] = [];

  constructor(router: Router,
              httpRequestService: HttpRequestService,
              statusMessageService: StatusMessageService,
              navigationService: NavigationService,
              feedbackSessionsService: FeedbackSessionsService,
              feedbackQuestionsService: FeedbackQuestionsService,
              modalService: NgbModal,
              studentService: StudentService,
              private courseService: CourseService,
              private route: ActivatedRoute,
              private ngbModal: NgbModal,
              private timezoneService: TimezoneService) {
    super(router, httpRequestService, statusMessageService, navigationService,
        feedbackSessionsService, feedbackQuestionsService, modalService, studentService);
    // need timezone data for moment()
    this.timezoneService.getTzVersion();
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;

      this.loadCourses();
    });
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
    if (event.target &&
        !(event.target as HTMLElement).className.includes('dropdown-toggle')) {
      return !courseTabModel.isTabExpanded;
    }
    return courseTabModel.isTabExpanded;
  }

  /**
   * Redirect to the search page and query the search
   */
  search(): void {
    this.router.navigate(['web/instructor/search'], {
      queryParams: { studentSearchkey: this.studentSearchkey },
    });
  }

  /**
   * Open the modal for different buttons and link.
   */
  openModal(content: any): void {
    this.ngbModal.open(content);
  }

  /**
   * Archives the entire course from the instructor
   */
  archiveCourse(courseId: string): void {
    this.httpRequestService.put('/course', { courseid: courseId, archive: 'true' })
      .subscribe((resp: MessageOutput) => {
        this.loadCourses();
        this.statusMessageService.showSuccessMessage(resp.message);
      }, (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorMessage(resp.error.message);
      });
  }

  /**
   * Deletes the entire course from the instructor
   */
  deleteCourse(courseId: string): void {
    this.courseService.binCourse(courseId).subscribe((course: Course) => {
      this.loadCourses();
      this.statusMessageService.showSuccessMessage(
        `The course ${course.courseId} has been deleted. You can restore it from the Recycle Bin manually.`);
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }
  /**
   * Loads courses of current instructor.
   */
  loadCourses(): void {
    this.courseTabModels = [];
    this.httpRequestService.get('/courses', {
      entitytype: 'instructor',
      coursestatus: 'active',
    }).subscribe((courses: Courses) => {
      courses.courses.forEach((course: Course) => {
        const model: CourseTabModel = {
          course,
          instructorPrivilege: DEFAULT_INSTRUCTOR_PRIVILEGE,
          sessionsTableRowModels: [],
          isTabExpanded: false,
          isAjaxSuccess: true,
          hasPopulated: false,
          sessionsTableRowModelsSortBy: SortBy.NONE,
          sessionsTableRowModelsSortOrder: SortOrder.ASC,
        };

        this.courseTabModels.push(model);
        this.updateCourseInstructorPrivilege(model);
      });
      this.sortCoursesBy(this.instructorCoursesSortBy);
    }, (resp: ErrorMessageOutput) => { this.statusMessageService.showErrorMessage(resp.error.message); });
  }

  /**
   * Updates the instructor privilege in {@code CourseTabModel}.
   */
  updateCourseInstructorPrivilege(model: CourseTabModel): void {
    this.httpRequestService.get('/instructor/privilege', {
      courseid: model.course.courseId,
    }).subscribe((instructorPrivilege: InstructorPrivilege) => {
      model.instructorPrivilege = instructorPrivilege;
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

  /**
   * Loads the feedback session in the course.
   */
  loadFeedbackSessions(model: CourseTabModel): void {
    if (!model.hasPopulated) {
      this.feedbackSessionsService.getFeedbackSessionsForInstructor(model.course.courseId)
          .subscribe((response: FeedbackSessions) => {
            response.feedbackSessions.forEach((feedbackSession: FeedbackSession) => {
              const m: SessionsTableRowModel = {
                feedbackSession,
                responseRate: '',
                isLoadingResponseRate: false,
                instructorPrivilege: DEFAULT_INSTRUCTOR_PRIVILEGE,
              };
              model.sessionsTableRowModels.push(m);
              this.updateInstructorPrivilege(m);
            });
            model.hasPopulated = true;
            if (!model.isAjaxSuccess) {
              model.isAjaxSuccess = true;
            }
          }, (resp: ErrorMessageOutput) => {
            model.isAjaxSuccess = false;
            this.statusMessageService.showErrorMessage(resp.error.message);
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
      this.courseTabModels.sort(this.sortPanelsBy(by));
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
      this.loadFeedbackSessions(this.courseTabModels[i]);
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
      switch (by) {
        case SortBy.COURSE_NAME:
          strA = a.course.courseName;
          strB = b.course.courseName;
          break;
        case SortBy.COURSE_ID:
          strA = a.course.courseId;
          strB = b.course.courseId;
          break;
        case SortBy.COURSE_CREATION_DATE:
          strA = a.course.creationDate;
          strB = b.course.creationDate;
          break;
        default:
          strA = '';
          strB = '';
      }
      return strA.localeCompare(strB);
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
   * Edits the feedback session.
   */
  editSessionEventHandler(tabIndex: number, rowIndex: number): void {
    this.editSession(this.courseTabModels[tabIndex].sessionsTableRowModels[rowIndex]);
  }

  /**
   * Moves the feedback session to the recycle bin.
   */
  moveSessionToRecycleBinEventHandler(tabIndex: number, rowIndex: number): void {
    const model: SessionsTableRowModel = this.courseTabModels[tabIndex].sessionsTableRowModels[rowIndex];
    const paramMap: { [key: string]: string } = {
      courseid: model.feedbackSession.courseId,
      fsname: model.feedbackSession.feedbackSessionName,
    };

    this.httpRequestService.put('/bin/session', paramMap)
        .subscribe(() => {
          this.courseTabModels[tabIndex].sessionsTableRowModels.splice(
              this.courseTabModels[tabIndex].sessionsTableRowModels.indexOf(model), 1);
          this.statusMessageService.showSuccessMessage(
              "The feedback session has been deleted. You can restore it from the 'Sessions' tab.");
        }, (resp: ErrorMessageOutput) => { this.statusMessageService.showErrorMessage(resp.error.message); });
  }

  /**
   * Edits the feedback session.
   */
  copySessionEventHandler(tabIndex: number, result: CopySessionResult): void {
    this.copySession(this.courseTabModels[tabIndex].sessionsTableRowModels[result.sessionToCopyRowIndex], result);
  }

  /**
   * Submits the feedback session as instructor.
   */
  submitSessionAsInstructorEventHandler(tabIndex: number, rowIndex: number): void {
    this.submitSessionAsInstructor(this.courseTabModels[tabIndex].sessionsTableRowModels[rowIndex]);
  }

  /**
   * Views the result of a feedback session.
   */
  viewSessionResultEventHandler(tabIndex: number, rowIndex: number): void {
    this.viewSessionResult(this.courseTabModels[tabIndex].sessionsTableRowModels[rowIndex]);
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
}
