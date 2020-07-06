import { DOCUMENT } from '@angular/common';
import { Component, Inject, OnInit } from '@angular/core';
import { PageScrollService } from 'ngx-page-scroll-core';

import { TemplateSession } from '../../../../services/feedback-sessions.service';
import {
  CommentVisibilityType,
  Course,
  FeedbackContributionResponseDetails,
  FeedbackMcqQuestionDetails,
  FeedbackMcqResponseDetails,
  FeedbackParticipantType,
  FeedbackQuestionType,
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  Instructor,
  InstructorPermissionRole,
  JoinState,
  NumberOfEntitiesToGiveFeedbackToSetting,
  QuestionOutput,
  ResponseOutput,
  ResponseVisibleSetting,
  SessionVisibleSetting,
  Student,
} from '../../../../types/api-output';
import {
  DEFAULT_CONTRIBUTION_RESPONSE_DETAILS,
  DEFAULT_MCQ_QUESTION_DETAILS,
  DEFAULT_MCQ_RESPONSE_DETAILS,
} from '../../../../types/default-question-structs';
import { CommentEditFormModel } from '../../../components/comment-box/comment-edit-form/comment-edit-form.component';
import { CommentRowMode } from '../../../components/comment-box/comment-row/comment-row.component';
import { CommentTableModel } from '../../../components/comment-box/comment-table/comment-table.component';
import {
  SessionEditFormMode,
  SessionEditFormModel,
} from '../../../components/session-edit-form/session-edit-form-model';
import {
  RecycleBinFeedbackSessionRowModel,
} from '../../../components/sessions-recycle-bin-table/sessions-recycle-bin-table.component';
import {
  SearchCommentsTable,
} from '../../../pages-instructor/instructor-search-page/comment-result-table/comment-result-table.component';
import {
  SectionTabModel,
} from '../../../pages-instructor/instructor-session-result-page/instructor-session-result-page.component';
import {
  InstructorSessionResultSectionType,
} from '../../../pages-instructor/instructor-session-result-page/instructor-session-result-section-type.enum';
import {
  InstructorSessionResultViewType,
} from '../../../pages-instructor/instructor-session-result-page/instructor-session-result-view-type.enum';
import { InstructorHelpSectionComponent } from '../instructor-help-section.component';
import { SessionsSectionQuestions } from './sessions-section-questions';

/**
 * Sessions Section of the Instructor Help Page.
 */
@Component({
  selector: 'tm-instructor-help-sessions-section',
  templateUrl: './instructor-help-sessions-section.component.html',
  styleUrls: ['./instructor-help-sessions-section.component.scss'],
})
export class InstructorHelpSessionsSectionComponent extends InstructorHelpSectionComponent implements OnInit {

  // enum
  CommentRowMode: typeof CommentRowMode = CommentRowMode;
  SessionEditFormMode: typeof SessionEditFormMode = SessionEditFormMode;
  InstructorSessionResultViewType: typeof InstructorSessionResultViewType = InstructorSessionResultViewType;
  InstructorSessionResultSectionType: typeof InstructorSessionResultSectionType = InstructorSessionResultSectionType;
  SessionsSectionQuestions: typeof SessionsSectionQuestions = SessionsSectionQuestions;

  readonly exampleSessionEditFormModel: SessionEditFormModel = {
    courseId: 'CS2103T',
    timeZone: 'UTC',
    courseName: 'Software Engineering',
    feedbackSessionName: 'Feedback for Project',
    instructions: 'This is where you type the instructions for the session',

    submissionStartTime: { hour: 10, minute: 0 },
    submissionStartDate: { year: 2020, month: 3, day: 13 },
    submissionEndTime: { hour: 12, minute: 0 },
    submissionEndDate: { year: 2020, month: 3, day: 13 },
    gracePeriod: 0,

    sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
    customSessionVisibleTime: { hour: 9, minute: 0 },
    customSessionVisibleDate: { year: 2020, month: 3, day: 13 },

    responseVisibleSetting: ResponseVisibleSetting.CUSTOM,
    customResponseVisibleTime: { hour: 13, minute: 0 },
    customResponseVisibleDate: { year: 2020, month: 3, day: 13 },

    submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
    publishStatus: FeedbackSessionPublishStatus.NOT_PUBLISHED,

    isClosingEmailEnabled: true,
    isPublishedEmailEnabled: true,

    templateSessionName: 'Example session',

    isSaving: false,
    isEditable: false,
    hasVisibleSettingsPanelExpanded: true,
    hasEmailSettingsPanelExpanded: true,
  };

  exampleCommentEditFormModel: CommentEditFormModel = {
    commentText: '',

    isUsingCustomVisibilities: false,
    showCommentTo: [],
    showGiverNameTo: [],
  };

  readonly exampleResponse: ResponseOutput = {
    isMissingResponse: false,
    responseId: '',
    giver: 'Alice',
    giverTeam: 'Team A',
    giverSection: 'Section A',
    recipient: 'Bob',
    recipientTeam: 'Team B',
    recipientSection: 'Section B',
    responseDetails: DEFAULT_CONTRIBUTION_RESPONSE_DETAILS(),
    instructorComments: [],
  };

  readonly exampleResponseWithComment: ResponseOutput = {
    isMissingResponse: false,
    responseId: '1',
    giver: 'Alice',
    giverTeam: 'Team A',
    giverSection: 'Section A',
    recipient: 'Bob',
    recipientTeam: 'Team B',
    recipientSection: 'Section B',
    responseDetails: {
      answer: 110,
      questionType: FeedbackQuestionType.CONTRIB,
    } as FeedbackContributionResponseDetails,
    instructorComments: [{
      commentGiver: 'Instructor',
      lastEditorEmail: '',
      feedbackResponseCommentId: 1,
      commentText: 'Good to know!',
      createdAt: 1,
      lastEditedAt: 1,
      isVisibilityFollowingFeedbackQuestion: true,
      showGiverNameTo: [CommentVisibilityType.GIVER],
      showCommentTo: [CommentVisibilityType.GIVER],
    }],
  };

  readonly exampleCourseCandidates: Course[] = [
    {
      courseId: 'CS2103T',
      courseName: 'Software Engineering',
      timeZone: 'UTC',
      creationTimestamp: 0,
      deletionTimestamp: 0,
    },
  ];

  readonly exampleTemplateSessions: TemplateSession[] = [
    {
      name: 'Example session',
      questions: [],
    },
  ];

  readonly exampleStudents: Student[] = [
    {
      email: 'alice@email.com',
      courseId: 'test.exa-demo',
      name: 'Alice Betsy',
      lastName: 'Betsy',
      comments: 'Alice is a transfer student.',
      teamName: 'Team A',
      sectionName: 'Section A',
      joinState: JoinState.JOINED,
    },
  ];
  readonly exampleInstructors: Instructor[] = [
    {
      googleId: 'bob@email.com',
      courseId: 'test.exa-demo',
      email: 'bob@email.com',
      isDisplayedToStudents: true,
      displayedToStudentsAs: 'Instructor',
      name: 'Bob Ruth',
      key: 'impicklerick',
      role: InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
      joinState: JoinState.JOINED,
    },
  ];

  readonly exampleFeedbackSession: FeedbackSession = {
    courseId: 'CS2103T',
    timeZone: 'UTC',
    feedbackSessionName: 'Project Feedback 1',
    instructions: 'Enter your feedback for projects',
    submissionStartTimestamp: 0,
    submissionEndTimestamp: 0,
    gracePeriod: 0,
    sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
    responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
    submissionStatus: FeedbackSessionSubmissionStatus.CLOSED,
    publishStatus: FeedbackSessionPublishStatus.NOT_PUBLISHED,
    isClosingEmailEnabled: true,
    isPublishedEmailEnabled: true,
    createdAtTimestamp: 0,
  };

  readonly exampleRecycleBinFeedbackSessions: RecycleBinFeedbackSessionRowModel[] = [
    {
      feedbackSession: this.exampleFeedbackSession,
    },
  ];

  readonly exampleInstructorCommentTableModel: Record<string, CommentTableModel> = {
    'feedbackQuestionId%bob@example.com%bob@example.com': {
      isReadOnly: true,
      commentRows: [],
      newCommentRow: {
        commentEditFormModel: {
          commentText: '',
          isUsingCustomVisibilities: true,
          showCommentTo: [],
          showGiverNameTo: [],
        },
        isEditing: false,
      },
      isAddingNewComment: true,
    },
    'feedbackQuestionId%bob@example.com%danny@example.com': {
      isReadOnly: true,
      commentRows: [],
      newCommentRow: {
        commentEditFormModel: {
          commentText: '',
          isUsingCustomVisibilities: false,
          showCommentTo: [],
          showGiverNameTo: [],
        },
        isEditing: false,
      },
      isAddingNewComment: false,
    },
  };

  readonly exampleGrqResponses: Record<string, SectionTabModel> = {
    'Section A': {
      questions: [
        {
          feedbackQuestion: {
            feedbackQuestionId: 'feedbackQuestionId',
            questionNumber: 1,
            questionBrief: 'How well did team member perform?',
            questionDescription: '',
            questionDetails: {
              hasAssignedWeights: false,
              mcqWeights: [],
              mcqOtherWeight: 0,
              numOfMcqChoices: 3,
              mcqChoices: [
                '<p>Good</p>',
                '<p>Normal</p>',
                '<p>Bad</p>',
              ],
              otherEnabled: false,
              generateOptionsFor: 'NONE',
              questionType: FeedbackQuestionType.MCQ,
              questionText: 'How well did team member perform?',
            } as FeedbackMcqQuestionDetails,
            questionType: FeedbackQuestionType.MCQ,
            giverType: FeedbackParticipantType.STUDENTS,
            recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF,
            numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
            showResponsesTo: [],
            showGiverNameTo: [],
            showRecipientNameTo: [],
            customNumberOfEntitiesToGiveFeedbackTo: 0,
          },
          questionStatistics: '',
          allResponses: [
            {
              responseId: 'feedbackQuestionId%bob@example.com%bob@example.com',
              giver: 'Bob Ruth',
              giverTeam: 'Team A',
              giverEmail: 'bob@example.com',
              giverSection: 'Section A',
              recipient: 'Bob Ruth',
              recipientTeam: 'Team A',
              recipientEmail: 'bob@example.com',
              recipientSection: 'Section A',
              responseDetails: {
                answer: '<p>Good</p>',
                isOther: false,
                otherFieldContent: '',
                questionType: FeedbackQuestionType.MCQ,
              } as FeedbackMcqResponseDetails,
              instructorComments: [],
              isMissingResponse: false,
            },
            {
              responseId: 'feedbackQuestionId%bob@example.com%danny@example.com',
              giver: 'Bob Ruth',
              giverTeam: 'Team A',
              giverEmail: 'bob@example.com',
              giverSection: 'Section A',
              recipient: 'Danny Engrid',
              recipientTeam: 'Team B',
              recipientEmail: 'danny@example.com',
              recipientSection: 'Section B',
              responseDetails: {
                answer: '<p>Bad</p>',
                isOther: false,
                otherFieldContent: '',
                questionType: FeedbackQuestionType.MCQ,
              } as FeedbackMcqResponseDetails,
              instructorComments: [],
              isMissingResponse: false,
            },
          ],
          responsesToSelf: [],
          responsesFromSelf: [],
          otherResponses: [],
        },
      ],
      hasPopulated: true,
      isTabExpanded: true,
    },
  };

  readonly exampleQuestionsWithResponses: QuestionOutput[] = [{
    feedbackQuestion: {
      feedbackQuestionId: 'ag50ZWFtbWF0ZXMtam9obnIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgIDICQw',
      questionNumber: 1,
      questionBrief: 'How well did this team member perform?',
      questionDescription: '',
      questionDetails: {
        ...DEFAULT_MCQ_QUESTION_DETAILS(),
        questionText: 'How well did this team member perform?',
        numOfMcqChoices: 3,
        mcqChoices: [
          '<p>Good</p>',
          '<p>Normal</p>',
          '<p>Bad</p>',
        ],
      } as FeedbackMcqQuestionDetails,
      questionType: FeedbackQuestionType.MCQ,
      giverType: FeedbackParticipantType.STUDENTS,
      recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF,
      numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
      showResponsesTo: [],
      showGiverNameTo: [],
      showRecipientNameTo: [],
      customNumberOfEntitiesToGiveFeedbackTo: 0,
    },
    questionStatistics: '',
    allResponses: [this.exampleResponseWithComment],
    responsesToSelf: [
      {
        responseId: 'ag50ZWFtbWF0ZXMtam9obnIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgIDICQw' +
            '%benny.c.tmms@gmail.tmt%alice.b.tmms@gmail.tmt',
        giver: 'Benny Charles',
        giverTeam: 'Team A',
        giverSection: 'Section A',
        recipient: 'You',
        recipientTeam: 'Team A',
        recipientSection: 'Section A',
        responseDetails: {
          ...DEFAULT_MCQ_RESPONSE_DETAILS(),
          answer: '<p>Good</p>',
        } as FeedbackMcqResponseDetails,
        instructorComments: [],
        isMissingResponse: false,
      },
    ],
    responsesFromSelf: [
      {
        responseId: 'ag50ZWFtbWF0ZXMtam9obnIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgIDICQw' +
            '%alice.b.tmms@gmail.tmt%alice.b.tmms@gmail.tmt',
        giver: 'You',
        giverTeam: 'Team A',
        giverSection: 'Section A',
        recipient: 'You',
        recipientSection: 'Section A',
        recipientTeam: 'Team A',
        responseDetails: {
          ...DEFAULT_MCQ_RESPONSE_DETAILS(),
          answer: '<p>Good</p>',
        } as FeedbackMcqResponseDetails,
        instructorComments: [],
        isMissingResponse: false,
      },
      {
        responseId: 'ag50ZWFtbWF0ZXMtam9obnIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgIDICQw' +
            '     %alice.b.tmms@gmail.tmt%benny.c.tmms@gmail.tmt',
        giver: 'You',
        giverTeam: 'Team A',
        giverSection: 'Section A',
        recipient: 'Benny Charles',
        recipientTeam: 'Team A',
        recipientSection: 'Section A',
        responseDetails: {
          ...DEFAULT_MCQ_RESPONSE_DETAILS(),
          answer: '<p>Bad</p>',
        } as FeedbackMcqResponseDetails,
        instructorComments: [],
        isMissingResponse: false,
      },
    ],
    otherResponses: [],
  }];

  readonly exampleCommentSearchResult: SearchCommentsTable[] = [{
    feedbackSession: this.exampleFeedbackSession,
    questions: this.exampleQuestionsWithResponses,
  }];

  questionsToCollapsed: Record<string, boolean> = {
    [SessionsSectionQuestions.TIPS_FOR_CONDUCTION_PEER_EVAL]: false,
    [SessionsSectionQuestions.SESSION_NEW_FEEDBACK]: false,
    [SessionsSectionQuestions.SESSION_QUESTIONS]: false,
    [SessionsSectionQuestions.SESSION_PREVIEW]: false,
    [SessionsSectionQuestions.SESSION_CANNOT_SUBMIT]: false,
    [SessionsSectionQuestions.SESSION_VIEW_RESULTS]: false,
    [SessionsSectionQuestions.VIEW_ALL_RESPONSES]: false,
    [SessionsSectionQuestions.SESSION_ADD_COMMENTS]: false,
    [SessionsSectionQuestions.EDIT_DEL_COMMENT]: false,
    [SessionsSectionQuestions.SESSION_SEARCH]: false,
    [SessionsSectionQuestions.VIEW_DELETED_SESSION]: false,
    [SessionsSectionQuestions.RESTORE_SESSION]: false,
    [SessionsSectionQuestions.PERMANENT_DEL_SESSION]: false,
    [SessionsSectionQuestions.RESTORE_DEL_ALL]: false,
  };

  constructor(private pageScrollService: PageScrollService,
              @Inject(DOCUMENT) private document: any) {
    super();
  }

  /**
   * Scrolls to an HTML element with a given target id.
   */
  jumpTo(target: string): boolean {
    this.pageScrollService.scroll({
      document: this.document,
      scrollTarget: `#${target}`,
      scrollOffset: 70,
    });
    return false;
  }

  expand(questionId: string): void {
    this.questionsToCollapsed[questionId] = true;
  }
}
