import { DOCUMENT } from '@angular/common';
import { Component, EventEmitter, Inject, OnInit, Output } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { PageScrollService } from 'ngx-page-scroll-core';
import { InstructorHelpSectionComponent } from '../instructor-help-section.component';
import { QuestionEditFormModel } from '../../../components/question-edit-form/question-edit-form-model';
import { DEFAULT_NUMSCALE_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { FeedbackQuestionType, FeedbackParticipantType, NumberOfEntitiesToGiveFeedbackToSetting } from '../../../../types/api-output';

/**
 * Questions Section of the Instructor Help Page.
 */
@Component({
  selector: 'tm-instructor-help-questions-section',
  templateUrl: './instructor-help-questions-section.component.html',
  styleUrls: ['./instructor-help-questions-section.component.scss'],
})
export class InstructorHelpQuestionsSectionComponent extends InstructorHelpSectionComponent implements OnInit {

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

  readonly exampleNumericalScaleEditFormModel: QuestionEditFormModel = {
      feedbackQuestionId: "CS3281",

      questionNumber: 1,
      questionBrief: "This is a brief of the question",
      questionDescription: "This is the description of the question",

      isQuestionHasResponses: false,

      questionType: FeedbackQuestionType.NUMSCALE,
      questionDetails: DEFAULT_NUMSCALE_QUESTION_DETAILS(),

      giverType: FeedbackParticipantType.STUDENTS,
      recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS,

      numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.CUSTOM,
      customNumberOfEntitiesToGiveFeedbackTo: 1,

      showResponsesTo: [],
      showGiverNameTo: [],
      showRecipientNameTo: [],

      isUsingOtherFeedbackPath: true,
      commonVisibilitySettingName: "",
      isUsingOtherVisibilitySetting: false,

      isEditable: false,
      isSaving: false,
 }

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
