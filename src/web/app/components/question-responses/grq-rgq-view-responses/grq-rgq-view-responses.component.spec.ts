import { async, ComponentFixture, TestBed } from '@angular/core/testing';

// tslint:disable-next-line:max-line-length
import { ResponseModerationButtonModule } from '../../../pages-instructor/instructor-session-result-page/response-moderation-button/response-moderation-button.module';
import { GroupedResponsesModule } from '../grouped-responses/grouped-responses.module';
import { GrqRgqViewResponsesComponent } from './grq-rgq-view-responses.component';

describe('GrqRgqViewResponsesComponent', () => {
  let component: GrqRgqViewResponsesComponent;
  let fixture: ComponentFixture<GrqRgqViewResponsesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [GrqRgqViewResponsesComponent],
      imports: [
        GroupedResponsesModule,
        ResponseModerationButtonModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GrqRgqViewResponsesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
