import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { AjaxLoadingModule } from '../../../components/ajax-loading/ajax-loading.module';
import { RichTextEditorModule } from '../../../components/rich-text-editor/rich-text-editor.module';
import { TeammatesCommonModule } from '../../../components/teammates-common/teammates-common.module';
import { CourseEditFormComponent } from './course-edit-form.component';

/**
 * Module for instructor course edit form.
 */
@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    NgbModule,
    AjaxLoadingModule,
    TeammatesCommonModule,
    RichTextEditorModule,
  ],
  declarations: [
    CourseEditFormComponent,
  ],
  exports: [
    CourseEditFormComponent,
  ],
})
export class CourseEditFormModule { }
