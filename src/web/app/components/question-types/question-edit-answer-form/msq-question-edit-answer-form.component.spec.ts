import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { FormsModule } from '@angular/forms';
import { MsqQuestionEditAnswerFormComponent } from './msq-question-edit-answer-form.component';

describe('MsqQuestionEditAnswerFormComponent', () => {
  let component: MsqQuestionEditAnswerFormComponent;
  let fixture: ComponentFixture<MsqQuestionEditAnswerFormComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [MsqQuestionEditAnswerFormComponent],
      imports: [
        FormsModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MsqQuestionEditAnswerFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

describe('updateNoneOfTheAbove', () => {
  it('should update answers based on isNoneOfTheAboveEnabled', () => {
    const component = new MsqQuestionEditAnswerFormComponent();
    component.responseDetails = {
      answers: ['A', 'B', 'C']
    };

    // Simulate isNoneOfTheAboveEnabled being false
    component.updateNoneOfTheAbove();

    expect(component.responseDetails.answers).toEqual(['NoneOfTheAbove']);

    // Simulate isNoneOfTheAboveEnabled being true
    component.responseDetails = {
      answers: ['A', 'B', 'C']
    };

    component.updateNoneOfTheAbove();

    expect(component.responseDetails.answers).toEqual([]);
  });
});
