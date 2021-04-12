import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FormsModule } from '@angular/forms';
import {
  ConstsumRecipientsQuestionEditAnswerFormComponent,
} from './constsum-recipients-question-edit-answer-form.component';

describe('ConstsumRecipientsQuestionEditAnswerFormComponent', () => {
  let component: ConstsumRecipientsQuestionEditAnswerFormComponent;
  let fixture: ComponentFixture<ConstsumRecipientsQuestionEditAnswerFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ConstsumRecipientsQuestionEditAnswerFormComponent],
      imports: [
        FormsModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConstsumRecipientsQuestionEditAnswerFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
