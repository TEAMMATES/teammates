import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { AjaxLoadingModule } from '../../components/ajax-loading/ajax-loading.module';
import { CommentToCommentRowModelPipe } from '../../components/comment-box/comment-to-comment-row-model.pipe';
import { CommentsToCommentTableModelPipe } from '../../components/comment-box/comments-to-comment-table-model.pipe';
import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { PanelChevronModule } from '../../components/panel-chevron/panel-chevron.module';
import {
  PreviewSessionResultPanelModule,
} from '../../components/preview-session-result-panel/preview-session-result-panel.module';
import {
  GqrRqgViewResponsesModule,
} from '../../components/question-responses/gqr-rqg-view-responses/gqr-rqg-view-responses.module';
import {
  GrqRgqViewResponsesModule,
} from '../../components/question-responses/grq-rgq-view-responses/grq-rgq-view-responses.module';
import {
  PerQuestionViewResponsesModule,
} from '../../components/question-responses/per-question-view-responses/per-question-view-responses.module';
import {
  SingleStatisticsModule,
} from '../../components/question-responses/single-statistics/single-statistics.module';
import { QuestionTextWithInfoModule } from '../../components/question-text-with-info/question-text-with-info.module';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
import { TeammatesRouterModule } from '../../components/teammates-router/teammates-router.module';
import { ViewResultsPanelModule } from '../../components/view-results-panel/view-results-panel.module';
import { InstructorSessionNoResponsePanelComponent } from './instructor-session-no-response-panel.component';
import { InstructorSessionResultGqrViewComponent } from './instructor-session-result-gqr-view.component';
import { InstructorSessionResultGrqViewComponent } from './instructor-session-result-grq-view.component';
import { InstructorSessionResultPageComponent } from './instructor-session-result-page.component';
import { InstructorSessionResultQuestionViewComponent } from './instructor-session-result-question-view.component';
import { InstructorSessionResultRgqViewComponent } from './instructor-session-result-rgq-view.component';
import { InstructorSessionResultRqgViewComponent } from './instructor-session-result-rqg-view.component';
import { SectionTypeDescriptionModule } from './section-type-description.module';

describe('InstructorSessionResultPageComponent', () => {
  let component: InstructorSessionResultPageComponent;
  let fixture: ComponentFixture<InstructorSessionResultPageComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        InstructorSessionResultPageComponent,
        InstructorSessionResultQuestionViewComponent,
        InstructorSessionResultRgqViewComponent,
        InstructorSessionResultGrqViewComponent,
        InstructorSessionResultRqgViewComponent,
        InstructorSessionResultGqrViewComponent,
        InstructorSessionNoResponsePanelComponent,
      ],
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
        FormsModule,
        NgbModule,
        TeammatesCommonModule,
        QuestionTextWithInfoModule,
        GqrRqgViewResponsesModule,
        GrqRgqViewResponsesModule,
        PerQuestionViewResponsesModule,
        SingleStatisticsModule,
        LoadingSpinnerModule,
        AjaxLoadingModule,
        LoadingRetryModule,
        PanelChevronModule,
        TeammatesRouterModule,
        ViewResultsPanelModule,
        SectionTypeDescriptionModule,
        PreviewSessionResultPanelModule,
      ],
      providers: [
        CommentsToCommentTableModelPipe,
        CommentToCommentRowModelPipe,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorSessionResultPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
