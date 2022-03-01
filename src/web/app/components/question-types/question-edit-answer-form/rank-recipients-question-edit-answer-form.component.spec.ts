import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { FormsModule } from '@angular/forms';
import { RankRecipientsQuestionEditAnswerFormComponent } from './rank-recipients-question-edit-answer-form.component';

describe('RankRecipientsQuestionEditAnswerFormComponent', () => {
  let component: RankRecipientsQuestionEditAnswerFormComponent;
  let fixture: ComponentFixture<RankRecipientsQuestionEditAnswerFormComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [RankRecipientsQuestionEditAnswerFormComponent],
      imports: [
        FormsModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RankRecipientsQuestionEditAnswerFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
