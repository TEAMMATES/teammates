import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AddingQuestionPanelComponent } from './adding-question-panel.component';
import { AjaxLoadingModule } from '../../components/ajax-loading/ajax-loading.module';
import { NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';

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
