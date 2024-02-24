import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { InstructorSessionResultQuestionViewComponent } from './instructor-session-result-question-view.component';
import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { PanelChevronModule } from '../../components/panel-chevron/panel-chevron.module';
import {
  PerQuestionViewResponsesModule,
} from '../../components/question-responses/per-question-view-responses/per-question-view-responses.module';
import {
  SingleStatisticsModule,
} from '../../components/question-responses/single-statistics/single-statistics.module';
import { QuestionTextWithInfoModule } from '../../components/question-text-with-info/question-text-with-info.module';

describe('InstructorSessionResultQuestionViewComponent', () => {
  let component: InstructorSessionResultQuestionViewComponent;
  let fixture: ComponentFixture<InstructorSessionResultQuestionViewComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [InstructorSessionResultQuestionViewComponent],
      imports: [
        PerQuestionViewResponsesModule,
        QuestionTextWithInfoModule,
        SingleStatisticsModule,
        NgbModule,
        LoadingSpinnerModule,
        PanelChevronModule,
        LoadingRetryModule,
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
