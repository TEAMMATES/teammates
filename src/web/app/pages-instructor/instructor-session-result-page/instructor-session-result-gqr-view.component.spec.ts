import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { InstructorSessionResultGqrViewComponent } from './instructor-session-result-gqr-view.component';
import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { PanelChevronModule } from '../../components/panel-chevron/panel-chevron.module';
import {
  GqrRqgViewResponsesModule,
} from '../../components/question-responses/gqr-rqg-view-responses/gqr-rqg-view-responses.module';

describe('InstructorSessionResultGqrViewComponent', () => {
  let component: InstructorSessionResultGqrViewComponent;
  let fixture: ComponentFixture<InstructorSessionResultGqrViewComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [InstructorSessionResultGqrViewComponent],
      imports: [GqrRqgViewResponsesModule, NgbModule, LoadingSpinnerModule, PanelChevronModule, LoadingRetryModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorSessionResultGqrViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
