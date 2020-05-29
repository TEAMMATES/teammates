import { ComponentFixture, TestBed } from '@angular/core/testing';
import {
  CommentVisibilityType,
  FeedbackQuestionType,
  ResponseOutput,
} from '../../../types/api-output';
import { CommentToCommentRowModelPipe } from './comment-to-comment-row-model.pipe';
import { CommentsToCommentTableModelPipe } from './comments-to-comment-table-model.pipe';

describe('CommentsToCommentTableModelPipe', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CommentsToCommentTableModelPipe],
      providers: [CommentToCommentRowModelPipe],
    });
  });
  it('converts response output to comment table model correctly', () => {
    const pipe: ComponentFixture<CommentsToCommentTableModelPipe> = TestBed.createComponent(
        CommentsToCommentTableModelPipe);
    const response: ResponseOutput = {
      responseId: 'responseId',
      giver: 'giver',
      relatedGiverEmail: 'relatedGiverEmail',
      giverTeam: 'giverTeam',
      giverEmail: 'giverEmail',
      giverSection: 'giverSection',
      recipient: 'recipient',
      recipientTeam: 'recipientTeam',
      recipientEmail: 'recipientEmail',
      recipientSection: 'recipientSection',
      responseDetails: { questionType: FeedbackQuestionType.MCQ },
      participantComment: {
        commentGiver: 'commentGiver',
        lastEditorEmail: 'lastEditorEmail',
        feedbackResponseCommentId: 0,
        commentText: 'commentText',
        createdAt: 0,
        lastEditedAt: 0,
        isVisibilityFollowingFeedbackQuestion: false,
        showGiverNameTo: [],
        showCommentTo: [],
        commentGiverName: 'commentGiverName',
        lastEditorName: 'lastEditorName',
      },
      instructorComments: [{
        commentGiver: 'commentGiver';
        lastEditorEmail: 'lastEditorEmail';
        feedbackResponseCommentId: 0;
        commentText: 'commentText';
        createdAt: 0;
        lastEditedAt: 0;
        isVisibilityFollowingFeedbackQuestion: boolean;
        showGiverNameTo: CommentVisibilityType[];
        showCommentTo: CommentVisibilityType[];
      }],
    };
    expect(pipe).toBeTruthy();
  });
});
