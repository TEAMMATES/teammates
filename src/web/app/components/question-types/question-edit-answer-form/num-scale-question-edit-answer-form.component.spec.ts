import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { NumScaleQuestionEditAnswerFormComponent } from './num-scale-question-edit-answer-form.component';

describe('NumScaleQuestionEditAnswerFormComponent', () => {
  let component: NumScaleQuestionEditAnswerFormComponent;
  let fixture: ComponentFixture<NumScaleQuestionEditAnswerFormComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [NumScaleQuestionEditAnswerFormComponent],
      imports: [
        FormsModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NumScaleQuestionEditAnswerFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
