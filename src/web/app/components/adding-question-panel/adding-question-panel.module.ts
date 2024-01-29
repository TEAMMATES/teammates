import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';

import { AddingQuestionPanelComponent } from './adding-question-panel.component';
import { AjaxLoadingModule } from '../ajax-loading/ajax-loading.module';
import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';
import { TeammatesRouterModule } from '../teammates-router/teammates-router.module';

/**
 * Adding question panel module.
 */
@NgModule({
  imports: [
    AjaxLoadingModule,
    CommonModule,
    NgbDropdownModule,
    TeammatesCommonModule,
    TeammatesRouterModule,
  ],
  declarations: [
    AddingQuestionPanelComponent,
  ],
  exports: [
    AddingQuestionPanelComponent,
  ],
})
export class AddingQuestionPanelModule { }
