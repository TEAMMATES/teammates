import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { EditorModule } from '@tinymce/tinymce-angular';
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
})
export class OptionRichTextEditorModule { }
