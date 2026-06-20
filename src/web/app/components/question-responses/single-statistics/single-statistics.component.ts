import { Component, Input } from '@angular/core';
import {
  FeedbackQuestionResultsStatistics,
} from '../../../../types/api-output';
import { QuestionStatisticsTypeChecker } from '../../../../types/question-statistics-impl/question-statistics-caster';
import { ConstsumOptionsQuestionStatisticsComponent } from '../../question-types/question-statistics/constsum-options-question-statistics.component';
import { ConstsumRecipientsQuestionStatisticsComponent } from '../../question-types/question-statistics/constsum-recipients-question-statistics.component';
import { ContributionCourseWideQuestionStatisticsComponent } from '../../question-types/question-statistics/contribution-question-statistics/contribution-course-wide-question-statistics.component';
import { ContributionRecipientQuestionStatisticsComponent } from '../../question-types/question-statistics/contribution-question-statistics/contribution-recipient-question-statistics.component';
import { McqMsqQuestionStatisticsComponent } from '../../question-types/question-statistics/mcq-msq-question-statistics.component';
import { NumScaleQuestionStatisticsComponent } from '../../question-types/question-statistics/num-scale-question-statistics.component';
import { RankOptionsQuestionStatisticsComponent } from '../../question-types/question-statistics/rank-options-question-statistics.component';
import { RankRecipientsQuestionStatisticsComponent } from '../../question-types/question-statistics/rank-recipients-question-statistics.component';
import { RubricQuestionStatisticsComponent } from '../../question-types/question-statistics/rubric-question-statistics.component';

/**
 * The component that will map a generic response statistics to its specialized view component.
 */
@Component({
  selector: 'tm-single-statistics',
  templateUrl: './single-statistics.component.html',
  imports: [
    ContributionCourseWideQuestionStatisticsComponent,
    ContributionRecipientQuestionStatisticsComponent,
    ConstsumOptionsQuestionStatisticsComponent,
    ConstsumRecipientsQuestionStatisticsComponent,
    NumScaleQuestionStatisticsComponent,
    RubricQuestionStatisticsComponent,
    RankOptionsQuestionStatisticsComponent,
    RankRecipientsQuestionStatisticsComponent,
    McqMsqQuestionStatisticsComponent,
  ],
})
export class SingleStatisticsComponent {
  readonly QuestionStatisticsTypeChecker: typeof QuestionStatisticsTypeChecker = QuestionStatisticsTypeChecker;

  @Input() statistics?: FeedbackQuestionResultsStatistics;
}
