import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { GqrRqgViewResponsesComponent } from './gqr-rqg-view-responses.component';
import {
  ResponseModerationButtonModule,
} from '../../../pages-instructor/instructor-session-result-page/response-moderation-button/response-moderation-button.module';
import { PanelChevronModule } from '../../panel-chevron/panel-chevron.module';
import { QuestionTextWithInfoModule } from '../../question-text-with-info/question-text-with-info.module';
import { TeammatesCommonModule } from '../../teammates-common/teammates-common.module';
import { PerQuestionViewResponsesModule } from '../per-question-view-responses/per-question-view-responses.module';
import { SingleStatisticsModule } from '../single-statistics/single-statistics.module';

describe('GqrRqgViewResponsesComponent', () => {
  let component: GqrRqgViewResponsesComponent;
  let fixture: ComponentFixture<GqrRqgViewResponsesComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [GqrRqgViewResponsesComponent],
      imports: [
        QuestionTextWithInfoModule,
        PerQuestionViewResponsesModule,
        ResponseModerationButtonModule,
        SingleStatisticsModule,
        TeammatesCommonModule,
        HttpClientTestingModule,
        NgbModule,
        PanelChevronModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GqrRqgViewResponsesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
