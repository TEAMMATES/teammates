import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RankOptionsQuestionEditAnswerFormComponent } from './rank-options-question-edit-answer-form.component';

describe('RankOptionsQuestionEditAnswerFormComponent', () => {
  let component: RankOptionsQuestionEditAnswerFormComponent;
  let fixture: ComponentFixture<RankOptionsQuestionEditAnswerFormComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [RankOptionsQuestionEditAnswerFormComponent],
      imports: [
        FormsModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RankOptionsQuestionEditAnswerFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
