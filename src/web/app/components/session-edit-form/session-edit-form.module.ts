import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { AjaxLoadingModule } from '../ajax-loading/ajax-loading.module';
import { RichTextEditorModule } from '../rich-text-editor/rich-text-editor.module';
import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';
import { SessionEditFormComponent } from './session-edit-form.component';
import { SubmissionStatusNamePipe } from './submission-status-name.pipe';
import { TimePickerComponent } from './time-picker/time-picker.component';

/**
 * Module for instructor session edit/create form.
 */
@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    NgbModule,
    AjaxLoadingModule,
    TeammatesCommonModule,
    RichTextEditorModule,
  ],
  declarations: [
    SessionEditFormComponent,
    TimePickerComponent,
    SubmissionStatusNamePipe,
  ],
  exports: [
    SessionEditFormComponent,
  ],
})
export class SessionEditFormModule { }
