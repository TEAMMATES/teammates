import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { ConstsumOptionsQuestionStatisticsComponent } from './constsum-options-question-statistics.component';
import { ConstsumRecipientsQuestionStatisticsComponent } from './constsum-recipients-question-statistics.component';
import {
  ContributionQuestionStatisticsComponent,
} from './contribution-question-statistics/contribution-question-statistics.component';
import {
  ContributionQuestionStatisticsModule,
} from './contribution-question-statistics/contribution-question-statistics.module';
import { McqQuestionStatisticsComponent } from './mcq-question-statistics.component';
import { MsqQuestionStatisticsComponent } from './msq-question-statistics.component';
import { NumScaleQuestionStatisticsComponent } from './num-scale-question-statistics.component';
import { RankOptionsQuestionStatisticsComponent } from './rank-options-question-statistics.component';
import { RankRecipientsQuestionStatisticsComponent } from './rank-recipients-question-statistics.component';
import { RubricQuestionStatisticsComponent } from './rubric-question-statistics.component';
import { TextQuestionStatisticsComponent } from './text-question-statistics.component';
import { SortableTableModule } from '../../sortable-table/sortable-table.module';
import { TeammatesRouterModule } from '../../teammates-router/teammates-router.module';

/**
 * Module for all different types of question statistics.
 */
@NgModule({
  declarations: [
    ContributionQuestionStatisticsComponent,
    TextQuestionStatisticsComponent,
    McqQuestionStatisticsComponent,
    MsqQuestionStatisticsComponent,
    NumScaleQuestionStatisticsComponent,
    RubricQuestionStatisticsComponent,
    RankOptionsQuestionStatisticsComponent,
    RankRecipientsQuestionStatisticsComponent,
    ConstsumOptionsQuestionStatisticsComponent,
    ConstsumRecipientsQuestionStatisticsComponent,
  ],
  exports: [
    TextQuestionStatisticsComponent,
    McqQuestionStatisticsComponent,
    MsqQuestionStatisticsComponent,
    NumScaleQuestionStatisticsComponent,
    RubricQuestionStatisticsComponent,
    RankOptionsQuestionStatisticsComponent,
    RankRecipientsQuestionStatisticsComponent,
    ConstsumOptionsQuestionStatisticsComponent,
    ConstsumRecipientsQuestionStatisticsComponent,
    ContributionQuestionStatisticsComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    NgbTooltipModule,
    SortableTableModule,
    RouterModule,
    ContributionQuestionStatisticsModule,
    TeammatesRouterModule,
  ],
})
export class QuestionStatisticsModule { }
