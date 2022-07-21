import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AjaxLoadingModule } from '../ajax-loading/ajax-loading.module';
import { PanelChevronModule } from '../panel-chevron/panel-chevron.module';
import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';
import { ProcessAccountRequestPanelComponent } from './process-account-request-panel.component';

/**
 * Module for panel used to display an account request being processed.
 */
@NgModule({
  imports: [
    AjaxLoadingModule,
    PanelChevronModule,
    TeammatesCommonModule,
    FormsModule,
    CommonModule,
  ],
  declarations: [
    ProcessAccountRequestPanelComponent,
  ],
  exports: [
    ProcessAccountRequestPanelComponent,
  ],
})
export class ProcessAccountRequestPanelModule { }
