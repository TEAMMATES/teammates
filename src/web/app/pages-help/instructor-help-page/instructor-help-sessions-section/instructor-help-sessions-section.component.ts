import { Component, OnInit } from '@angular/core';

import { TemplateSession } from '../../../../services/feedback-sessions.service';
import {
  Course,
  FeedbackSession,
  Instructor,
  QuestionOutput,
  ResponseOutput,
  Student,
} from '../../../../types/api-output';
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
import { collapseAnim } from '../../../components/teammates-common/collapse-anim';
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
import {
  EXAMPLE_COMMENT_EDIT_FORM_MODEL,
  EXAMPLE_COMMENT_SEARCH_RESULT,
  EXAMPLE_COURSE_CANDIDATES,
  EXAMPLE_FEEDBACK_SESSION,
  EXAMPLE_GRQ_RESPONSES,
  EXAMPLE_INSTRUCTORS,
  EXAMPLE_INSTRUCTOR_COMMENT_TABLE_MODEL,
  EXAMPLE_QUESTIONS_WITH_RESPONSES,
  EXAMPLE_RECYCLE_BIN_FEEDBACK_SESSIONS,
  EXAMPLE_RESPONSE,
  EXAMPLE_RESPONSE_WITH_COMMENT,
  EXAMPLE_SESSION_EDIT_FORM_MODEL,
  EXAMPLE_STUDENTS,
  EXAMPLE_TEMPLATE_SESSIONS,
} from './instructor-help-sessions-data';
import { SessionsSectionQuestions } from './sessions-section-questions';

/**
 * Sessions Section of the Instructor Help Page.
 */
@Component({
  selector: 'tm-instructor-help-sessions-section',
  templateUrl: './instructor-help-sessions-section.component.html',
  styleUrls: ['./instructor-help-sessions-section.component.scss'],
  animations: [collapseAnim],
})
export class InstructorHelpSessionsSectionComponent extends InstructorHelpSectionComponent implements OnInit {

  // enum
  CommentRowMode: typeof CommentRowMode = CommentRowMode;
  SessionEditFormMode: typeof SessionEditFormMode = SessionEditFormMode;
  InstructorSessionResultViewType: typeof InstructorSessionResultViewType = InstructorSessionResultViewType;
  InstructorSessionResultSectionType: typeof InstructorSessionResultSectionType = InstructorSessionResultSectionType;
  SessionsSectionQuestions: typeof SessionsSectionQuestions = SessionsSectionQuestions;

  readonly exampleCommentEditFormModel: CommentEditFormModel = EXAMPLE_COMMENT_EDIT_FORM_MODEL;
  readonly exampleSessionEditFormModel: SessionEditFormModel = EXAMPLE_SESSION_EDIT_FORM_MODEL;
  readonly exampleResponse: ResponseOutput = EXAMPLE_RESPONSE;
  readonly exampleResponseWithComment: ResponseOutput = EXAMPLE_RESPONSE_WITH_COMMENT;
  readonly exampleCourseCandidates: Course[] = EXAMPLE_COURSE_CANDIDATES;
  readonly exampleTemplateSessions: TemplateSession[] = EXAMPLE_TEMPLATE_SESSIONS;
  readonly exampleStudents: Student[] = EXAMPLE_STUDENTS;
  readonly exampleInstructors: Instructor[] = EXAMPLE_INSTRUCTORS;
  readonly exampleFeedbackSession: FeedbackSession = EXAMPLE_FEEDBACK_SESSION;
  readonly exampleRecycleBinFeedbackSessions: RecycleBinFeedbackSessionRowModel[]
    = EXAMPLE_RECYCLE_BIN_FEEDBACK_SESSIONS;
  readonly exampleInstructorCommentTableModel: Record<string, CommentTableModel>
    = EXAMPLE_INSTRUCTOR_COMMENT_TABLE_MODEL;
  readonly exampleGrqResponses: Record<string, SectionTabModel> = EXAMPLE_GRQ_RESPONSES;
  readonly exampleQuestionsWithResponses: QuestionOutput[] = EXAMPLE_QUESTIONS_WITH_RESPONSES;
  readonly exampleCommentSearchResult: SearchCommentsTable[] = EXAMPLE_COMMENT_SEARCH_RESULT;

  readonly questionsOrder: string[] = [
    SessionsSectionQuestions.TIPS_FOR_CONDUCTION_PEER_EVAL,
    SessionsSectionQuestions.SESSION_NEW_FEEDBACK,
    SessionsSectionQuestions.SESSION_QUESTIONS,
    SessionsSectionQuestions.SESSION_PREVIEW,
    SessionsSectionQuestions.SESSION_CANNOT_SUBMIT,
    SessionsSectionQuestions.SESSION_VIEW_RESULTS,
    SessionsSectionQuestions.VIEW_ALL_RESPONSES,
    SessionsSectionQuestions.SESSION_ADD_COMMENTS,
    SessionsSectionQuestions.EDIT_DEL_COMMENT,
    SessionsSectionQuestions.SESSION_SEARCH,
    SessionsSectionQuestions.VIEW_DELETED_SESSION,
    SessionsSectionQuestions.RESTORE_SESSION,
    SessionsSectionQuestions.PERMANENT_DEL_SESSION,
    SessionsSectionQuestions.RESTORE_DEL_ALL,
  ];

  getQuestionsOrder(): string[] {
    return this.questionsOrder;
  }

}
