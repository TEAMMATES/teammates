import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';
import { AjaxLoadingModule } from '../ajax-loading/ajax-loading.module';
import { RichTextEditorModule } from '../rich-text-editor/rich-text-editor.module';
import { ContactUsFormComponent } from './contact-us-form.component';

@NgModule({
    declarations: [ContactUsFormComponent],
    exports: [ContactUsFormComponent],
    imports: [
      CommonModule,
      FormsModule,
      NgbDropdownModule,
      RichTextEditorModule,
      AjaxLoadingModule,
    ],
})
export class ContactUsFormModule { }
