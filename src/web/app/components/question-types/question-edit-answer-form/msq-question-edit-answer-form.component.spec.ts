import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FormsModule } from '@angular/forms';
import { MsqQuestionEditAnswerFormComponent } from './msq-question-edit-answer-form.component';

describe('MsqQuestionEditAnswerFormComponent', () => {
  let component: MsqQuestionEditAnswerFormComponent;
  let fixture: ComponentFixture<MsqQuestionEditAnswerFormComponent>;

  beforeEach(async(() => {
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
