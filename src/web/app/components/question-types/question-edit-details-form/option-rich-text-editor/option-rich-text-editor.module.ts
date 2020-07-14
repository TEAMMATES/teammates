import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { EditorModule, TINYMCE_SCRIPT_SRC } from '@tinymce/tinymce-angular';
import { OptionRichTextEditorComponent } from './option-rich-text-editor.component';

/**
 * Module for a rich text editor.
 */
@NgModule({
  declarations: [OptionRichTextEditorComponent],
  imports: [
    CommonModule,
    FormsModule,
    EditorModule,
  ],
  exports: [
    OptionRichTextEditorComponent,
  ],
  providers: [
    { provide: TINYMCE_SCRIPT_SRC, useValue: 'tinymce/tinymce.min.js' },
  ],
})
export class OptionRichTextEditorModule { }
