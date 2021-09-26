import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';
import { FeedbackPathPanelComponent } from './feedback-path-panel.component';
import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';


/**
 * Feedback path panel module.
 */
@NgModule({
  imports: [
    CommonModule,
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
