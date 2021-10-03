import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { FormsModule } from '@angular/forms';
import { ViewResultsPanelComponent } from './view-results-panel.component';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
import { SectionTypeDescriptionPipe } from '../../pages-instructor/instructor-session-result-page/section-type-description.pipe';

/**
 * X module.
 */
@NgModule({
  imports: [
    CommonModule,
    NgbTooltipModule,
    FormsModule,
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
