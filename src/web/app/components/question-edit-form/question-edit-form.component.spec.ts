import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { AjaxLoadingModule } from '../ajax-loading/ajax-loading.module';
import {
  QuestionEditDetailsFormModule,
} from '../question-types/question-edit-details-form/question-edit-details-form.module';
import { RichTextEditorModule } from '../rich-text-editor/rich-text-editor.module';
import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';
import { VisibilityMessagesModule } from '../visibility-messages/visibility-messages.module';
import { GiverTypeDescriptionPipe, RecipientTypeDescriptionPipe } from './feedback-path.pipe';
import { QuestionEditFormComponent } from './question-edit-form.component';
import {
  VisibilityControlNamePipe,
  VisibilityTypeDescriptionPipe,
  VisibilityTypeNamePipe,
} from './visibility-setting.pipe';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { mockTinyMceUuid } from '../../../test-helpers/mock-tinymce-uuid';
import { PanelChevronModule } from '../panel-chevron/panel-chevron.module';

describe('QuestionEditFormComponent', () => {
  let component: QuestionEditFormComponent;
  let fixture: ComponentFixture<QuestionEditFormComponent>;

  mockTinyMceUuid();

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        QuestionEditFormComponent,
        GiverTypeDescriptionPipe,
        RecipientTypeDescriptionPipe,
        VisibilityControlNamePipe,
        VisibilityTypeDescriptionPipe,
        VisibilityTypeNamePipe,
      ],
      imports: [
        HttpClientTestingModule,
        FormsModule,
        TeammatesCommonModule,
        AjaxLoadingModule,
        RichTextEditorModule,
        QuestionEditDetailsFormModule,
        NgbModule,
        VisibilityMessagesModule,
        BrowserAnimationsModule,
        PanelChevronModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(QuestionEditFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default view', () => {
    expect(fixture).toMatchSnapshot();
  });
});
