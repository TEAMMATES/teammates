import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FormsModule } from '@angular/forms';
import { RichTextEditorModule } from '../../../../rich-text-editor/rich-text-editor.module';
import { TextRecipientSubmissionFormComponent } from './text-recipient-submission-form.component';

describe('TextRecipientSubmissionFormComponent', () => {
  let component: TextRecipientSubmissionFormComponent;
  let fixture: ComponentFixture<TextRecipientSubmissionFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [TextRecipientSubmissionFormComponent],
      imports: [
        FormsModule,
        RichTextEditorModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TextRecipientSubmissionFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
