import { Component, EventEmitter, OnInit, Output, TemplateRef } from '@angular/core';
import { NgbCollapse } from '@ng-bootstrap/ng-bootstrap/collapse';
import { NgbTooltip } from '@ng-bootstrap/ng-bootstrap/tooltip';
import {
  EXAMPLE_CONTRIBUTION_STATISTICS,
  EXAMPLE_DISTRIBUTED_POINT_OPTION_MODEL,
  EXAMPLE_DISTRIBUTED_POINT_RECIPIENT_MODEL,
  EXAMPLE_DISTRIBUTE_POINT_OPTION_QUESTIONS,
  EXAMPLE_DISTRIBUTE_POINT_OPTION_QUESTION_DETAIL,
  EXAMPLE_DISTRIBUTE_POINT_OPTION_QUESTION_TAB_MODEL,
  EXAMPLE_DISTRIBUTE_POINT_OPTION_RESPONSE_OUTPUT,
  EXAMPLE_ESSAY_QUESTION_MODEL,
  EXAMPLE_FEEDBACK_SESSION,
  EXAMPLE_INSTRUCTOR_COMMENT_TABLE_MODEL,
  EXAMPLE_MCQ_QUESTION_WITHOUT_WEIGHTS_MODEL,
  EXAMPLE_MCQ_QUESTION_WITH_WEIGHTS_MODEL,
  EXAMPLE_NUMERICAL_SCALE_QUESTIONS,
  EXAMPLE_NUMERICAL_SCALE_QUESTION_MODEL,
  EXAMPLE_NUMERICAL_SCALE_QUESTION_TAB_MODEL,
  EXAMPLE_NUMERICAL_SCALE_RESPONSES,
  EXAMPLE_NUMERICAL_SCALE_RESPONSE_OUTPUT,
  EXAMPLE_RANK_OPTION_QUESTION_MODEL,
  EXAMPLE_RANK_RECIPIENT_QUESTIONS,
  EXAMPLE_RANK_RECIPIENT_QUESTION_MODEL,
  EXAMPLE_RANK_RECIPIENT_QUESTION_TAB_MODEL,
  EXAMPLE_RANK_RECIPIENT_RESPONSE_OUTPUT,
  EXAMPLE_RESPONDER_RUBRIC_SUBMISSION_FORM_MODEL,
  EXAMPLE_RUBRIC_QUESTION_MODEL,
  EXAMPLE_RUBRIC_QUESTION_QUESTIONS,
  EXAMPLE_RUBRIC_QUESTION_QUESTION_TAB_MODEL,
  EXAMPLE_RUBRIC_QUESTION_RESPONSE_OUTPUT,
  EXAMPLE_TEAM_CONTRIBUTION_QUESTIONS,
  EXAMPLE_TEAM_CONTRIBUTION_QUESTION_MODEL,
  EXAMPLE_TEAM_CONTRIBUTION_QUESTION_TAB_MODEL,
  EXAMPLE_TEAM_CONTRIBUTION_RESPONSE_OUTPUT,
} from './instructor-help-questions-data';
import { QuestionsSectionQuestions } from './questions-section-questions';
import {
  FeedbackContributionCourseWideStatistics,
  FeedbackConstantSumOptionsQuestionDetails,
  FeedbackNumericalScaleResponseDetails,
  FeedbackSession,
  ResponseOutput,
} from '../../../../types/api-output';
import { CommentTableModel } from '../../../components/comment-box/comment-table/comment-table.model';
import {
  QuestionEditFormMode,
  QuestionEditFormModel,
} from '../../../components/question-edit-form/question-edit-form-model';
import { QuestionEditFormComponent } from '../../../components/question-edit-form/question-edit-form.component';
import { QuestionSubmissionFormModel } from '../../../components/question-submission-form/question-submission-form-model';
import { QuestionSubmissionFormComponent } from '../../../components/question-submission-form/question-submission-form.component';
import { SimpleModalType } from '../../../components/simple-modal/simple-modal-type';
import { TeammatesRouterDirective } from '../../../components/teammates-router/teammates-router.directive';
import { InstructorSessionResultQuestionViewComponent } from '../../../pages-instructor/instructor-session-result-page/instructor-session-result-question-view.component';
import { InstructorSessionResultSectionType } from '../../../pages-instructor/instructor-session-result-page/instructor-session-result-section-type.enum';
import { QuestionTabModel } from '../../../pages-instructor/instructor-session-result-page/instructor-session-tab.model';
import { ExampleBoxComponent } from '../example-box/example-box.component';
import { InstructorHelpPanelComponent } from '../instructor-help-panel/instructor-help-panel.component';
import { InstructorHelpSectionComponent } from '../instructor-help-section.component';
import { Sections } from '../sections';
import { Response } from '../../../../types/question-statistics.model';

/**
 * Questions Section of the Instructor Help Page.
 */
@Component({
  selector: 'tm-instructor-help-questions-section',
  templateUrl: './instructor-help-questions-section.component.html',
  styleUrls: ['./instructor-help-questions-section.component.scss'],
  imports: [
    InstructorHelpPanelComponent,
    ExampleBoxComponent,
    QuestionEditFormComponent,
    TeammatesRouterDirective,
    InstructorSessionResultQuestionViewComponent,
    NgbTooltip,
    QuestionSubmissionFormComponent,
    NgbCollapse,
  ],
})
export class InstructorHelpQuestionsSectionComponent extends InstructorHelpSectionComponent implements OnInit {
  // enums
  QuestionsSectionQuestions!: typeof QuestionsSectionQuestions;
  InstructorSessionResultSectionType!: typeof InstructorSessionResultSectionType;
  QuestionEditFormMode!: typeof QuestionEditFormMode;
  Sections!: typeof Sections;

  readonly exampleEssayQuestionModel: QuestionEditFormModel;
  readonly exampleNumericalScaleQuestionModel: QuestionEditFormModel;
  readonly exampleNumericalScaleResponses: Response<FeedbackNumericalScaleResponseDetails>[];
  readonly exampleNumericalScaleResponseOutput: ResponseOutput[];
  readonly exampleNumericalScaleQuestionTabModel: QuestionTabModel;
  readonly exampleNumericalScaleQuestions: Record<string, QuestionTabModel>;
  readonly exampleInstructorCommentTableModel: Record<string, CommentTableModel>;
  readonly exampleFeedbackSession: FeedbackSession;
  readonly exampleDistributePointOptionQuestionDetail: FeedbackConstantSumOptionsQuestionDetails;
  readonly exampleDistributedPointOptionModel: QuestionEditFormModel;
  readonly exampleDistributePointOptionResponseOutput: ResponseOutput[];
  readonly exampleDistributePointOptionQuestionTabModel: QuestionTabModel;
  readonly exampleDistributePointOptionQuestions: Record<string, QuestionTabModel>;
  readonly exampleDistributedPointRecipientModel: QuestionEditFormModel;
  readonly exampleTeamContributionQuestionModel: QuestionEditFormModel;
  readonly exampleTeamContributionResponseOutput: ResponseOutput[];
  readonly exampleContributionStatistics: FeedbackContributionCourseWideStatistics;
  readonly exampleTeamContributionQuestionTabModel: QuestionTabModel;
  readonly exampleTeamContributionQuestions: Record<string, QuestionTabModel>;
  readonly exampleRubricQuestionModel: QuestionEditFormModel;
  readonly exampleRubricQuestionResponseOutput: ResponseOutput[];
  readonly exampleRubricQuestionQuestionTabModel: QuestionTabModel;
  readonly exampleRubricQuestionQuestions: Record<string, QuestionTabModel>;
  readonly exampleRankRecipientQuestionModel: QuestionEditFormModel;
  readonly exampleRankRecipientResponseOutput: ResponseOutput[];
  readonly exampleRankRecipientQuestionTabModel: QuestionTabModel;
  readonly exampleRankRecipientQuestions: Record<string, QuestionTabModel>;
  readonly exampleRankOptionQuestionModel: QuestionEditFormModel;
  readonly exampleMCQQuestionWithoutWeightsModel: QuestionEditFormModel;
  readonly exampleMCQQuestionWithWeightsModel: QuestionEditFormModel;
  readonly exampleResponderRubricSubmissionFormModel: QuestionSubmissionFormModel;

  readonly questionsOrder: string[] = [
    QuestionsSectionQuestions.ESSAY,
    QuestionsSectionQuestions.SINGLE_ANSWER_MCQ,
    QuestionsSectionQuestions.MULTIPLE_ANSWER_MCQ,
    QuestionsSectionQuestions.NUMERICAL_SCALE,
    QuestionsSectionQuestions.POINTS_OPTIONS,
    QuestionsSectionQuestions.POINTS_RECIPIENTS,
    QuestionsSectionQuestions.CONTRIBUTION,
    QuestionsSectionQuestions.RUBRIC,
    QuestionsSectionQuestions.RANK_OPTIONS,
    QuestionsSectionQuestions.RANK_RECIPIENTS,
  ];

  @Output() collapsePeerEvalTips: EventEmitter<void> = new EventEmitter();

  constructor() {
    super();
    this.QuestionsSectionQuestions = QuestionsSectionQuestions;
    this.InstructorSessionResultSectionType = InstructorSessionResultSectionType;
    this.QuestionEditFormMode = QuestionEditFormMode;
    this.Sections = Sections;
    this.exampleEssayQuestionModel = structuredClone(EXAMPLE_ESSAY_QUESTION_MODEL);
    this.exampleNumericalScaleQuestionModel = structuredClone(EXAMPLE_NUMERICAL_SCALE_QUESTION_MODEL);
    this.exampleNumericalScaleResponses = structuredClone(EXAMPLE_NUMERICAL_SCALE_RESPONSES);
    this.exampleNumericalScaleResponseOutput = structuredClone(EXAMPLE_NUMERICAL_SCALE_RESPONSE_OUTPUT);
    this.exampleNumericalScaleQuestionTabModel = structuredClone(EXAMPLE_NUMERICAL_SCALE_QUESTION_TAB_MODEL);
    this.exampleNumericalScaleQuestions = structuredClone(EXAMPLE_NUMERICAL_SCALE_QUESTIONS);
    this.exampleInstructorCommentTableModel = structuredClone(EXAMPLE_INSTRUCTOR_COMMENT_TABLE_MODEL);
    this.exampleFeedbackSession = structuredClone(EXAMPLE_FEEDBACK_SESSION);
    this.exampleDistributePointOptionQuestionDetail = structuredClone(EXAMPLE_DISTRIBUTE_POINT_OPTION_QUESTION_DETAIL);
    this.exampleDistributedPointOptionModel = structuredClone(EXAMPLE_DISTRIBUTED_POINT_OPTION_MODEL);
    this.exampleDistributePointOptionResponseOutput = structuredClone(EXAMPLE_DISTRIBUTE_POINT_OPTION_RESPONSE_OUTPUT);
    this.exampleDistributePointOptionQuestionTabModel = structuredClone(
      EXAMPLE_DISTRIBUTE_POINT_OPTION_QUESTION_TAB_MODEL,
    );
    this.exampleDistributePointOptionQuestions = structuredClone(EXAMPLE_DISTRIBUTE_POINT_OPTION_QUESTIONS);
    this.exampleDistributedPointRecipientModel = structuredClone(EXAMPLE_DISTRIBUTED_POINT_RECIPIENT_MODEL);
    this.exampleTeamContributionQuestionModel = structuredClone(EXAMPLE_TEAM_CONTRIBUTION_QUESTION_MODEL);
    this.exampleTeamContributionResponseOutput = structuredClone(EXAMPLE_TEAM_CONTRIBUTION_RESPONSE_OUTPUT);
    this.exampleContributionStatistics = structuredClone(EXAMPLE_CONTRIBUTION_STATISTICS);
    this.exampleTeamContributionQuestionTabModel = structuredClone(EXAMPLE_TEAM_CONTRIBUTION_QUESTION_TAB_MODEL);
    this.exampleTeamContributionQuestions = structuredClone(EXAMPLE_TEAM_CONTRIBUTION_QUESTIONS);
    this.exampleRubricQuestionModel = structuredClone(EXAMPLE_RUBRIC_QUESTION_MODEL);
    this.exampleRubricQuestionResponseOutput = structuredClone(EXAMPLE_RUBRIC_QUESTION_RESPONSE_OUTPUT);
    this.exampleRubricQuestionQuestionTabModel = structuredClone(EXAMPLE_RUBRIC_QUESTION_QUESTION_TAB_MODEL);
    this.exampleRubricQuestionQuestions = structuredClone(EXAMPLE_RUBRIC_QUESTION_QUESTIONS);
    this.exampleRankRecipientQuestionModel = structuredClone(EXAMPLE_RANK_RECIPIENT_QUESTION_MODEL);
    this.exampleRankRecipientResponseOutput = structuredClone(EXAMPLE_RANK_RECIPIENT_RESPONSE_OUTPUT);
    this.exampleRankRecipientQuestionTabModel = structuredClone(EXAMPLE_RANK_RECIPIENT_QUESTION_TAB_MODEL);
    this.exampleRankRecipientQuestions = structuredClone(EXAMPLE_RANK_RECIPIENT_QUESTIONS);
    this.exampleRankOptionQuestionModel = structuredClone(EXAMPLE_RANK_OPTION_QUESTION_MODEL);
    this.exampleMCQQuestionWithoutWeightsModel = structuredClone(EXAMPLE_MCQ_QUESTION_WITHOUT_WEIGHTS_MODEL);
    this.exampleMCQQuestionWithWeightsModel = structuredClone(EXAMPLE_MCQ_QUESTION_WITH_WEIGHTS_MODEL);
    this.exampleResponderRubricSubmissionFormModel = structuredClone(EXAMPLE_RESPONDER_RUBRIC_SUBMISSION_FORM_MODEL);
  }

  getQuestionsOrder(): string[] {
    return this.questionsOrder;
  }

  /**
   * Opens modal for contribution info.
   */
  openContribInfoModal(modal: TemplateRef<void>): void {
    this.simpleModalService.openInformationModal('Team contribution calculation', SimpleModalType.NEUTRAL, modal);
  }

  /**
   * Opens modal for rank info.
   */
  openRankInfoModal(modal: TemplateRef<void>): void {
    this.simpleModalService.openInformationModal('Rank calculation', SimpleModalType.NEUTRAL, modal);
  }
}
