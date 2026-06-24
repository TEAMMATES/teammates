import {
  CommentEditFormModel,
  GiverCommentRowModel,
  InstructorCommentRowModel,
  NewCommentRowModel,
} from './comment.model';
import { ResponseInstructorComment } from '../../../types/api-output';

export function createNewCommentRowModel(isEditing = false): NewCommentRowModel {
  return {
    commentType: 'new',
    commentEditFormModel: {
      commentText: '',
    },
    isEditing,
  };
}

export function giverCommentToCommentRowModel(commentText: string): GiverCommentRowModel {
  const originalCommentFormModel: CommentEditFormModel = {
    commentText,
  };

  return {
    commentType: 'giver',
    originalCommentFormModel,
    commentEditFormModel: structuredClone(originalCommentFormModel),
    isEditing: false,
  };
}

export function instructorCommentToCommentRowModel(
  comment: ResponseInstructorComment,
  timezone: string,
): InstructorCommentRowModel {
  const originalCommentFormModel: CommentEditFormModel = {
    commentText: comment.commentText,
  };

  return {
    commentType: 'instructor',
    commentId: comment.responseInstructorCommentId,
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
