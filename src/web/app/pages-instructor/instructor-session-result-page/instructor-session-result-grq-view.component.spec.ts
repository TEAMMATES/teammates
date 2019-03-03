import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import {
  GrqRgqViewResponsesModule,
} from '../../components/question-responses/grq-rgq-view-responses/grq-rgq-view-responses.module';
import { InstructorSessionResultGrqViewComponent } from './instructor-session-result-grq-view.component';

describe('InstructorSessionResultGrqViewComponent', () => {
  let component: InstructorSessionResultGrqViewComponent;
  let fixture: ComponentFixture<InstructorSessionResultGrqViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [InstructorSessionResultGrqViewComponent],
      imports: [GrqRgqViewResponsesModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorSessionResultGrqViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
