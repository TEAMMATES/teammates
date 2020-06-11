import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { SortableTableModule } from '../../sortable-table/sortable-table.module';
import { ConstsumOptionsQuestionStatisticsComponent } from './constsum-options-question-statistics.component';
import { ConstsumRecipientsQuestionStatisticsComponent } from './constsum-recipients-question-statistics.component';
import { ContributionQuestionStatisticsComponent } from './contribution-question-statistics.component';
import { ContributionRatingsListComponent } from './contribution-ratings-list.component';
import { ContributionComponent } from './contribution.component';
import { McqQuestionStatisticsComponent } from './mcq-question-statistics.component';
import { MsqQuestionStatisticsComponent } from './msq-question-statistics.component';
import { NumScaleQuestionStatisticsComponent } from './num-scale-question-statistics.component';
import { RankOptionsQuestionStatisticsComponent } from './rank-options-question-statistics.component';
import { RankRecipientsQuestionStatisticsComponent } from './rank-recipients-question-statistics.component';
import { RubricQuestionStatisticsComponent } from './rubric-question-statistics.component';
import { TextQuestionStatisticsComponent } from './text-question-statistics.component';

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
    ContributionComponent,
    ContributionRatingsListComponent,
  ],
  exports: [
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
  imports: [
    CommonModule,
    FormsModule,
    NgbModule,
    SortableTableModule,
  ],
})
export class QuestionStatisticsModule { }
