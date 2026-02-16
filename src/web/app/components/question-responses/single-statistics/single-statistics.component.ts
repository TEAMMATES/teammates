
import { Component, Input, OnChanges, OnInit } from '@angular/core';
import { FeedbackResponsesService } from '../../../../services/feedback-responses.service';
import {
  FeedbackParticipantType,
  FeedbackQuestionDetails,
  FeedbackQuestionType,
  ResponseOutput,
} from '../../../../types/api-output';
import { QuestionDetailsTypeChecker } from '../../../../types/question-details-impl/question-details-caster';
import { ResponseOutputCaster } from '../../../../types/response-details-impl/response-details-caster';
import {
  InstructorSessionResultSectionType,
} from '../../../pages-instructor/instructor-session-result-page/instructor-session-result-section-type.enum';
import { ConstsumOptionsQuestionStatisticsComponent } from '../../question-types/question-statistics/constsum-options-question-statistics.component';
import { ConstsumRecipientsQuestionStatisticsComponent } from '../../question-types/question-statistics/constsum-recipients-question-statistics.component';
import { ContributionQuestionStatisticsComponent } from '../../question-types/question-statistics/contribution-question-statistics/contribution-question-statistics.component';
import { McqQuestionStatisticsComponent } from '../../question-types/question-statistics/mcq-question-statistics.component';
import { MsqQuestionStatisticsComponent } from '../../question-types/question-statistics/msq-question-statistics.component';
import { NumScaleQuestionStatisticsComponent } from '../../question-types/question-statistics/num-scale-question-statistics.component';
import { RankOptionsQuestionStatisticsComponent } from '../../question-types/question-statistics/rank-options-question-statistics.component';
import { RankRecipientsQuestionStatisticsComponent } from '../../question-types/question-statistics/rank-recipients-question-statistics.component';
import { RubricQuestionStatisticsComponent } from '../../question-types/question-statistics/rubric-question-statistics.component';
import { TextQuestionStatisticsComponent } from '../../question-types/question-statistics/text-question-statistics.component';

/**
 * The component that will map a generic response statistics to its specialized view component.
 */
@Component({
  selector: 'tm-single-statistics',
  templateUrl: './single-statistics.component.html',
  styleUrls: ['./single-statistics.component.scss'],
  imports: [
    ContributionQuestionStatisticsComponent,
    TextQuestionStatisticsComponent,
    ConstsumOptionsQuestionStatisticsComponent,
    ConstsumRecipientsQuestionStatisticsComponent,
    NumScaleQuestionStatisticsComponent,
    RubricQuestionStatisticsComponent,
    RankOptionsQuestionStatisticsComponent,
    RankRecipientsQuestionStatisticsComponent,
    MsqQuestionStatisticsComponent,
    McqQuestionStatisticsComponent
],
})
export class SingleStatisticsComponent implements OnInit, OnChanges {
  readonly QuestionDetailsTypeChecker = QuestionDetailsTypeChecker;
  readonly ResponseOutputCaster = ResponseOutputCaster;

  @Input() responses: ResponseOutput[] = [];
  @Input() question: FeedbackQuestionDetails = {
    questionType: FeedbackQuestionType.TEXT,
    questionText: '',
  };
  @Input() recipientType: FeedbackParticipantType = FeedbackParticipantType.NONE;
  @Input() isStudent: boolean = false;
  @Input() statistics: string = '';
  @Input() displayContributionStats: boolean = true;
  @Input() section: string = '';
  @Input() sectionType: InstructorSessionResultSectionType = InstructorSessionResultSectionType.EITHER;

  responsesToUse: ResponseOutput[] = [];

  constructor(private feedbackResponsesService: FeedbackResponsesService) { }

  ngOnInit(): void {
    this.filterResponses();
  }

  ngOnChanges(): void {
    this.filterResponses();
  }

  private filterResponses(): void {
    this.responsesToUse = this.responses.filter((response: ResponseOutput) => {
      if (response.isMissingResponse && this.question.questionType !== FeedbackQuestionType.CONTRIB) {
        // Missing response is meaningless for statistics
        // For contribution question statistics, need to keep the missing response
        // to build the response summary
        return false;
      }

      if (this.isUsingResponsesToSelf() && response.recipient !== 'You') {
        return false;
      }

      return this.feedbackResponsesService
          .isFeedbackResponsesDisplayedOnSection(response, this.section, this.sectionType);
    });
  }

  private isUsingResponsesToSelf(): boolean {
    return this.isStudent
      && (this.question.questionType === FeedbackQuestionType.NUMSCALE
      || this.question.questionType === FeedbackQuestionType.CONSTSUM_RECIPIENTS
      || this.question.questionType === FeedbackQuestionType.CONSTSUM_OPTIONS);
  }
}
