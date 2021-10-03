import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { FormsModule } from '@angular/forms';
import { ViewResultsPanelComponent } from './view-results-panel.component';
import { SectionTypeDescriptionModule } from '../../pages-instructor/instructor-session-result-page/section-type-description.module';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';

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
