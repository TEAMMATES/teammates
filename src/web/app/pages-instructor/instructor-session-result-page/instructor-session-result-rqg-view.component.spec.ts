import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { PanelChevronModule } from '../../components/panel-chevron/panel-chevron.module';
import {
  GqrRqgViewResponsesModule,
} from '../../components/question-responses/gqr-rqg-view-responses/gqr-rqg-view-responses.module';
import { InstructorSessionResultRqgViewComponent } from './instructor-session-result-rqg-view.component';

describe('InstructorSessionResultRqgViewComponent', () => {
  let component: InstructorSessionResultRqgViewComponent;
  let fixture: ComponentFixture<InstructorSessionResultRqgViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [InstructorSessionResultRqgViewComponent],
      imports: [GqrRqgViewResponsesModule, NgbModule, LoadingSpinnerModule, PanelChevronModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorSessionResultRqgViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
