import { CommentEditFormModel } from './comment-edit-form/comment-edit-form.component';
import {
  GiverCommentRowModel,
  InstructorCommentRowModel,
  NewCommentRowModel,
} from './comment-row/comment-row.component';
import { FeedbackResponseComment } from '../../../types/api-output';

export function createNewCommentRowModel(isEditing = false): NewCommentRowModel {
  return {
    commentType: 'new',
    commentEditFormModel: {
      commentText: '',
      isUsingCustomVisibilities: false,
      showCommentTo: [],
      showGiverNameTo: [],
    },
    isEditing,
  };
}

export function giverCommentToCommentRowModel(commentText: string): GiverCommentRowModel {
  const originalCommentFormModel: CommentEditFormModel = {
    commentText,
    isUsingCustomVisibilities: false,
    showCommentTo: [],
    showGiverNameTo: [],
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
    isUsingCustomVisibilities: !comment.isVisibilityFollowingFeedbackQuestion,
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
