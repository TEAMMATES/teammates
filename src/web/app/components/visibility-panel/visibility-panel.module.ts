import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { NgbDropdownModule, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';

import { VisibilityPanelComponent } from './visibility-panel.component';
import { VisibilityMessagesModule } from '../visibility-messages/visibility-messages.module';
import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';

/**
 * Visibility panel module.
 */
@NgModule({
  imports: [
    CommonModule,
    NgbDropdownModule,
    NgbTooltipModule,
    TeammatesCommonModule, 
    VisibilityMessagesModule,
  ],
  declarations: [
    VisibilityPanelComponent,
  ],
  exports: [
    VisibilityPanelComponent,
  ],
})
export class VisibilityPanelModule { }
