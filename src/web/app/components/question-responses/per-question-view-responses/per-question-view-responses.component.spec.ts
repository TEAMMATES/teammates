import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { RouterModule } from '@angular/router';
import {
  CommentOutput, FeedbackQuestion, FeedbackTextQuestionDetails,
  FeedbackTextResponseDetails, ResponseOutput,
} from 'src/web/types/api-output';
import SpyInstance = jest.SpyInstance;
import { PerQuestionViewResponsesComponent } from './per-question-view-responses.component';
import { FeedbackResponsesService } from '../../../../services/feedback-responses.service';
import testEventEmission from '../../../../test-helpers/test-event-emitter';
import {
  CommentVisibilityType, FeedbackParticipantType, FeedbackQuestionType,
  NumberOfEntitiesToGiveFeedbackToSetting,
} from '../../../../types/api-request';
import {
  ResponseModerationButtonModule,
} from '../../../pages-instructor/instructor-session-result-page/response-moderation-button/response-moderation-button.module';
import { CommentBoxModule } from '../../comment-box/comment-box.module';
import { CommentRowModel } from '../../comment-box/comment-row/comment-row.component';
import { CommentTableModel } from '../../comment-box/comment-table/comment-table.component';
import { RichTextEditorModule } from '../../rich-text-editor/rich-text-editor.module';
import { TeammatesCommonModule } from '../../teammates-common/teammates-common.module';
import { SingleResponseModule } from '../single-response/single-response.module';

describe('PerQuestionViewResponsesComponent', () => {
  let component: PerQuestionViewResponsesComponent;
  let fixture: ComponentFixture<PerQuestionViewResponsesComponent>;

  let feedbackResponsesService: FeedbackResponsesService;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [PerQuestionViewResponsesComponent],
      imports: [
        SingleResponseModule,
        CommentBoxModule,
        TeammatesCommonModule,
        HttpClientTestingModule,
        RichTextEditorModule,
        RouterModule,
        ResponseModerationButtonModule,
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PerQuestionViewResponsesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    feedbackResponsesService = TestBed.inject(FeedbackResponsesService);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  const commentOutput: CommentOutput = {
    commentGiverName: 'Jennie Kim',
    lastEditorName: 'Jennie Kim',
    commentGiver: 'Jennie Kim',
    lastEditorEmail: 'jenniekim@gmail.com',
    feedbackResponseCommentId: 3,
    commentText: 'commentText',
    createdAt: 0,
    lastEditedAt: 0,
    isVisibilityFollowingFeedbackQuestion: false,
    showGiverNameTo: [],
    showCommentTo: [],
  };

  const responseOutput: ResponseOutput = {
    isMissingResponse: false,
    responseId: 'resp-id-101',
    giver: 'Jennie Kim',
    giverEmail: 'jenniekim@gmail.com',
    giverTeam: 'Tutorial Group 135',
    giverSection: 'section2',
    recipient: 'Lisa Mano',
    recipientTeam: 'Tutorial Group 246',
    recipientEmail: 'lisamano@gmail.com',
    recipientSection: 'section2',
    responseDetails: {
      answer: '<p>Lisa is a good classmate </p>',
    } as FeedbackTextResponseDetails,
    instructorComments: [],
    participantComment: commentOutput,
  };

  const feedbackQuestion: FeedbackQuestion = {
    feedbackQuestionId: 'feedbackQuestion22',
    questionNumber: 22,
    questionBrief: 'What did you think of the class contribution of this classmate?',
    questionDescription: '',
    questionDetails: {
      shouldAllowRichText: true,
    } as FeedbackTextQuestionDetails,
    questionType: FeedbackQuestionType.TEXT,
    giverType: FeedbackParticipantType.STUDENTS,
    recipientType: FeedbackParticipantType.STUDENTS_EXCLUDING_SELF,
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    showResponsesTo: [],
    showGiverNameTo: [],
    showRecipientNameTo: [],
    customNumberOfEntitiesToGiveFeedbackTo: 0,
  };

  const commentRowModel: CommentRowModel = {
    commentEditFormModel: {
      commentText: '',
      isUsingCustomVisibilities: false,
      showCommentTo: [CommentVisibilityType.RECIPIENT],
      showGiverNameTo: [CommentVisibilityType.RECIPIENT],
    },
    isEditing: false,
  };

  const commentTableModel: CommentTableModel = {
    commentRows: [],
    newCommentRow: commentRowModel,
    isAddingNewComment: false,
    isReadOnly: false,
  };

  const instructorCommentTableModel: Record<string, CommentTableModel> = {
    'resp-id-101': commentTableModel,
  };

  it('should snap response with comments', () => {
    component.question = feedbackQuestion;
    component.instructorCommentTableModel = instructorCommentTableModel;
    component.responses = [responseOutput];
    const feedbackResponseSpy: SpyInstance = jest.spyOn(feedbackResponsesService,
      'isFeedbackResponsesDisplayedOnSection')
      .mockReturnValue(true);

    component.ngOnInit();
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();

    expect(feedbackResponseSpy).toHaveBeenCalledTimes(1);
    expect(JSON.stringify(component.responsesToShow[0]))
      .toBe(JSON.stringify(responseOutput));
  });

  it('triggerDeleteCommentEvent: should emit correct responseID and index to deleteCommentEvent', () => {
    let emittedID: string | undefined;
    let emittedIndex: number | undefined;
    testEventEmission(component.deleteCommentEvent, (val) => { emittedID = val.responseId; emittedIndex = val.index; });

    component.triggerDeleteCommentEvent('testID', 5);
    expect(emittedID).toBe('testID');
    expect(emittedIndex).toBe(5);
  });

  it('triggerUpdateCommentEvent: should emit correct responseID and index to updateCommentEvent', () => {
    let emittedID: string | undefined;
    let emittedIndex: number | undefined;
    testEventEmission(component.updateCommentEvent, (val) => { emittedID = val.responseId; emittedIndex = val.index; });

    component.triggerUpdateCommentEvent('testID2', 6);
    expect(emittedID).toBe('testID2');
    expect(emittedIndex).toBe(6);
  });

  it('triggerSaveNewCommentEvent: should emit correct responseID to saveNewCommentEvent', () => {
    let emittedID: string | undefined;
    testEventEmission(component.saveNewCommentEvent, (responseId) => { emittedID = responseId; });

    component.triggerSaveNewCommentEvent('testID3');
    expect(emittedID).toBe('testID3');
  });

  it('triggerModelChangeForSingleResponse: should emit correct Record to instructorCommentTableModelChange', () => {
    let emittedRecord: Record<string, CommentTableModel> | undefined;
    testEventEmission(component.instructorCommentTableModelChange, (record) => { emittedRecord = record; });

    const testRecord: Record<string, CommentTableModel> = { responseId: commentTableModel };

    component.triggerModelChangeForSingleResponse('responseId', commentTableModel);
    expect(emittedRecord).toEqual(testRecord);
  });

  it('triggerModelChange: should emit correct instructorCommentTableModel Record to triggerModelChange', () => {
    let emittedRecord: Record<string, CommentTableModel> | undefined;
    testEventEmission(component.instructorCommentTableModelChange, (record) => { emittedRecord = record; });

    const testRecord: Record<string, CommentTableModel> = {};

    component.triggerModelChange(testRecord);
    expect(emittedRecord).toEqual(testRecord);
  });
});
