import { DOCUMENT } from '@angular/common';
import { Component, EventEmitter, Inject, OnInit, Output } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { PageScrollService } from 'ngx-page-scroll-core';
import {
  QuestionEditFormMode,
} from '../../../components/question-edit-form/question-edit-form-model';

import {
    InstructorSessionResultSectionType,
} from '../../../pages-instructor/instructor-session-result-page/instructor-session-result-section-type.enum';
import { InstructorHelpSectionComponent } from '../instructor-help-section.component';
import {
  ExampleEssayQuestionModel,
  ExampleNumericalScaleQuestionModel,
  ExampleNumericalScaleResponses,
  ExampleNumericalScaleResponseOutput,
  ExampleNumericalScaleQuestionTabModel,
  ExampleNumericalScaleQuestions,
  ExampleInstructorCommentTableModel,
  ExampleFeedbackSession,
  ExampleDistributePointOptionQuestionDetail,
  ExampleDistributedPointOptionModel,
  ExampleDistributePointOptionResponseOutput,
  ExampleDistributePointOptionQuestionTabModel,
  ExampleDistributePointOptionQuestions,
  ExampleDistributedPointRecipientModel,
  ExampleTeamContributionQuestionModel,
  ExampleTeamContributionResponseOutput,
  ExampleContributionStatistics,
  ExampleTeamContributionQuestionTabModel,
  ExampleTeamContributionQuestions,
  ExampleRubricQuestionModel,
  ExampleRubricQuestionResponseOutput,
  ExampleRubricQuestionQuestionTabModel,
  ExampleRubricQuestionQuestions,
  ExampleRankRecipientQuestionModel,
  ExampleRankRecipientResponseOutput,
  ExampleRankRecipientQuestionTabModel,
  ExampleRankRecipientQuestions,
  ExampleRankOptionQuestionModel,
  ExampleMCQQuestionWithoutWeightsModel,
  ExampleMCQQuestionWithWeightsModel,
  ExampleResponderRubricSubmissionFormModel,
} from './instructor-help-questions-data';
  
/**
 * Questions Section of the Instructor Help Page.
 */
@Component({
  selector: 'tm-instructor-help-questions-section',
  templateUrl: './instructor-help-questions-section.component.html',
  styleUrls: ['./instructor-help-questions-section.component.scss'],
})
export class InstructorHelpQuestionsSectionComponent extends InstructorHelpSectionComponent implements OnInit {

  // enum
  InstructorSessionResultSectionType: typeof InstructorSessionResultSectionType = InstructorSessionResultSectionType;
  QuestionEditFormMode: typeof QuestionEditFormMode = QuestionEditFormMode;

  readonly exampleEssayQuestionModel = ExampleEssayQuestionModel;
  readonly exampleNumericalScaleQuestionModel = ExampleNumericalScaleQuestionModel;
  readonly exampleNumericalScaleResponses = ExampleNumericalScaleResponses;
  readonly exampleNumericalScaleResponseOutput = ExampleNumericalScaleResponseOutput;
  readonly exampleNumericalScaleQuestionTabModel = ExampleNumericalScaleQuestionTabModel;
  readonly exampleNumericalScaleQuestions = ExampleNumericalScaleQuestions;
  readonly exampleInstructorCommentTableModel = ExampleInstructorCommentTableModel;
  readonly exampleFeedbackSession = ExampleFeedbackSession;
  readonly exampleDistributePointOptionQuestionDetail = ExampleDistributePointOptionQuestionDetail;
  readonly exampleDistributedPointOptionModel = ExampleDistributedPointOptionModel;
  readonly exampleDistributePointOptionResponseOutput = ExampleDistributePointOptionResponseOutput;
  readonly exampleDistributePointOptionQuestionTabModel = ExampleDistributePointOptionQuestionTabModel;
  readonly exampleDistributePointOptionQuestions = ExampleDistributePointOptionQuestions;
  readonly exampleDistributedPointRecipientModel = ExampleDistributedPointRecipientModel;
  readonly exampleTeamContributionQuestionModel = ExampleTeamContributionQuestionModel;
  readonly exampleTeamContributionResponseOutput = ExampleTeamContributionResponseOutput;
  readonly exampleContributionStatistics = ExampleContributionStatistics;
  readonly exampleTeamContributionQuestionTabModel = ExampleTeamContributionQuestionTabModel;
  readonly exampleTeamContributionQuestions = ExampleTeamContributionQuestions;
  readonly exampleRubricQuestionModel = ExampleRubricQuestionModel;
  readonly exampleRubricQuestionResponseOutput = ExampleRubricQuestionResponseOutput;
  readonly exampleRubricQuestionQuestionTabModel = ExampleRubricQuestionQuestionTabModel;
  readonly exampleRubricQuestionQuestions = ExampleRubricQuestionQuestions;
  readonly exampleRankRecipientQuestionModel = ExampleRankRecipientQuestionModel;
  readonly exampleRankRecipientResponseOutput = ExampleRankRecipientResponseOutput;
  readonly exampleRankRecipientQuestionTabModel = ExampleRankRecipientQuestionTabModel;
  readonly exampleRankRecipientQuestions = ExampleRankRecipientQuestions;
  readonly exampleRankOptionQuestionModel = ExampleRankOptionQuestionModel;
  readonly exampleMCQQuestionWithoutWeightsModel = ExampleMCQQuestionWithoutWeightsModel;
  readonly exampleMCQQuestionWithWeightsModel = ExampleMCQQuestionWithWeightsModel;
  readonly exampleResponderRubricSubmissionFormModel = ExampleResponderRubricSubmissionFormModel;

  isEssayQuestionsCollapsed: boolean = false;
  isMCQSingleAnsCollapsed: boolean = false;
  isMCQMultipleAnsCollapsed: boolean = false;
  isNumericalScaleCollapsed: boolean = false;
  isPointsOptionsCollapsed: boolean = false;
  isPointsRecipientsCollapsed: boolean = false;
  isContributionQsCollapsed: boolean = false;
  isRubricQsCollapsed: boolean = false;
  isRankOptionsCollapsed: boolean = false;
  isRankRecipientsCollapsed: boolean = false;
  @Output() collapsePeerEvalTips: EventEmitter<boolean> = new EventEmitter<boolean>();

  constructor(private modalService: NgbModal,
              private pageScrollService: PageScrollService,
              @Inject(DOCUMENT) private document: any) {
    super();
  }

  /**
   * Opens modal window.
   */
  openModal(modal: any): void {
    this.modalService.open(modal);
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
    if (target === 'tips-for-conducting-peer-eval') {
      this.collapsePeerEvalTips.emit(true);
    }
    return false;
  }
}
