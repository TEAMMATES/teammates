export interface CommentEditFormModel {
  commentText: string;
}

interface CommentRowBaseModel {
  commentEditFormModel: CommentEditFormModel;
  isEditing: boolean;
}

export interface NewCommentRowModel extends CommentRowBaseModel {
  commentType: 'new';
}

interface SavedCommentRowBaseModel extends CommentRowBaseModel {
  originalCommentFormModel: CommentEditFormModel;
}

export interface GiverCommentRowModel extends SavedCommentRowBaseModel {
  commentType: 'giver';
}

export interface InstructorCommentRowModel extends SavedCommentRowBaseModel {
  commentType: 'instructor';
  commentId: string;
  commentGiverName: string;
  createdAt: number;
  timezone: string;
}

export type SavedCommentRowModel = GiverCommentRowModel | InstructorCommentRowModel;

export type CommentRowModel = NewCommentRowModel | SavedCommentRowModel;
