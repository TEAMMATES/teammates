import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { QuestionEditBriefDescriptionFormComponent } from './question-edit-brief-description-form.component';
import { RichTextEditorModule } from '../rich-text-editor/rich-text-editor.module';

describe('QuestionEditBriefDescriptionFormComponent', () => {
  let component: QuestionEditBriefDescriptionFormComponent;
  let fixture: ComponentFixture<QuestionEditBriefDescriptionFormComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        QuestionEditBriefDescriptionFormComponent,
      ],
      imports: [
        FormsModule,
        RichTextEditorModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(QuestionEditBriefDescriptionFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
