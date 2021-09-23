import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { PreviewSessionPanelComponent } from './preview-session-panel.component';

/**
 * X module.
 */
@NgModule({
  imports: [
    CommonModule,
  ],
  declarations: [
    PreviewSessionPanelComponent,
  ],
  exports: [
    PreviewSessionPanelComponent,
  ],
})
export class PreviewSessionPanelModule { }
