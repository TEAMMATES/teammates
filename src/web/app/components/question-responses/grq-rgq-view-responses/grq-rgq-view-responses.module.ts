import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { NgbCollapseModule } from '@ng-bootstrap/ng-bootstrap';
import { GrqRgqViewResponsesComponent } from './grq-rgq-view-responses.component';
import {
  ResponseModerationButtonModule,
} from '../../../pages-instructor/instructor-session-result-page/response-moderation-button/response-moderation-button.module';
import { PanelChevronModule } from '../../panel-chevron/panel-chevron.module';
import { TeammatesCommonModule } from '../../teammates-common/teammates-common.module';
import { GroupedResponsesModule } from '../grouped-responses/grouped-responses.module';

/**
 * Module for component to display list of responses in GRQ/RGQ view.
 */
@NgModule({
  declarations: [GrqRgqViewResponsesComponent],
  exports: [GrqRgqViewResponsesComponent],
  imports: [
    CommonModule,
    GroupedResponsesModule,
    ResponseModerationButtonModule,
    TeammatesCommonModule,
    NgbCollapseModule,
    PanelChevronModule,
  ],
})
export class GrqRgqViewResponsesModule { }
