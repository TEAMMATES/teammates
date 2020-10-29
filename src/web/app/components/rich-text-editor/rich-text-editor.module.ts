import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { EditorModule, TINYMCE_SCRIPT_SRC } from '@tinymce/tinymce-angular';
import { InViewportModule } from 'ng-in-viewport';
import { RichTextEditorComponent } from './rich-text-editor.component';
import { TINYMCE_BASE_URL } from './tinymce';

/**
 * Module for a rich text editor.
 */
@NgModule({
  declarations: [RichTextEditorComponent],
  imports: [
    CommonModule,
    FormsModule,
    EditorModule,
    InViewportModule,
  ],
  exports: [
    RichTextEditorComponent,
  ],
  providers: [
    { provide: TINYMCE_SCRIPT_SRC, useValue: `${TINYMCE_BASE_URL}/tinymce.min.js` },
  ],
})
export class RichTextEditorModule { }
