import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { AddingQuestionPanelComponent } from './adding-question-panel.component';

/**
 * Adding question panel module.
 */
@NgModule({
  imports: [
    CommonModule,
  ],
  declarations: [
    AddingQuestionPanelComponent,
  ],
  exports: [
    AddingQuestionPanelComponent,
  ],
})
export class AddingQuestionPanelModule { }
