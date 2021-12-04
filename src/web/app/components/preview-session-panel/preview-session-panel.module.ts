import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { TeammatesRouterModule } from '../teammates-router/teammates-router.module';
import { PreviewSessionPanelComponent } from './preview-session-panel.component';

/**
 * X module.
 */
@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    NgbTooltipModule,
    TeammatesRouterModule,
  ],
  declarations: [
    PreviewSessionPanelComponent,
  ],
  exports: [
    PreviewSessionPanelComponent,
  ],
})
export class PreviewSessionPanelModule { }
