import { Component, OnInit } from '@angular/core';
import {
  EXAMPLE_COMMENT_EDIT_FORM_MODEL,
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
import { environment } from '../../../../environments/environment';
import { TemplateSession } from '../../../../services/feedback-sessions.service';
import {
  Course,
  FeedbackSession,
  Instructor,
  ResponseOutput,
  Student,
} from '../../../../types/api-output';
import { CommentEditFormModel } from '../../../components/comment-box/comment-edit-form/comment-edit-form.component';
import { CommentRowMode } from '../../../components/comment-box/comment-row/comment-row.mode';
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
  SectionTabModel,
} from '../../../pages-instructor/instructor-session-result-page/instructor-session-result-page.component';
import {
  InstructorSessionResultSectionType,
} from '../../../pages-instructor/instructor-session-result-page/instructor-session-result-section-type.enum';
import {
  InstructorSessionResultViewType,
} from '../../../pages-instructor/instructor-session-result-page/instructor-session-result-view-type.enum';
import { FeedbackQuestionModel } from '../../../pages-session/session-result-page/session-result-page.component';
import { InstructorHelpSectionComponent } from '../instructor-help-section.component';
import { Sections } from '../sections';

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

  // enums
  CommentRowMode: typeof CommentRowMode = CommentRowMode;
  SessionEditFormMode: typeof SessionEditFormMode = SessionEditFormMode;
  InstructorSessionResultViewType: typeof InstructorSessionResultViewType = InstructorSessionResultViewType;
  InstructorSessionResultSectionType: typeof InstructorSessionResultSectionType = InstructorSessionResultSectionType;
  SessionsSectionQuestions: typeof SessionsSectionQuestions = SessionsSectionQuestions;
  Sections: typeof Sections = Sections;

  readonly supportEmail: string = environment.supportEmail;
  readonly frontendUrl: string = environment.frontendUrl;
  readonly exampleCommentEditFormModel: CommentEditFormModel = EXAMPLE_COMMENT_EDIT_FORM_MODEL;
  readonly exampleSessionEditFormModel: SessionEditFormModel = EXAMPLE_SESSION_EDIT_FORM_MODEL;
  readonly exampleResponse: ResponseOutput = EXAMPLE_RESPONSE;
  readonly exampleResponseWithComment: ResponseOutput = EXAMPLE_RESPONSE_WITH_COMMENT;
  readonly exampleCourseCandidates: Course[] = EXAMPLE_COURSE_CANDIDATES;
  readonly exampleTemplateSessions: TemplateSession[] = EXAMPLE_TEMPLATE_SESSIONS;
  readonly exampleStudents: Student[] = EXAMPLE_STUDENTS;
  readonly exampleInstructors: Instructor[] = EXAMPLE_INSTRUCTORS;
  readonly exampleFeedbackSession: FeedbackSession = EXAMPLE_FEEDBACK_SESSION;
  readonly exampleRecycleBinFeedbackSessions: RecycleBinFeedbackSessionRowModel[] =
    EXAMPLE_RECYCLE_BIN_FEEDBACK_SESSIONS;
  readonly exampleInstructorCommentTableModel: Record<string, CommentTableModel> =
    EXAMPLE_INSTRUCTOR_COMMENT_TABLE_MODEL;
  readonly exampleGrqResponses: Record<string, SectionTabModel> = EXAMPLE_GRQ_RESPONSES;
  readonly exampleQuestionsWithResponses: FeedbackQuestionModel[] = EXAMPLE_QUESTIONS_WITH_RESPONSES;

  readonly questionsOrder: string[] = [
    SessionsSectionQuestions.TIPS_FOR_CONDUCTION_PEER_EVAL,
    SessionsSectionQuestions.SESSION_NEW_FEEDBACK,
    SessionsSectionQuestions.SESSION_QUESTIONS,
    SessionsSectionQuestions.SET_QUESTION_COMPULSORY,
    SessionsSectionQuestions.SESSION_PREVIEW,
    SessionsSectionQuestions.LET_STUDENT_KNOW_SESSION,
    SessionsSectionQuestions.STUDENT_DID_NOT_RECEIVE_SUBMISSION_LINK,
    SessionsSectionQuestions.EXTEND_SESSION_DEADLINE,
    SessionsSectionQuestions.CHANGE_VISIBILITY_AFTER_SESSION_START,
    SessionsSectionQuestions.STUDENT_EDIT_RESPONSE,
    SessionsSectionQuestions.SUBMIT_FOR_STUDENT,
    SessionsSectionQuestions.STUDENT_ACCESS_SUBMISSION_PAGE,
    SessionsSectionQuestions.SESSION_VIEW_RESULTS,
    SessionsSectionQuestions.VIEW_ALL_RESPONSES,
    SessionsSectionQuestions.MODERATE_RESPONSE,
    SessionsSectionQuestions.STUDENT_SEE_RESPONSE,
    SessionsSectionQuestions.STUDENT_DID_NOT_RECEIVE_RESULT_LINK,
    SessionsSectionQuestions.SESSION_ADD_COMMENTS,
    SessionsSectionQuestions.EDIT_DEL_COMMENT,
    SessionsSectionQuestions.VIEW_DELETED_SESSION,
    SessionsSectionQuestions.RESTORE_SESSION,
    SessionsSectionQuestions.PERMANENT_DEL_SESSION,
    SessionsSectionQuestions.RESTORE_DEL_ALL,
  ];

  getQuestionsOrder(): string[] {
    return this.questionsOrder;
  }

}
