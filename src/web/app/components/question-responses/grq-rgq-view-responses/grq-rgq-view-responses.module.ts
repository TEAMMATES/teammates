import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { NgbCollapseModule } from '@ng-bootstrap/ng-bootstrap';
// tslint:disable-next-line:max-line-length
import { ResponseModerationButtonModule } from '../../../pages-instructor/instructor-session-result-page/response-moderation-button/response-moderation-button.module';
import { PanelChevronModule } from '../../panel-chevron/panel-chevron.module';
import { TeammatesCommonModule } from '../../teammates-common/teammates-common.module';
import { GroupedResponsesModule } from '../grouped-responses/grouped-responses.module';
import { GrqRgqViewResponsesComponent } from './grq-rgq-view-responses.component';

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
