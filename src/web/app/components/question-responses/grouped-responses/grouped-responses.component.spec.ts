import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { GroupedResponsesComponent } from './grouped-responses.component';
import {
  ResponseModerationButtonModule,
} from '../../../pages-instructor/instructor-session-result-page/response-moderation-button/response-moderation-button.module';
import { CommentBoxModule } from '../../comment-box/comment-box.module';
import { QuestionTextWithInfoModule } from '../../question-text-with-info/question-text-with-info.module';
import { TeammatesCommonModule } from '../../teammates-common/teammates-common.module';
import { SingleResponseModule } from '../single-response/single-response.module';
import {
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  QuestionOutput,
  ResponseOutput,
  ResponseVisibleSetting,
  SessionVisibleSetting,
  FeedbackQuestionType,
  FeedbackParticipantType,
  NumberOfEntitiesToGiveFeedbackToSetting,
} from '../../../../types/api-output';
import { CommentTableModel } from '../../comment-box/comment-table/comment-table.component';

describe('GroupedResponsesComponent', () => {
  let component: GroupedResponsesComponent;
  let fixture: ComponentFixture<GroupedResponsesComponent>;

  const mockSession: FeedbackSession = {
    courseId: 'test-course',
    timeZone: 'UTC',
    feedbackSessionName: 'Test Session',
    instructions: '',
    submissionStartTimestamp: 0,
    submissionEndTimestamp: 0,
    gracePeriod: 0,
    sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
    responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
    submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
    publishStatus: FeedbackSessionPublishStatus.NOT_PUBLISHED,
    isClosingSoonEmailEnabled: true,
    isPublishedEmailEnabled: true,
    createdAtTimestamp: 0,
    studentDeadlines: {},
    instructorDeadlines: {},
  };

  const mockResponse: ResponseOutput = {
    responseId: 'response-1',
    giver: 'giver-1',
    giverTeam: 'Team 1',
    giverEmail: 'giver1@example.com',
    recipient: 'recipient-1',
    recipientTeam: 'Team 2',
    recipientEmail: 'recipient1@example.com',
    giverSection: 'Section 1',
    recipientSection: 'Section 2',
    responseDetails: {
      questionType: FeedbackQuestionType.TEXT,
      answer: 'Test response',
    } as any,
    isMissingResponse: false,
    instructorComments: [],
  };

  const mockQuestionOutput: QuestionOutput = {
    feedbackQuestion: {
      feedbackQuestionId: 'test-question-1',
      questionNumber: 1,
      questionBrief: 'Test question',
      questionDescription: 'Test description',
      questionDetails: {
        questionType: FeedbackQuestionType.TEXT,
        questionText: 'Test question text',
      },
      questionType: FeedbackQuestionType.TEXT,
      giverType: FeedbackParticipantType.STUDENTS,
      recipientType: FeedbackParticipantType.STUDENTS,
      numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
      customNumberOfEntitiesToGiveFeedbackTo: 0,
      showResponsesTo: [],
      showGiverNameTo: [],
      showRecipientNameTo: [],
    },
    allResponses: [mockResponse],
    questionStatistics: '',
    hasResponseButNotVisibleForPreview: false,
    hasCommentNotVisibleForPreview: false,
    responsesToSelf: [],
    responsesFromSelf: [],
    otherResponses: [],
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [GroupedResponsesComponent],
      imports: [
        QuestionTextWithInfoModule,
        SingleResponseModule,
        CommentBoxModule,
        ResponseModerationButtonModule,
        TeammatesCommonModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GroupedResponsesComponent);
    component = fixture.componentInstance;
    component.session = mockSession;
    component.responses = [mockQuestionOutput];
    component.userToEmail = {
      'giver-1': 'giver1@example.com',
      'recipient-1': 'recipient1@example.com',
    };
    component.instructorCommentTableModel = {
      'response-1': {
        isAddingNewComment: false,
        isReadOnly: false,
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
      },
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('hasRealResponses: should return true when there are real responses', () => {
    component.ngOnInit();
    expect(component.hasRealResponses).toBeTruthy();
  });

  it('hasRealResponses: should return false when all responses are missing', () => {
    const missingResponse = { ...mockResponse, isMissingResponse: true };
    const missingQuestionOutput = {
      ...mockQuestionOutput,
      allResponses: [missingResponse],
    };
    component.responses = [missingQuestionOutput];
    component.ngOnInit();
    expect(component.hasRealResponses).toBeFalsy();
  });

  it('teamInfo: should return correct team info for GRQ mode', () => {
    component.isGrq = true;
    const teamInfo = component.teamInfo;
    expect(teamInfo['recipient']).toBe('(Team 2)');
    expect(teamInfo['giver']).toBe('(Team 1)');
  });

  it('teamInfo: should return correct team info for RGQ mode', () => {
    component.isGrq = false;
    const teamInfo = component.teamInfo;
    expect(teamInfo['recipient']).toBe('(Team 2)');
    expect(teamInfo['giver']).toBe('(Team 1)');
  });

  it('teamInfo: should handle empty team name', () => {
    const emptyTeamResponse = { ...mockResponse, recipientTeam: '' };
    const emptyTeamQuestionOutput = {
      ...mockQuestionOutput,
      allResponses: [emptyTeamResponse],
    };
    component.responses = [emptyTeamQuestionOutput];
    component.isGrq = true;
    const teamInfo = component.teamInfo;
    expect(teamInfo['recipient']).toBe('');
  });

  it('teamInfo: should handle no specific team', () => {
    const noTeamResponse = { ...mockResponse, recipientTeam: '-' };
    const noTeamQuestionOutput = {
      ...mockQuestionOutput,
      allResponses: [noTeamResponse],
    };
    component.responses = [noTeamQuestionOutput];
    component.isGrq = true;
    const teamInfo = component.teamInfo;
    expect(teamInfo['recipient']).toBe('(No Specific Team)');
  });

  it('toggleAddComment: should toggle comment addition state', () => {
    const responseId = 'response-1';
    const initialCommentTable: CommentTableModel = {
      isAddingNewComment: false,
      isReadOnly: false,
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
    };
    component.instructorCommentTableModel[responseId] = initialCommentTable;

    component.toggleAddComment(responseId);
    expect(component.instructorCommentTableModel[responseId].isAddingNewComment).toBeTruthy();

    component.toggleAddComment(responseId);
    expect(component.instructorCommentTableModel[responseId].isAddingNewComment).toBeFalsy();
  });
});
