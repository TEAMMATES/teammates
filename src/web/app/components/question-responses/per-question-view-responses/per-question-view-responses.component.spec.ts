import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterModule } from '@angular/router';

// tslint:disable-next-line:max-line-length
import { ResponseModerationButtonModule } from '../../../pages-instructor/instructor-session-result-page/response-moderation-button/response-moderation-button.module';
import { SingleResponseModule } from '../single-response/single-response.module';
import { PerQuestionViewResponsesComponent } from './per-question-view-responses.component';

describe('PerQuestionViewResponsesComponent', () => {
  let component: PerQuestionViewResponsesComponent;
  let fixture: ComponentFixture<PerQuestionViewResponsesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [PerQuestionViewResponsesComponent],
      imports: [
        SingleResponseModule,
        RouterModule,
        ResponseModerationButtonModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PerQuestionViewResponsesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
