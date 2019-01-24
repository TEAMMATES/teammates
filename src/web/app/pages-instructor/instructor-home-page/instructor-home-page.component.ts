import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpRequestService } from '../../../services/http-request.service';
import { NavigationService } from '../../../services/navigation.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { TimezoneService } from '../../../services/timezone.service';
import {
  CopySessionResult,
  SessionsTableColumn,
  SessionsTableHeaderColorScheme,
  SessionsTableRowModel,
  SortBy,
  SortOrder,
} from '../../components/sessions-table/sessions-table-model';
import { Course, Courses } from '../../course';
import { FeedbackSession, FeedbackSessions } from '../../feedback-session';
import { defaultInstructorPrivilege } from '../../instructor-privilege';
import { ErrorMessageOutput } from '../../message-output';
import { InstructorSessionBasePageComponent } from '../instructor-session-base-page.component';

interface CourseTabModel {
  course: Course;
  sessionsTableRowModels: SessionsTableRowModel[];
  sessionsTableRowModelsSortBy: SortBy;
  sessionsTableRowModelsSortOrder: SortOrder;

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
export class InstructorHomePageComponent extends InstructorSessionBasePageComponent implements OnInit {

  // enum
  SessionsTableColumn: typeof SessionsTableColumn = SessionsTableColumn;
  SessionsTableHeaderColorScheme: typeof SessionsTableHeaderColorScheme = SessionsTableHeaderColorScheme;

  user: string = '';

  // data
  courseTabModels: CourseTabModel[] = [];

  constructor(router: Router, httpRequestService: HttpRequestService,
              statusMessageService: StatusMessageService, navigationService: NavigationService,
              private route: ActivatedRoute, private timezoneService: TimezoneService) {
    super(router, httpRequestService, statusMessageService, navigationService);
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
   * Loads courses of current instructor.
   */
  loadCourses(): void {
    this.httpRequestService.get('/courses').subscribe((courses: Courses) => {
      courses.courses.forEach((course: Course) => {
        const model: CourseTabModel = {
          course,
          sessionsTableRowModels: [],
          isTabExpanded: false,
          sessionsTableRowModelsSortBy: SortBy.NONE,
          sessionsTableRowModelsSortOrder: SortOrder.ASC,
        };

        this.courseTabModels.push(model);
        this.loadFeedbackSessions(model);
      });
    }, (resp: ErrorMessageOutput) => { this.statusMessageService.showErrorMessage(resp.error.message); });
  }

  /**
   * Loads the feedback session in the course.
   */
  loadFeedbackSessions(model: CourseTabModel): void {
    this.httpRequestService.get('/sessions', {
      courseid: model.course.courseId,
    }).subscribe((response: FeedbackSessions) => {
      response.feedbackSessions.forEach((feedbackSession: FeedbackSession) => {
        const m: SessionsTableRowModel = {
          feedbackSession,
          responseRate: '',
          isLoadingResponseRate: false,
          instructorPrivilege: defaultInstructorPrivilege,
        };
        model.sessionsTableRowModels.push(m);
        this.updateInstructorPrivilege(m);
      });

      model.isTabExpanded = true;
    }, (resp: ErrorMessageOutput) => { this.statusMessageService.showErrorMessage(resp.error.message); });
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
  viewSessionResultEventHandler(): void {
    this.viewSessionResult();
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
   * Sends e-mails to remind students who have not submitted their feedback.
   */
  sendRemindersToStudentsEventHandler(tabIndex: number, rowIndex: number): void {
    this.sendRemindersToStudents(this.courseTabModels[tabIndex].sessionsTableRowModels[rowIndex]);
  }
}
