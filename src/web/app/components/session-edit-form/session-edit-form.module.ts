import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { SessionEditFormComponent } from './session-edit-form.component';


import { RichTextEditorModule } from '../rich-text-editor/rich-text-editor.module';




/**
 * Module for instructor session edit/create form.
 */
@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    NgbTooltipModule,
    RichTextEditorModule,
    SessionEditFormComponent,
],
  exports: [
    SessionEditFormComponent,
  ],
})
export class SessionEditFormModule { }
