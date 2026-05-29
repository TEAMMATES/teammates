import { CommentEditFormModel } from './comment-edit-form/comment-edit-form.component';
import {
  GiverCommentRowModel,
  InstructorCommentRowModel,
  NewCommentRowModel,
} from './comment-row/comment-row.component';
import { CommentVisibilityStateMachine } from '../../../services/comment-visibility-state-machine';
import { CommentVisibilityControl } from '../../../types/comment-visibility-control';
import { CommentVisibilityType, FeedbackResponseComment, FeedbackVisibilityType } from '../../../types/api-output';

interface CommentVisibilityModel {
  showCommentTo: CommentVisibilityType[];
  showGiverNameTo: CommentVisibilityType[];
}

function getDefaultCommentVisibility(questionShowResponsesTo: FeedbackVisibilityType[]): CommentVisibilityModel {
  const visibilityStateMachine = new CommentVisibilityStateMachine(questionShowResponsesTo);
  visibilityStateMachine.allowAllApplicableTypesToSee();
  return {
    showCommentTo: visibilityStateMachine.getVisibilityTypesUnderVisibilityControl(
      CommentVisibilityControl.SHOW_COMMENT,
    ),
    showGiverNameTo: visibilityStateMachine.getVisibilityTypesUnderVisibilityControl(
      CommentVisibilityControl.SHOW_GIVER_NAME,
    ),
  };
}

export function createNewCommentRowModel(
  questionShowResponsesTo: FeedbackVisibilityType[] = [],
  isEditing = false,
): NewCommentRowModel {
  const { showCommentTo, showGiverNameTo } = getDefaultCommentVisibility(questionShowResponsesTo);
  return {
    commentType: 'new',
    commentEditFormModel: {
      commentText: '',
      showCommentTo,
      showGiverNameTo,
    },
    isEditing,
  };
}

export function giverCommentToCommentRowModel(
  commentText: string,
  questionShowResponsesTo: FeedbackVisibilityType[] = [],
): GiverCommentRowModel {
  const { showCommentTo, showGiverNameTo } = getDefaultCommentVisibility(questionShowResponsesTo);
  const originalCommentFormModel: CommentEditFormModel = {
    commentText,
    showCommentTo,
    showGiverNameTo,
  };

  return {
    commentType: 'giver',
    originalCommentFormModel,
    commentEditFormModel: structuredClone(originalCommentFormModel),
    isEditing: false,
  };
}

export function instructorCommentToCommentRowModel(
  comment: FeedbackResponseComment,
  timezone: string,
): InstructorCommentRowModel {
  const originalCommentFormModel: CommentEditFormModel = {
    commentText: comment.commentText,
    showCommentTo: comment.showCommentTo,
    showGiverNameTo: comment.showGiverNameTo,
  };

  return {
    commentType: 'instructor',
    commentId: comment.feedbackResponseCommentId,
    commentGiverName: comment.commentGiverName,
    lastEditorName: comment.lastEditorName,
    createdAt: comment.createdAt,
    lastEditedAt: comment.lastEditedAt,
    timezone,
    originalCommentFormModel,
    commentEditFormModel: structuredClone(originalCommentFormModel),
    isEditing: false,
  };
}
