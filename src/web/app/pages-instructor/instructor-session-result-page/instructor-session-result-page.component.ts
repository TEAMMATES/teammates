import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { saveAs } from 'file-saver';
import moment from 'moment-timezone';
import { Observable } from 'rxjs';
import { CourseService } from '../../../services/course.service';
import { FeedbackQuestionsService } from '../../../services/feedback-questions.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { TimezoneService } from '../../../services/timezone.service';
import {
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmittedGiverSet,
  SessionResults,
  Student,
  Students,
} from '../../../types/api-output';
import { Intent } from '../../../types/api-request';
import {
  ConfirmPublishingSessionModalComponent,
} from '../../components/sessions-table/confirm-publishing-session-modal/confirm-publishing-session-modal.component';
// tslint:disable-next-line:max-line-length
import { ConfirmUnpublishingSessionModalComponent } from '../../components/sessions-table/confirm-unpublishing-session-modal/confirm-unpublishing-session-modal.component';
import { ErrorMessageOutput } from '../../error-message-output';
import { InstructorSessionNoResponsePanelComponent } from './instructor-session-no-response-panel.component';
import { InstructorSessionResultGqrViewComponent } from './instructor-session-result-gqr-view.component';
import { InstructorSessionResultGrqViewComponent } from './instructor-session-result-grq-view.component';
import { InstructorSessionResultQuestionViewComponent } from './instructor-session-result-question-view.component';
import { InstructorSessionResultRgqViewComponent } from './instructor-session-result-rgq-view.component';
import { InstructorSessionResultRqgViewComponent } from './instructor-session-result-rqg-view.component';
import { InstructorSessionResultSectionType } from './instructor-session-result-section-type.enum';

/**
 * Instructor feedback session result page.
 */
@Component({
  selector: 'tm-instructor-session-result-page',
  templateUrl: './instructor-session-result-page.component.html',
  styleUrls: ['./instructor-session-result-page.component.scss'],
})
export class InstructorSessionResultPageComponent implements OnInit {

  // enum
  InstructorSessionResultSectionType: typeof InstructorSessionResultSectionType = InstructorSessionResultSectionType;

  session: any = {};
  formattedSessionOpeningTime: string = '';
  formattedSessionClosingTime: string = '';
  viewType: string = 'QUESTION';
  section: string = '';
  sectionType: InstructorSessionResultSectionType = InstructorSessionResultSectionType.EITHER;
  groupByTeam: boolean = true;
  showStatistics: boolean = true;
  indicateMissingResponses: boolean = true;

  sectionsModel: { [key: string]: any } = {};
  isSectionsLoaded: boolean = false;
  questionsModel: { [key: string]: any } = {};
  isQuestionsLoaded: boolean = false;
  noResponseStudents: Student[] = [];
  isNoResponsePanelLoaded: boolean = false;
  FeedbackSessionPublishStatus: typeof FeedbackSessionPublishStatus = FeedbackSessionPublishStatus;
  isExpandAll: boolean = false;

  @ViewChild(InstructorSessionResultQuestionViewComponent, { static: false }) questionView?:
    InstructorSessionResultQuestionViewComponent;
  @ViewChild(InstructorSessionResultGrqViewComponent, { static: false }) grqView?:
    InstructorSessionResultGrqViewComponent;
  @ViewChild(InstructorSessionResultRgqViewComponent, { static: false }) rgqView?:
    InstructorSessionResultRgqViewComponent;
  @ViewChild(InstructorSessionResultGqrViewComponent, { static: false }) gprView?:
    InstructorSessionResultGqrViewComponent;
  @ViewChild(InstructorSessionResultRqgViewComponent, { static: false }) rpgView?:
    InstructorSessionResultRqgViewComponent;
  @ViewChild(InstructorSessionNoResponsePanelComponent, { static: false }) noResponsePanel?:
    InstructorSessionNoResponsePanelComponent;

  constructor(private feedbackSessionsService: FeedbackSessionsService,
              private feedbackQuestionsService: FeedbackQuestionsService,
              private courseService: CourseService,
              private studentService: StudentService,
              private route: ActivatedRoute,
      private timezoneService: TimezoneService, private statusMessageService: StatusMessageService,
      private modalService: NgbModal, private router: Router) {
    this.timezoneService.getTzVersion(); // import timezone service to load timezone data
  }

  ngOnInit(): void {

    this.route.queryParams.subscribe((queryParams: any) => {
      this.feedbackSessionsService.getFeedbackSession({
        courseId: queryParams.courseid,
        feedbackSessionName: queryParams.fsname,
        intent: Intent.INSTRUCTOR_RESULT,
      }).subscribe((resp: FeedbackSession) => {
        const TIME_FORMAT: string = 'ddd, DD MMM, YYYY, hh:mm A zz';
        this.session = resp;
        this.formattedSessionOpeningTime =
            moment(this.session.submissionStartTimestamp).tz(this.session.timeZone).format(TIME_FORMAT);
        this.formattedSessionClosingTime =
            moment(this.session.submissionEndTimestamp).tz(this.session.timeZone).format(TIME_FORMAT);

        this.courseService.getCourseSectionNames(queryParams.courseid).subscribe((resp2: any) => {
          for (const sectionName of resp2.sectionNames) {
            this.sectionsModel[sectionName] = {
              responses: [],
              hasPopulated: false,
            };
          }
          this.isSectionsLoaded = true;
        }, (resp2: any) => {
          this.statusMessageService.showErrorMessage(resp2.error.message);
        });

        this.feedbackQuestionsService.getFeedbackQuestions({
          courseId: queryParams.courseid,
          feedbackSessionName: queryParams.fsname,
          intent: Intent.INSTRUCTOR_RESULT,
        }).subscribe((resp2: any) => {
          for (const question of resp2.questions) {
            question.responses = [];
            question.hasPopulated = false;
            this.questionsModel[question.feedbackQuestionId] = question;
          }
          this.isQuestionsLoaded = true;
        }, (resp2: any) => {
          this.statusMessageService.showErrorMessage(resp2.error.message);
        });

        this.studentService.getStudentsFromCourse({
          courseId: queryParams.courseid,
        }).subscribe((allStudents: Students) => {
          const students: Student[] = allStudents.students;

          this.feedbackSessionsService.getFeedbackSessionSubmittedGiverSet({
            courseId: queryParams.courseid,
            feedbackSessionName: queryParams.fsname,
          })
              .subscribe((feedbackSessionSubmittedGiverSet: FeedbackSessionSubmittedGiverSet) => {
                this.noResponseStudents = students.filter((student: Student) =>
                                            !feedbackSessionSubmittedGiverSet.giverIdentifiers.includes(student.email));
              }, (resp4: any) => {
                this.statusMessageService.showErrorMessage(resp4.error.message);
              });

          this.isNoResponsePanelLoaded = true;
        }, (resp3: any) => {
          this.statusMessageService.showErrorMessage(resp3.error.message);
        });

      }, (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorMessage(resp.error.message);
      });
    });
  }

  /**
   * Loads all the responses and response statistics for the specified question.
   */
  loadQuestion(questionId: string): void {
    if (this.questionsModel[questionId].hasPopulated) {
      // Do not re-fetch data
      return;
    }
    this.feedbackSessionsService.getFeedbackSessionsResult({
      questionId,
      courseId: this.session.courseId,
      feedbackSessionName: this.session.feedbackSessionName,
      intent: Intent.INSTRUCTOR_RESULT,
    })
    .subscribe((resp: SessionResults) => {
      if (resp.questions.length) {
        const responses: any = resp.questions[0];
        this.questionsModel[questionId].responses = responses.allResponses;
        this.questionsModel[questionId].statistics = responses.questionStatistics;
        this.questionsModel[questionId].hasPopulated = true;
      }
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

  /**
   * Loads all the responses and response statistics for the specified section.
   */
  loadSection(sectionName: string): void {
    if (this.sectionsModel[sectionName].hasPopulated) {
      // Do not re-fetch data
      return;
    }
    this.feedbackSessionsService.getFeedbackSessionsResult({
      courseId: this.session.courseId,
      feedbackSessionName: this.session.feedbackSessionName,
      intent: Intent.INSTRUCTOR_RESULT,
      groupBySection: sectionName,
    })
    .subscribe((resp: SessionResults) => {
      this.sectionsModel[sectionName].questions = resp.questions;
      this.sectionsModel[sectionName].hasPopulated = true;
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

  /**
   * Handle publish result button event.
   */
  publishResultHandler(): void {
    const isPublished: boolean = this.session.publishStatus === FeedbackSessionPublishStatus.PUBLISHED;
    const modalRef: NgbModalRef = this.modalService.open(isPublished ? ConfirmUnpublishingSessionModalComponent :
        ConfirmPublishingSessionModalComponent);
    modalRef.componentInstance.feedbackSessionName = this.session.feedbackSessionName;

    modalRef.result.then(() => {
      const response: Observable<any> = isPublished ?
          this.feedbackSessionsService.unpublishFeedbackSession(
            this.session.courseId, this.session.feedbackSessionName,
          ) :
          this.feedbackSessionsService.publishFeedbackSession(
            this.session.courseId, this.session.feedbackSessionName,
          )
      ;

      response.subscribe(() => {
        this.router.navigateByUrl('/web/instructor/sessions');
      }, (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorMessage(resp.error.message);
      });
    }, () => {});
  }

  /**
   * Handle print view button event.
   */
  printViewHandler(): void {
    this.expandAllHandler();
    setTimeout(() => {
      window.print();
    }, 1000);
  }

  /**
   * Handle download results button event.
   */
  downloadResultHandler(): void {
    const filename: string = `${this.session.feedbackSessionName.concat('_result')}.csv`;
    let blob: any;

    this.feedbackSessionsService.downloadSessionResults(
      this.session.courseId,
      this.session.feedbackSessionName,
      Intent.INSTRUCTOR_RESULT,
      this.indicateMissingResponses,
      this.showStatistics,
    ).subscribe((resp: string) => {
      blob = new Blob([resp], { type: 'text/csv' });
      saveAs(blob, filename);
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

  /**
   * Handle expand all questions button event.
   */
  expandAllHandler(): void {
    if (!this.isExpandAll) {
      if (this.questionView !== undefined) {
        this.questionView.expandAllQuestionTabs();
      }
      if (this.noResponsePanel !== undefined) {
        this.noResponsePanel.expandTab();
      }
      if (this.gprView !== undefined) {
        this.gprView.expandAllSectionTabs();
      }
      if (this.rpgView !== undefined) {
        this.rpgView.expandAllSectionTabs();
      }
      if (this.grqView !== undefined) {
        this.grqView.expandAllSectionTabs();
      }
      if (this.rgqView !== undefined) {
        this.rgqView.expandAllSectionTabs();
      }
    } else {
      if (this.questionView !== undefined) {
        this.questionView.collapseAllQuestionTabs();
      }
      if (this.noResponsePanel !== undefined) {
        this.noResponsePanel.collapseTab();
      }
      if (this.gprView !== undefined) {
        this.gprView.collapseAllSectionTabs();
      }
      if (this.rpgView !== undefined) {
        this.rpgView.collapseAllSectionTabs();
      }
      if (this.grqView !== undefined) {
        this.grqView.collapseAllSectionTabs();
      }
      if (this.rgqView !== undefined) {
        this.rgqView.collapseAllSectionTabs();
      }
    }
    this.isExpandAll = !this.isExpandAll;
  }
}
