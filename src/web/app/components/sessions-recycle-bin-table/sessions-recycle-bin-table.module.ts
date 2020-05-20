import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';
import { RecycleBinTableFormatDatePipe } from './recycle-bin-table-format-date.pipe';
import {
  SessionsRecycleBinTableComponent,
} from './sessions-recycle-bin-table.component';

/**
 * Module for deleted sessions table.
 */
@NgModule({
  declarations: [SessionsRecycleBinTableComponent, RecycleBinTableFormatDatePipe],
  imports: [
    CommonModule,
    NgbModule,
    TeammatesCommonModule,
  ],
  exports: [SessionsRecycleBinTableComponent],
})
export class SessionsRecycleBinTableModule { }
