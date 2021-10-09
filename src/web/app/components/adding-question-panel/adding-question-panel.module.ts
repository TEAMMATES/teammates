import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';

import { AjaxLoadingModule } from '../../components/ajax-loading/ajax-loading.module';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';

import { AddingQuestionPanelComponent } from './adding-question-panel.component';

/**
 * Adding question panel module.
 */
@NgModule({
  imports: [
    AjaxLoadingModule,
    CommonModule,
    NgbDropdownModule,
    TeammatesCommonModule,
    RouterModule,
  ],
  declarations: [
    AddingQuestionPanelComponent,
  ],
  exports: [
    AddingQuestionPanelComponent,
  ],
})
export class AddingQuestionPanelModule { }
