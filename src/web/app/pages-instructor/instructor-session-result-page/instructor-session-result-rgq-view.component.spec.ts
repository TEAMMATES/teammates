import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import {
  GrqRgqViewResponsesModule,
} from '../../components/question-responses/grq-rgq-view-responses/grq-rgq-view-responses.module';
import { InstructorSessionResultRgqViewComponent } from './instructor-session-result-rgq-view.component';

describe('InstructorSessionResultRgqViewComponent', () => {
  let component: InstructorSessionResultRgqViewComponent;
  let fixture: ComponentFixture<InstructorSessionResultRgqViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [InstructorSessionResultRgqViewComponent],
      imports: [GrqRgqViewResponsesModule, NgbModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorSessionResultRgqViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
