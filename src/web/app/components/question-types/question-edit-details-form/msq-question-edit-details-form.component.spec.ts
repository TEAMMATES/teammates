import { DragDropModule } from '@angular/cdk/drag-drop';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { GeneratedChoicePipe } from './generated-choice.pipe';
import { MsqFieldComponent } from './msq-field/msq-field.component';
import { MsqQuestionEditDetailsFormComponent } from './msq-question-edit-details-form.component';
import { WeightFieldComponent } from './weight-field/weight-field.component';

describe('MsqQuestionEditDetailsFormComponent', () => {
  let component: MsqQuestionEditDetailsFormComponent;
  let fixture: ComponentFixture<MsqQuestionEditDetailsFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        MsqQuestionEditDetailsFormComponent,
        MsqFieldComponent,
        WeightFieldComponent,
        GeneratedChoicePipe,
      ],
      imports: [
        FormsModule,
        DragDropModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MsqQuestionEditDetailsFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
