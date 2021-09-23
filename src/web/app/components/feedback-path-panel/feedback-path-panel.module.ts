import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { FeedbackPathPanelComponent } from './feedback-path-panel.component';

/**
 * Feedback path panel module.
 */
@NgModule({
  imports: [
    CommonModule,
  ],
  declarations: [
    FeedbackPathPanelComponent,
  ],
  exports: [
    FeedbackPathPanelComponent,
  ],
})
export class FeedbackPathPanelModule { }
