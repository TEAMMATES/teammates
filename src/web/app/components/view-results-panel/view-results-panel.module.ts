import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';

import { ViewResultsPanelComponent } from './view-results-panel.component';
import {
  SectionTypeDescriptionModule,
} from '../../pages-instructor/instructor-session-result-page/section-type-description.module';
import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';

/**
 * View Results Panel module.
 */
@NgModule({
  imports: [
    CommonModule,
    NgbTooltipModule,
    FormsModule,
    SectionTypeDescriptionModule,
    TeammatesCommonModule,
  ],
  declarations: [
    ViewResultsPanelComponent,
  ],
  exports: [
    ViewResultsPanelComponent,
  ],
})
export class ViewResultsPanelModule { }
