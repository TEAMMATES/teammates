import { DragDropModule } from '@angular/cdk/drag-drop';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { GeneratedChoicePipe } from './generated-choice.pipe';
import { McqFieldComponent } from './mcq-field/mcq-field.component';
import { McqQuestionEditDetailsFormComponent } from './mcq-question-edit-details-form.component';
import { OptionRichTextEditorModule } from './option-rich-text-editor/option-rich-text-editor.module';
import { WeightFieldComponent } from './weight-field/weight-field.component';

describe('McqQuestionEditDetailsFormComponent', () => {
  let component: McqQuestionEditDetailsFormComponent;
  let fixture: ComponentFixture<McqQuestionEditDetailsFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        McqQuestionEditDetailsFormComponent,
        McqFieldComponent,
        WeightFieldComponent,
        GeneratedChoicePipe,
      ],
      imports: [
        FormsModule,
        OptionRichTextEditorModule,
        DragDropModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(McqQuestionEditDetailsFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
