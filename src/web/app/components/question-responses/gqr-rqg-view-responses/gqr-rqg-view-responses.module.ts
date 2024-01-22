import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { NgbCollapseModule } from '@ng-bootstrap/ng-bootstrap';
import { GqrRqgViewResponsesComponent } from './gqr-rqg-view-responses.component';
import {
  ResponseModerationButtonModule,
} from '../../../pages-instructor/instructor-session-result-page/response-moderation-button/response-moderation-button.module';

import { PanelChevronModule } from '../../panel-chevron/panel-chevron.module';
import { QuestionTextWithInfoModule } from '../../question-text-with-info/question-text-with-info.module';
import { TeammatesCommonModule } from '../../teammates-common/teammates-common.module';
import { PerQuestionViewResponsesModule } from '../per-question-view-responses/per-question-view-responses.module';
import { SingleStatisticsModule } from '../single-statistics/single-statistics.module';

/**
 * Module for component to display list of responses in GQR/RQG view.
 */
@NgModule({
  declarations: [GqrRqgViewResponsesComponent],
  exports: [GqrRqgViewResponsesComponent],
  imports: [
    CommonModule,
    QuestionTextWithInfoModule,
    PerQuestionViewResponsesModule,
    ResponseModerationButtonModule,
    SingleStatisticsModule,
    TeammatesCommonModule,
    NgbCollapseModule,
    PanelChevronModule,
  ],
})
export class GqrRqgViewResponsesModule { }
