import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbDropdownModule, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';

import { VisibilityPanelComponent } from './visibility-panel.component';
import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';
import { VisibilityMessagesModule } from '../visibility-messages/visibility-messages.module';

/**
 * Visibility panel module.
 */
@NgModule({
  imports: [
    CommonModule,
    FormsModule,
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
