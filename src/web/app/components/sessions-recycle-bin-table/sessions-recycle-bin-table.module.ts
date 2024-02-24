import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';

import { RecycleBinTableFormatDatePipe } from './recycle-bin-table-format-date.pipe';
import {
  SessionsRecycleBinTableComponent,
} from './sessions-recycle-bin-table.component';
import { AjaxLoadingModule } from '../ajax-loading/ajax-loading.module';
import { PanelChevronModule } from '../panel-chevron/panel-chevron.module';
import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';

/**
 * Module for deleted sessions table.
 */
@NgModule({
  declarations: [SessionsRecycleBinTableComponent, RecycleBinTableFormatDatePipe],
  imports: [
    AjaxLoadingModule,
    CommonModule,
    NgbTooltipModule,
    TeammatesCommonModule,
    PanelChevronModule,
  ],
  exports: [SessionsRecycleBinTableComponent],
})
export class SessionsRecycleBinTableModule { }
