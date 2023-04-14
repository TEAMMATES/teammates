import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { saveAs } from 'file-saver';
import { Observable, of } from 'rxjs';
import { concatMap, finalize } from 'rxjs/operators';
import { CourseService } from '../../../services/course.service';
import { FeedbackQuestionsService } from '../../../services/feedback-questions.service';
import { FeedbackResponseCommentService } from '../../../services/feedback-response-comment.service';
import { FeedbackSessionActionsService } from '../../../services/feedback-session-actions.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { InstructorService } from '../../../services/instructor.service';
import { NavigationService } from '../../../services/navigation.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { TableComparatorService } from '../../../services/table-comparator.service';
import { TimezoneService } from '../../../services/timezone.service';
import {
  CourseSectionNames,
  FeedbackQuestion,
  FeedbackQuestions,
  FeedbackSession,
  FeedbackSessionPublishStatus, FeedbackSessionSubmissionStatus,
  FeedbackSessionSubmittedGiverSet,
  Instructor,
  Instructors,
  QuestionOutput,
  ResponseOutput, ResponseVisibleSetting,
  SessionResults, SessionVisibleSetting,
  Student,
  Students,
} from '../../../types/api-output';
import { Intent } from '../../../types/api-request';
import { CommentToCommentRowModelPipe } from '../../components/comment-box/comment-to-comment-row-model.pipe';
import { CommentsToCommentTableModelPipe } from '../../components/comment-box/comments-to-comment-table-model.pipe';
import { QuestionStatistics } from '../../components/question-types/question-statistics/question-statistics';
import {
  ReminderResponseModel,
} from '../../components/sessions-table/send-reminders-to-respondents-modal/send-reminders-to-respondents-model';
import { SimpleModalType } from '../../components/simple-modal/simple-modal-type';
import { ErrorMessageOutput } from '../../error-message-output';
import { InstructorCommentsComponent } from '../instructor-comments.component';
import { InstructorSessionNoResponsePanelComponent } from './instructor-session-no-response-panel.component';
import { InstructorSessionResultSectionType } from './instructor-session-result-section-type.enum';
import { InstructorSessionResultViewType } from './instructor-session-result-view-type.enum';

/**
 * Per section view tab model.
 */
export interface SectionTabModel {
  questions: QuestionOutput[];

  hasPopulated: boolean;
  errorMessage?: string;
  isTabExpanded: boolean;
}

/**
 * Per question view tab model.
 */
export interface QuestionTabModel {
  question: FeedbackQuestion;
  responses: ResponseOutput[];
  statistics: string; // TODO will define types later
  hasPopulated: boolean;
  errorMessage?: string;
  isTabExpanded: boolean;
}

const TIME_FORMAT: string = 'ddd, DD MMM, YYYY, hh:mm A zz';

/**
 * Instructor feedback session result page.
 */
@Component({
  selector: 'tm-instructor-session-result-page',
  templateUrl: './instructor-session-result-page.component.html',
  styleUrls: ['./instructor-session-result-page.component.scss'],
})
export class InstructorSessionResultPageComponent extends InstructorCommentsComponent implements OnInit {

  // enum
  InstructorSessionResultSectionType: typeof InstructorSessionResultSectionType = InstructorSessionResultSectionType;
  InstructorSessionResultViewType: typeof InstructorSessionResultViewType = InstructorSessionResultViewType;

  formattedSessionOpeningTime: string = '';
  formattedSessionClosingTime: string = '';
  formattedResultVisibleFromTime: string = '';

  courseId: string = '';
  fsName: string = '';
  viewType: string = InstructorSessionResultViewType.QUESTION;
  section: string = '';
  sectionType: InstructorSessionResultSectionType = InstructorSessionResultSectionType.EITHER;
  groupByTeam: boolean = true;
  showStatistics: boolean = true;
  indicateMissingResponses: boolean = true;

  // below are two models contain similar and duplicate data
  // they are for different views
  sectionsModel: Record<string, SectionTabModel> = {};
  isSectionsLoaded: boolean = false;
  hasSectionsLoadingFailed: boolean = false;
  questionsModel: Record<string, QuestionTabModel> = {};
  isQuestionsLoaded: boolean = false;
  hasQuestionsLoadingFailed: boolean = false;
  isNoResponseStudentsLoaded: boolean = false;

  isFeedbackSessionLoading: boolean = false;
  hasFeedbackSessionLoadingFailed: boolean = false;
  isDownloadingResults: boolean = false;

  noResponseStudents: Student[] = [];
  isNoResponsePanelLoaded: boolean = false;
  hasNoResponseLoadingFailed: boolean = false;

  allStudentsInCourse: Student[] = [];
  emailOfStudentToPreview: string = '';
  allInstructorsInCourse: Instructor[] = [];
  emailOfInstructorToPreview: string = '';

  FeedbackSessionPublishStatus: typeof FeedbackSessionPublishStatus = FeedbackSessionPublishStatus;
  isExpandAll: boolean = false;

  session: FeedbackSession = {
    courseId: '',
    timeZone: '',
    feedbackSessionName: '',
    instructions: '',
    submissionStartTimestamp: 0,
    submissionEndTimestamp: 0,
    gracePeriod: 0,
    sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
    responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
    submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
    publishStatus: FeedbackSessionPublishStatus.NOT_PUBLISHED,
    isClosingEmailEnabled: true,
    isPublishedEmailEnabled: true,
    createdAtTimestamp: 0,
    studentDeadlines: {},
    instructorDeadlines: {},
  };

  @ViewChild(InstructorSessionNoResponsePanelComponent) noResponsePanel?:
    InstructorSessionNoResponsePanelComponent;

  constructor(private feedbackSessionsService: FeedbackSessionsService,
              private feedbackSessionActionsService: FeedbackSessionActionsService,
              private feedbackQuestionsService: FeedbackQuestionsService,
              private courseService: CourseService,
              private studentService: StudentService,
              private instructorService: InstructorService,
              private route: ActivatedRoute,
              private timezoneService: TimezoneService,
              private simpleModalService: SimpleModalService,
              private commentsToCommentTableModel: CommentsToCommentTableModelPipe,
              private navigationService: NavigationService,
              statusMessageService: StatusMessageService,
              commentService: FeedbackResponseCommentService,
              commentToCommentRowModel: CommentToCommentRowModelPipe,
              tableComparatorService: TableComparatorService) {
    super(commentToCommentRowModel, commentService, statusMessageService, tableComparatorService);
    this.timezoneService.getTzVersion(); // import timezone service to load timezone data
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.courseId = queryParams.courseid;
      this.fsName = queryParams.fsname;
      this.loadFeedbackSessionResults(this.courseId, this.fsName);
    });
  }

  loadFeedbackSessionResults(courseId: string, feedbackSessionName: string): void {
    this.hasQuestionsLoadingFailed = false;
    this.hasSectionsLoadingFailed = false;
    this.hasFeedbackSessionLoadingFailed = false;
    this.isFeedbackSessionLoading = true;
    this.feedbackSessionsService.getFeedbackSession({
      courseId,
      feedbackSessionName,
      intent: Intent.INSTRUCTOR_RESULT,
    }).subscribe({
      next: (feedbackSession: FeedbackSession) => {
        this.session = feedbackSession;
        this.formattedSessionOpeningTime = this.timezoneService
            .formatToString(this.session.submissionStartTimestamp, this.session.timeZone, TIME_FORMAT);
        this.formattedSessionClosingTime = this.timezoneService
            .formatToString(this.session.submissionEndTimestamp, this.session.timeZone, TIME_FORMAT);
        if (this.session.responseVisibleSetting === ResponseVisibleSetting.AT_VISIBLE) {
          if (this.session.sessionVisibleSetting === SessionVisibleSetting.AT_OPEN) {
            this.formattedResultVisibleFromTime = this.timezoneService
                .formatToString(this.session.submissionStartTimestamp, this.session.timeZone, TIME_FORMAT);
          } else if (this.session.sessionVisibleFromTimestamp) {
            this.formattedResultVisibleFromTime = this.timezoneService
                .formatToString(this.session.sessionVisibleFromTimestamp, this.session.timeZone, TIME_FORMAT);
          }
        } else if (this.session.resultVisibleFromTimestamp) {
          this.formattedResultVisibleFromTime = this.timezoneService
              .formatToString(this.session.resultVisibleFromTimestamp, this.session.timeZone, TIME_FORMAT);
        } else {
          this.formattedResultVisibleFromTime = 'Not applicable';
        }
        this.isFeedbackSessionLoading = false;

        // load section tabs
        this.courseService.getCourseSectionNames(courseId)
            .subscribe({
              next: (courseSectionNames: CourseSectionNames) => {
                this.sectionsModel['None'] = {
                  questions: [],
                  hasPopulated: false,
                  isTabExpanded: false,
                };
                for (const sectionName of courseSectionNames.sectionNames) {
                  this.sectionsModel[sectionName] = {
                    questions: [],
                    hasPopulated: false,
                    isTabExpanded: false,
                  };
                }
                this.isSectionsLoaded = true;
              },
              error: (resp: ErrorMessageOutput) => {
                this.hasSectionsLoadingFailed = true;
                this.statusMessageService.showErrorToast(resp.error.message);
              },
            });

        // load question tabs
        this.feedbackQuestionsService.getFeedbackQuestions({
          courseId,
          feedbackSessionName,
          intent: Intent.INSTRUCTOR_RESULT,
        }).subscribe({
          next: (feedbackQuestions: FeedbackQuestions) => {
            for (const question of feedbackQuestions.questions) {
              this.questionsModel[question.feedbackQuestionId] = {
                question,
                responses: [],
                statistics: '',
                hasPopulated: false,
                isTabExpanded: false,
              };
            }
            this.isQuestionsLoaded = true;
          },
          error: (resp: ErrorMessageOutput) => {
            this.hasQuestionsLoadingFailed = true;
            this.statusMessageService.showErrorToast(resp.error.message);
          },
        });

        // load all students in course
        this.studentService.getStudentsFromCourse({
          courseId,
        }).subscribe({
          next: (allStudents: Students) => {
            this.allStudentsInCourse = allStudents.students;

            // sort the student list based on team name and student name
            this.allStudentsInCourse.sort((a: Student, b: Student): number => {
              const teamNameCompare: number = a.teamName.localeCompare(b.teamName);
              if (teamNameCompare === 0) {
                return a.name.localeCompare(b.name);
              }
              return teamNameCompare;
            });

            // select the first student
            if (this.allStudentsInCourse.length >= 1) {
              this.emailOfStudentToPreview = this.allStudentsInCourse[0].email;
            }

            this.loadNoResponseStudents(courseId, feedbackSessionName);
          },
          error: (resp: ErrorMessageOutput) => {
            this.statusMessageService.showErrorToast(resp.error.message);
          },
        });

        // load all instructors in course
        this.instructorService.loadInstructors({
          courseId: this.courseId,
          intent: Intent.FULL_DETAIL,
        }).subscribe({
          next: (instructors: Instructors) => {
            this.allInstructorsInCourse = instructors.instructors;

            // sort the instructor list based on name
            this.allInstructorsInCourse.sort((a: Instructor, b: Instructor): number => {
              return a.name.localeCompare(b.name);
            });

            // select the first instructor
            if (this.allInstructorsInCourse.length >= 1) {
              this.emailOfInstructorToPreview = this.allInstructorsInCourse[0].email;
            }
          },
          error: (resp: ErrorMessageOutput) => {
            this.statusMessageService.showErrorToast(resp.error.message);
          },
        });

        // load current instructor name
        this.instructorService.getInstructor({
          courseId,
          intent: Intent.FULL_DETAIL,
        }).subscribe((instructor: Instructor) => {
          this.currInstructorName = instructor.name;
        });
      },
      error: (resp: ErrorMessageOutput) => {
        this.isFeedbackSessionLoading = false;
        this.hasFeedbackSessionLoadingFailed = true;
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
  }

  loadNoResponseStudents(courseId: string, feedbackSessionName: string): void {
    this.hasNoResponseLoadingFailed = false;
    // load no response students
    this.feedbackSessionsService.getFeedbackSessionSubmittedGiverSet({
      courseId,
      feedbackSessionName,
    }).subscribe({
      next: (feedbackSessionSubmittedGiverSet: FeedbackSessionSubmittedGiverSet) => {
        // TODO team is missing
        this.noResponseStudents = this.allStudentsInCourse.filter((student: Student) =>
            !feedbackSessionSubmittedGiverSet.giverIdentifiers.includes(student.email));
        this.isNoResponseStudentsLoaded = true;
      },
      error: (resp: ErrorMessageOutput) => {
        this.hasNoResponseLoadingFailed = true;
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
    this.isNoResponsePanelLoaded = true;
  }

  /**
   * Toggles the question tab in per question view.
   */
  toggleQuestionTab(questionId: string): void {
    this.questionsModel[questionId].isTabExpanded = !this.questionsModel[questionId].isTabExpanded;
    if (this.questionsModel[questionId].isTabExpanded) {
      this.loadQuestionTab(questionId);
    }
  }

  /**
   * Loads all the responses and response statistics for the specified question.
   */
  loadQuestionTab(questionId: string): void {
    if (this.questionsModel[questionId].hasPopulated) {
      // Do not re-fetch data
      return;
    }

    const missingRespMap: Map<string, ResponseOutput> = new Map();
    const tmpMap: Map<string, ResponseOutput> = new Map();

    if (this.hasSectionsLoadingFailed) {
      // the page would not render properly
      return;
    }
    of(...Object.keys(this.sectionsModel)).pipe(
        concatMap((sectionName: string) => {
          return this.feedbackSessionsService.getFeedbackSessionResults({
            questionId,
            courseId: this.session.courseId,
            feedbackSessionName: this.session.feedbackSessionName,
            intent: Intent.FULL_DETAIL,
            groupBySection: sectionName,
            sectionByGiverReceiver: 'both',
          });
        }),
    ).subscribe(
      {
        next: (resp: SessionResults) => {
          if (!resp.questions.length) {
            return;
          }
          const responses: QuestionOutput = resp.questions[0];
          responses.allResponses.forEach((response: ResponseOutput) =>
              (response.isMissingResponse
                  ? missingRespMap.set(response.responseId, response)
                  : tmpMap.set(response.responseId, response)));
          this.questionsModel[questionId].statistics = QuestionStatistics.appendStats(
              this.questionsModel[questionId].statistics,
              responses.questionStatistics);

          this.preprocessComments(responses.allResponses);
        },
        complete: () => {
          tmpMap.forEach((response: ResponseOutput) =>
              this.questionsModel[questionId].responses.push(response));
          missingRespMap.forEach((response: ResponseOutput) =>
              this.questionsModel[questionId].responses.push(response));
          this.questionsModel[questionId].errorMessage = '';
          this.questionsModel[questionId].hasPopulated = true;
        },
        error: (resp: ErrorMessageOutput) => {
          this.questionsModel[questionId].errorMessage = resp.error.message;
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      },
    );
  }

  /**
   * Toggles the section tab in per section view.
   */
  toggleSectionTab(sectionName: string): void {
    this.sectionsModel[sectionName].isTabExpanded = !this.sectionsModel[sectionName].isTabExpanded;
    if (this.sectionsModel[sectionName].isTabExpanded) {
      this.loadSectionTab(sectionName);
    }
  }

  /**
   * Loads all the responses and response statistics for the specified section.
   */
  loadSectionTab(sectionName: string): void {
    if (this.sectionsModel[sectionName].hasPopulated) {
      // Do not re-fetch data
      return;
    }
    this.feedbackSessionsService.getFeedbackSessionResults({
      courseId: this.session.courseId,
      feedbackSessionName: this.session.feedbackSessionName,
      intent: Intent.FULL_DETAIL,
      groupBySection: sectionName,
    })
    .subscribe(
      {
        next: (resp: SessionResults) => {
          this.sectionsModel[sectionName].questions = resp.questions;

          // sort questions by question number
          resp.questions.sort((a: QuestionOutput, b: QuestionOutput) =>
            a.feedbackQuestion.questionNumber - b.feedbackQuestion.questionNumber);
          resp.questions.forEach((question: QuestionOutput) => {
            this.preprocessComments(question.allResponses);
          });
        },
        complete: () => {
          this.sectionsModel[sectionName].hasPopulated = true;
          this.sectionsModel[sectionName].errorMessage = '';
        },
        error: (resp: ErrorMessageOutput) => {
          this.sectionsModel[sectionName].errorMessage = resp.error.message;
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      },
    );
  }

  /**
   * Preprocesses the comments from instructor.
   *
   * <p>The instructor comment will be moved to map {@code instructorCommentTableModel}. The original
   * instructor comments associated with the response will be deleted.
   */
  preprocessComments(responses: ResponseOutput[]): void {
    responses.forEach((response: ResponseOutput) => {
      this.instructorCommentTableModel[response.responseId] =
         this.commentsToCommentTableModel.transform(response.instructorComments, false, this.session.timeZone);
      this.sortComments(this.instructorCommentTableModel[response.responseId]);
      // clear the original comments for safe as instructorCommentTableModel will become the single point of truth
      response.instructorComments = [];
    });
  }

  /**
   * Handle publish result button event.
   */
  publishResultHandler(): void {
    const isPublished: boolean = this.session.publishStatus === FeedbackSessionPublishStatus.PUBLISHED;
    let modalRef: NgbModalRef;
    if (isPublished) {
      const modalContent: string =
          `An email will be sent to students to inform them that the session has been unpublished
          and the session responses will no longer be viewable by students.`;
      modalRef = this.simpleModalService.openConfirmationModal(
          `Unpublish this session <strong>${this.session.feedbackSessionName}</strong>?`,
          SimpleModalType.WARNING, modalContent);
    } else {
      const modalContent: string =
          'An email will be sent to students to inform them that the responses are ready for viewing.';
      modalRef = this.simpleModalService.openConfirmationModal(
          `Publish this session <strong>${this.session.feedbackSessionName}</strong>?`,
          SimpleModalType.WARNING, modalContent);
    }

    modalRef.result.then(() => {
      const response: Observable<any> = isPublished
          ? this.feedbackSessionsService.unpublishFeedbackSession(
            this.session.courseId, this.session.feedbackSessionName,
          )
          : this.feedbackSessionsService.publishFeedbackSession(
            this.session.courseId, this.session.feedbackSessionName,
          );

      response.subscribe({
        next: (res: FeedbackSession) => {
          this.session = res;
          if (this.session.resultVisibleFromTimestamp) {
            this.formattedResultVisibleFromTime = this.timezoneService
                .formatToString(this.session.resultVisibleFromTimestamp, this.session.timeZone, TIME_FORMAT);
            this.statusMessageService.showSuccessToast('The feedback session has been published. '
                + 'Please allow up to 1 hour for all the notification emails to be sent out.');
          } else {
            this.formattedResultVisibleFromTime = 'Not applicable';
            this.statusMessageService.showSuccessToast('The feedback session has been unpublished.');
          }
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
    }, () => {});
  }

  /**
   * Handle print view button event.
   */
  printViewHandler(): void {
    window.print();
  }

  /**
   * Handle download results button event.
   */
  downloadResultHandler(): void {
    this.isDownloadingResults = true;
    of(this.feedbackSessionActionsService.downloadSessionResult(
      this.courseId,
      this.session.feedbackSessionName,
      Intent.FULL_DETAIL,
      this.indicateMissingResponses,
      this.showStatistics,
      Object.values(this.questionsModel).map((questionTabModel: QuestionTabModel) => questionTabModel.question),
      this.section.length === 0 ? undefined : this.section,
      this.section.length === 0 ? undefined : this.sectionType,
    )).pipe(finalize(() => {
      this.isDownloadingResults = false;
    }))
      .subscribe();
  }

  downloadQuestionResultHandler(question: { questionNumber: number, questionId: string }): void {
    const filename: string =
        `${this.session.courseId}_${this.session.feedbackSessionName}_question${question.questionNumber}.csv`;

    this.feedbackSessionsService.downloadSessionResults(
        this.session.courseId,
        this.session.feedbackSessionName,
        Intent.FULL_DETAIL,
        this.indicateMissingResponses,
        this.showStatistics,
        question.questionId,
    ).subscribe({
      next: (resp: string) => {
        const blob: any = new Blob([resp], { type: 'text/csv' });
        saveAs(blob, filename);
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
  }

  /**
   * Handle expand all questions button event.
   */
  toggleExpandAllHandler(): void {
    if (this.isExpandAll) {
      this.collapseAllTabs();
    } else {
      this.expandAllTabs();
    }
  }

  /**
   * Expands the tab of all sections.
   */
  expandAllTabs(): void {
    this.isExpandAll = true;

    if (this.noResponsePanel !== undefined) {
      this.noResponsePanel.expandTab();
    }

    if (this.viewType === InstructorSessionResultViewType.QUESTION) {
      for (const questionId of Object.keys(this.questionsModel)) {
        this.questionsModel[questionId].isTabExpanded = true;
        this.loadQuestionTab(questionId);
      }
      return;
    }

    for (const sectionName of Object.keys(this.sectionsModel)) {
      this.sectionsModel[sectionName].isTabExpanded = true;
      this.loadSectionTab(sectionName);
    }
  }

  /**
   * Collapses the tab of all sections.
   */
  collapseAllTabs(): void {
    this.isExpandAll = false;

    if (this.noResponsePanel !== undefined) {
      this.noResponsePanel.collapseTab();
    }

    if (this.viewType === InstructorSessionResultViewType.QUESTION) {
      for (const questionId of Object.keys(this.questionsModel)) {
        this.questionsModel[questionId].isTabExpanded = false;
      }
      return;
    }

    for (const sectionName of Object.keys(this.sectionsModel)) {
      this.sectionsModel[sectionName].isTabExpanded = false;
    }
  }

  /**
   * Handles the sending of reminders to students.
   */
  sendReminderToStudents(reminderResponse: ReminderResponseModel): void {
    this.feedbackSessionsService
      .remindFeedbackSessionSubmissionForRespondents(this.session.courseId, this.session.feedbackSessionName, {
        usersToRemind: reminderResponse.respondentsToSend.map((m) => m.email),
        isSendingCopyToInstructor: reminderResponse.isSendingCopyToInstructor,
      })
        .subscribe({
          next: () => {
            this.statusMessageService.showSuccessToast(
                'Reminder e-mails have been sent out to those students and instructors. '
                + 'Please allow up to 1 hour for all the notification emails to be sent out.');

          },
          error: (resp: ErrorMessageOutput) => {
            this.statusMessageService.showErrorToast(resp.error.message);
          },
        });
  }

  navigateToIndividualSessionResultPage(): void {
    this.navigationService.navigateByURL('/web/instructor/sessions/result',
        { courseid: this.courseId, fsname: this.fsName });
  }

}
