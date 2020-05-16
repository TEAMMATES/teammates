import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FormsModule } from '@angular/forms';
import { ConstsumOptionsQuestionEditAnswerFormComponent } from './constsum-options-question-edit-answer-form.component';

describe('ConstsumOptionsQuestionEditAnswerFormComponent', () => {
  let component: ConstsumOptionsQuestionEditAnswerFormComponent;
  let fixture: ComponentFixture<ConstsumOptionsQuestionEditAnswerFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ConstsumOptionsQuestionEditAnswerFormComponent],
      imports: [
        FormsModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConstsumOptionsQuestionEditAnswerFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
