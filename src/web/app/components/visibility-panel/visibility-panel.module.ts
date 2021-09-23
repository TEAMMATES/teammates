import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { VisibilityPanelComponent } from './visibility-panel.component';

/**
 * Visibility panel module.
 */
@NgModule({
  imports: [
    CommonModule,
  ],
  declarations: [
    VisibilityPanelComponent,
  ],
  exports: [
    VisibilityPanelComponent,
  ],
})
export class VisibilityPanelModule { }
