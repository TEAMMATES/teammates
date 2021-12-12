import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { SingleStatisticsModule } from '../question-responses/single-statistics/single-statistics.module';
import {
  StudentViewResponsesModule,
} from '../question-responses/student-view-responses/student-view-responses.module';
import { QuestionTextWithInfoModule } from '../question-text-with-info/question-text-with-info.module';
import { QuestionResponsePanelComponent } from './question-response-panel.component';

describe('QuestionResponsePanelComponent', () => {
  let component: QuestionResponsePanelComponent;
  let fixture: ComponentFixture<QuestionResponsePanelComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        QuestionResponsePanelComponent,
      ],
      imports: [
        SingleStatisticsModule,
        StudentViewResponsesModule,
        QuestionTextWithInfoModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(QuestionResponsePanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
