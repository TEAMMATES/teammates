import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { SessionEditFormComponent } from './session-edit-form.component';
import { AjaxLoadingModule } from '../ajax-loading/ajax-loading.module';
import { DatepickerModule } from '../datepicker/datepicker.module';
import { RichTextEditorModule } from '../rich-text-editor/rich-text-editor.module';
import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';
import { TeammatesRouterModule } from '../teammates-router/teammates-router.module';
import { TimepickerModule } from '../timepicker/timepicker.module';

/**
 * Module for instructor session edit/create form.
 */
@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    NgbTooltipModule,
    AjaxLoadingModule,
    TeammatesCommonModule,
    RichTextEditorModule,
    TeammatesRouterModule,
    DatepickerModule,
    TimepickerModule,
  ],
  declarations: [
    SessionEditFormComponent,
  ],
  exports: [
    SessionEditFormComponent,
  ],
})
export class SessionEditFormModule { }
