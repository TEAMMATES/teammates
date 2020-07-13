import { DOCUMENT } from '@angular/common';
import { Component, EventEmitter, Inject, OnInit, Output } from '@angular/core';
import { PageScrollService } from 'ngx-page-scroll-core';
import { SimpleModalService } from '../../../../services/simple-modal.service';
import {
  ContributionStatistics,
  FeedbackConstantSumQuestionDetails,
  FeedbackNumericalScaleResponseDetails,
  FeedbackSession,
  ResponseOutput,
} from '../../../../types/api-output';
import { CommentTableModel } from '../../../components/comment-box/comment-table/comment-table.component';
import {
  QuestionEditFormMode,
  QuestionEditFormModel,
} from '../../../components/question-edit-form/question-edit-form-model';
import { QuestionSubmissionFormModel } from '../../../components/question-submission-form/question-submission-form-model';
import { Response } from '../../../components/question-types/question-statistics/question-statistics';
import { SimpleModalType } from '../../../components/simple-modal/simple-modal-type';
import { collapseAnim } from '../../../components/teammates-common/collapse-anim';
import { QuestionTabModel } from '../../../pages-instructor/instructor-session-result-page/instructor-session-result-page.component';
import { InstructorSessionResultSectionType } from '../../../pages-instructor/instructor-session-result-page/instructor-session-result-section-type.enum';
import { InstructorHelpSectionComponent } from '../instructor-help-section.component';
import { SessionsSectionQuestions } from '../instructor-help-sessions-section/sessions-section-questions';
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

/**
 * Questions Section of the Instructor Help Page.
 */
@Component({
  selector: 'tm-instructor-help-questions-section',
  templateUrl: './instructor-help-questions-section.component.html',
  styleUrls: ['./instructor-help-questions-section.component.scss'],
  animations: [collapseAnim],
})
export class InstructorHelpQuestionsSectionComponent extends InstructorHelpSectionComponent implements OnInit {

  // enum
  QuestionsSectionQuestions: typeof QuestionsSectionQuestions = QuestionsSectionQuestions;
  SessionsSectionQuestions: typeof SessionsSectionQuestions = SessionsSectionQuestions;
  InstructorSessionResultSectionType: typeof InstructorSessionResultSectionType = InstructorSessionResultSectionType;
  QuestionEditFormMode: typeof QuestionEditFormMode = QuestionEditFormMode;

  readonly exampleEssayQuestionModel: QuestionEditFormModel = EXAMPLE_ESSAY_QUESTION_MODEL;
  readonly exampleNumericalScaleQuestionModel: QuestionEditFormModel = EXAMPLE_NUMERICAL_SCALE_QUESTION_MODEL;
  readonly exampleNumericalScaleResponses: Response<FeedbackNumericalScaleResponseDetails>[]
    = EXAMPLE_NUMERICAL_SCALE_RESPONSES;
  readonly exampleNumericalScaleResponseOutput: ResponseOutput[] = EXAMPLE_NUMERICAL_SCALE_RESPONSE_OUTPUT;
  readonly exampleNumericalScaleQuestionTabModel: QuestionTabModel = EXAMPLE_NUMERICAL_SCALE_QUESTION_TAB_MODEL;
  readonly exampleNumericalScaleQuestions: Record<string, QuestionTabModel> = EXAMPLE_NUMERICAL_SCALE_QUESTIONS;
  readonly exampleInstructorCommentTableModel: Record<string, CommentTableModel>
    = EXAMPLE_INSTRUCTOR_COMMENT_TABLE_MODEL;
  readonly exampleFeedbackSession: FeedbackSession = EXAMPLE_FEEDBACK_SESSION;
  readonly exampleDistributePointOptionQuestionDetail: FeedbackConstantSumQuestionDetails
    = EXAMPLE_DISTRIBUTE_POINT_OPTION_QUESTION_DETAIL;
  readonly exampleDistributedPointOptionModel: QuestionEditFormModel = EXAMPLE_DISTRIBUTED_POINT_OPTION_MODEL;
  readonly exampleDistributePointOptionResponseOutput: ResponseOutput[]
    = EXAMPLE_DISTRIBUTE_POINT_OPTION_RESPONSE_OUTPUT;
  readonly exampleDistributePointOptionQuestionTabModel: QuestionTabModel
    = EXAMPLE_DISTRIBUTE_POINT_OPTION_QUESTION_TAB_MODEL;
  readonly exampleDistributePointOptionQuestions: Record<string, QuestionTabModel>
    = EXAMPLE_DISTRIBUTE_POINT_OPTION_QUESTIONS;
  readonly exampleDistributedPointRecipientModel: QuestionEditFormModel = EXAMPLE_DISTRIBUTED_POINT_RECIPIENT_MODEL;
  readonly exampleTeamContributionQuestionModel: QuestionEditFormModel = EXAMPLE_TEAM_CONTRIBUTION_QUESTION_MODEL;
  readonly exampleTeamContributionResponseOutput: ResponseOutput[] = EXAMPLE_TEAM_CONTRIBUTION_RESPONSE_OUTPUT;
  readonly exampleContributionStatistics: ContributionStatistics = EXAMPLE_CONTRIBUTION_STATISTICS;
  readonly exampleTeamContributionQuestionTabModel: QuestionTabModel = EXAMPLE_TEAM_CONTRIBUTION_QUESTION_TAB_MODEL;
  readonly exampleTeamContributionQuestions: Record<string, QuestionTabModel> = EXAMPLE_TEAM_CONTRIBUTION_QUESTIONS;
  readonly exampleRubricQuestionModel: QuestionEditFormModel = EXAMPLE_RUBRIC_QUESTION_MODEL;
  readonly exampleRubricQuestionResponseOutput: ResponseOutput[] = EXAMPLE_RUBRIC_QUESTION_RESPONSE_OUTPUT;
  readonly exampleRubricQuestionQuestionTabModel: QuestionTabModel = EXAMPLE_RUBRIC_QUESTION_QUESTION_TAB_MODEL;
  readonly exampleRubricQuestionQuestions: Record<string, QuestionTabModel> = EXAMPLE_RUBRIC_QUESTION_QUESTIONS;
  readonly exampleRankRecipientQuestionModel: QuestionEditFormModel = EXAMPLE_RANK_RECIPIENT_QUESTION_MODEL;
  readonly exampleRankRecipientResponseOutput: ResponseOutput[] = EXAMPLE_RANK_RECIPIENT_RESPONSE_OUTPUT;
  readonly exampleRankRecipientQuestionTabModel: QuestionTabModel = EXAMPLE_RANK_RECIPIENT_QUESTION_TAB_MODEL;
  readonly exampleRankRecipientQuestions: Record<string, QuestionTabModel> = EXAMPLE_RANK_RECIPIENT_QUESTIONS;
  readonly exampleRankOptionQuestionModel: QuestionEditFormModel = EXAMPLE_RANK_OPTION_QUESTION_MODEL;
  readonly exampleMCQQuestionWithoutWeightsModel: QuestionEditFormModel = EXAMPLE_MCQ_QUESTION_WITHOUT_WEIGHTS_MODEL;
  readonly exampleMCQQuestionWithWeightsModel: QuestionEditFormModel = EXAMPLE_MCQ_QUESTION_WITH_WEIGHTS_MODEL;
  readonly exampleResponderRubricSubmissionFormModel: QuestionSubmissionFormModel
    = EXAMPLE_RESPONDER_RUBRIC_SUBMISSION_FORM_MODEL;

  questionsToCollapsed: Record<string, boolean> = {
    [QuestionsSectionQuestions.ESSAY]: false,
    [QuestionsSectionQuestions.SINGLE_ANSWER_MCQ]: false,
    [QuestionsSectionQuestions.MULTIPLE_ANSWER_MCQ]: false,
    [QuestionsSectionQuestions.NUMERICAL_SCALE]: false,
    [QuestionsSectionQuestions.POINTS_OPTIONS]: false,
    [QuestionsSectionQuestions.POINTS_RECIPIENTS]: false,
    [QuestionsSectionQuestions.CONTRIBUTION]: false,
    [QuestionsSectionQuestions.RUBRIC]: false,
    [QuestionsSectionQuestions.RANK_OPTIONS]: false,
    [QuestionsSectionQuestions.RANK_RECIPIENTS]: false,
  };

  @Output() collapsePeerEvalTips: EventEmitter<any> = new EventEmitter();

  constructor(private simpleModalService: SimpleModalService,
              private pageScrollService: PageScrollService,
              @Inject(DOCUMENT) private document: any) {
    super();
  }

  /**
   * Opens modal for contribution info.
   */
  openContribInfoModal(): void {
    const modalContent: string = `<div class="modal-body">
      <ul>
        <li>
          <strong>General Mechanism</strong>
          <div>
            Students enter contribution estimates for themselves and their team members using the contribution scale
            (see <a href="https://github.com/TEAMMATES/teammates/blob/master/docs/glossary.md#product-related"> glossary</a>).
            <br>
            <br>Based on those values, a student's answer to the following two questions are deduced:
            <br>(a) In your opinion, what portion of the project did you do?
            <br>(b) In your opinion, if your teammates are doing the project by themselves without you, how do they compare against each other in terms of contribution?
            <br>
            <br>In the calculation, we do not allow (a) to affect (b). We use (b) to calculate the average perceived contribution for each student.
            <br>
          </div>
          <br>
        </li>
        <li>
          <strong>Calculation Scheme</strong>
          <div>
            <ol type="1">
              <li>
                <strong>Calculate <code>normalizedClaimed</code> values</strong>
                <div>
                  This is required because the total of points entered might not sum up to <code>100 * (team size)</code>.
                  <br><br>
                  <code>(normalized value) = (original value) * (normalization factor)</code>
                  <br><br>
                  <div class="bs-example">
                    entered values: <code>90</code> [self], <code>110</code>, <code>130</code>, <code>N/A</code> (total = <code>330</code>)
                    <br>normalization factor: <code>(100 * 3) / (90 + 110 + 130) = 300 / 330</code>
                    <br>normalized: <code>82</code>, <code>100</code>, <code>118</code>, <code>N/A</code>
                    <br>normalized total = <code>300</code> (i.e. <code>100 * number of inputs</code>)
                  </div>
                  This answers the question (a) above. The student thinks he did '<code>Equal share - 18%</code>' (as indicated by <code>82</code>).
                  <br>
                  <br>
                </div>
              </li>
              <li>
                <strong>Calculate <code>peerContributionRatio</code> values by removing self-rating bias</strong>
                <div>
                  Here, we ignore the self rating and normalize remaining values.
                  <br><br>
                  <div class="bs-example">
                    normalized input (from above): <code>82</code>,<code>100</code>, <code>118</code>, <code>N/A</code>
                    <br>Calculating unbiased values:
                    <br>&nbsp;<code>82</code> → ignored.
                    <br>&nbsp;<code>100</code> → <code>100 * 200 / (100 + 118) = 92</code>
                    <br>&nbsp;<code>118</code> → <code>118 * 200 / (100 + 118) = 108</code>
                    <br>Unbiased values: [self (ignored)], <code>92</code>, <code>108</code>, <code>N/A</code>
                    <br>Unbiased values total = <code>200 (100 * number of ratings)</code>
                  </div>
                  This answers the question (b) above. In the example above, the student thinks his teammates contribution ratio is <code>92:108</code> and is unsure of the third teammate.
                  <br><br>
                </div>
              </li>
              <li>
                <strong>Calculate <code>averagePerceivedContributionRatio</code></strong>
                <div>
                  Next, we calculate <code>averagePerceivedContributionRatio</code> among team members, independent of (a). This consists of these steps:
                  <br><br>
                  <ol type="i">
                    <li>
                      <strong>Calculate <code>averagePerceived</code>:</strong>
                      <div>
                        For each student, take the average of <code>peerContributionRatio</code> that others have given him.
                      </div>
                      <br>
                    </li>
                    <li>
                      <strong>Calculate <code>normalizedAveragePerceived</code>:</strong>
                      <div>
                        Normalize the averages, similar to how input was normalized.
                        <br><br>
                        <code>
                          normalizedAveragePerceived = averagePerceived * normalizationFactor
                          <br>normalizationFactor = 100 * (number of students with averagePerceived values)/(sum of averagePerceived)
                        </code>
                        <br>
                        <br>This is the relative work distribution among team members based on unbiased opinions of team members.
                        <br>
                        <br>
                      </div>
                    </li>
                    <li>
                      <strong>Calculate <code>normalizedPeerContributionRatio</code></strong>
                      <div>
                        Since we normalized the averages (in previous step), we also normalize the value that were averaged in the first place. This is such that average and averaged tallies with each other.
                        <br>
                        <code>normalizedPeerContributionRatio = peerContributionRatio * normalizationFactor</code>
                        <br>
                      </div>
                    </li>
                  </ol>
                </div>
              </li>
              <li>
                <strong>Denormalize <code>normalizedAveragePerceived</code></strong>
                <div>
                  For each student, denormalize <code>normalizedAveragePerceived</code>. We scale back to match the total of original
                  input by student. That way, student can compare his input (i.e., his opinion of the team’s work distribution)
                  with the team’s opinion. In the example used above, we should use 330/300 as the denormalizing factor for that student.
                  The result could be something like this:
                  <br>
                  <br>
                  <div class="bs-example">
                    student’s opinion: <code>90</code> [self], <code>110</code>, <code>130</code>, <code>N/A</code> (total = <code>330</code>)
                    <br>team’s opinion : <code>95</code>, <code>105</code>, <code>125</code>, <code>115</code> (total = <code>440</code>)
                  </div>
                  Value transformation steps: input (i.e. claimed) → <code>normalizedClaimed</code> →
                  <code>peerContributionRatio</code> → <code>averagePerceived</code> →
                  <code>normalizedAveragePerceived</code> → <code>denormalizedAveragePerceived</code> →
                  <code>normalizedPeerContributionRatio</code>
                  <br>
                  <br>
                </div>
              </li>
            </ol>
          </div>
          Student view:
          <ul>
            <li>
              for claimed contribution, show: same as what the student entered initially (otherwise, the student will be confused as to how the value got changed)
            </li>
            <li>
              for perceived contribution, show: <code>denormalizedAveragePerceived</code>
            </li>
          </ul>
          <br>Instructor view:
          <ul>
            <li>
              for claimed contribution, show: <code>normalizedClaimed</code>
            </li>
            <li>
              for perceived contribution, show: <code>normalizedAveragePerceived</code>
            </li>
          </ul>
          <br>Note:
          <ul>
            <li>
              Scenario 1: If students give 0 points to each other, then everyone should receive Equal Share and difference should be 0.
            </li>
            <li>
              Scenario 2: If students are not sure or do not submit the evaluation, then Perceived/Claimed for Instructor should be shown as N/A instead of Equal Share. In this case, difference too should be shown as N/A.
            </li>
          </ul>
        </li>
      </ul>
    </div>`;
    this.simpleModalService.openInformationModal(
        'Team contribution calculation', SimpleModalType.NEUTRAL, modalContent);
  }

  /**
   * Opens modal for rank info.
   */
  openRankInfoModal(): void {
    const modalContent: string = `<ul>
      <li>
        <b>Ranks Received</b> is a list of the actual ranks each recipient received. TEAMMATES processes the original responses, handling ties and unused ranks.
        For example, if giver A's original response is <code>(1, 3, 3, 5)</code> and Rank 5 is given to recipient B, after the processing, giver A's response will become <code>(1, 2, 2, 4)</code> and recipient B will have a Rank 4 in his/her <b>Ranks Received</b>, instead of the Rank 5 in the original response by giver A.
      </li>
      <li>
        The <b>Overall Rank</b> ranks the average rank each recipient receives.
        For example, if recipient A received the ranks <code>(1, 2)</code> and recipient B received the ranks <code>(2, 4, 6)</code>, then recipient A and recipient B's average ranks are 1.5 and 4 respectively. By ranking these two averages, recipient A and B will get an <b>Overall Rank</b> of 1 and 2 respectively.
      </li>
    </ul>`;
    this.simpleModalService.openInformationModal(
        'Rank calculation', SimpleModalType.NEUTRAL, modalContent);
  }

  ngOnInit(): void {
  }

  /**
   * Scrolls to an HTML element with a given target id.
   */
  jumpTo(target: string): boolean {
    this.pageScrollService.scroll({
      document: this.document,
      scrollTarget: `#${target}`,
      scrollOffset: 70,
    });
    return false;
  }

  expand(questionId: string): void {
    this.questionsToCollapsed[questionId] = true;
  }
}
