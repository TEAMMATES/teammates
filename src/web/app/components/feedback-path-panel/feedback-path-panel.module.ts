import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';

import { FeedbackPathPanelComponent } from './feedback-path-panel.component';
import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';

/**
 * Feedback path panel module.
 */
@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    NgbDropdownModule,
    TeammatesCommonModule,
  ],
  declarations: [
    FeedbackPathPanelComponent,
  ],
  exports: [
    FeedbackPathPanelComponent,
  ],
})
export class FeedbackPathPanelModule { }
