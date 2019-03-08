import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import {
  PerQuestionViewResponsesModule,
} from '../../components/question-responses/per-question-view-responses/per-question-view-responses.module';
import { QuestionTextWithInfoModule } from '../../components/question-text-with-info/question-text-with-info.module';
import { InstructorSessionResultQuestionViewComponent } from './instructor-session-result-question-view.component';

describe('InstructorSessionResultQuestionViewComponent', () => {
  let component: InstructorSessionResultQuestionViewComponent;
  let fixture: ComponentFixture<InstructorSessionResultQuestionViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [InstructorSessionResultQuestionViewComponent],
      imports: [
        PerQuestionViewResponsesModule,
        QuestionTextWithInfoModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorSessionResultQuestionViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
