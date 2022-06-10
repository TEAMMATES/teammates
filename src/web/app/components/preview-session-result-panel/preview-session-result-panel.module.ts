import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { TeammatesRouterModule } from '../teammates-router/teammates-router.module';
import { PreviewSessionResultPanelComponent } from './preview-session-result-panel.component';

/**
 * Module for panel used to select respondent and preview session results as the person.
 */
@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    NgbTooltipModule,
    TeammatesRouterModule,
  ],
  declarations: [
    PreviewSessionResultPanelComponent,
  ],
  exports: [
    PreviewSessionResultPanelComponent,
  ],
})
export class PreviewSessionResultPanelModule { }
