import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { InstructorSessionsResultGqrViewComponent } from './instructor-sessions-result-gqr-view.component';
import { InstructorSessionsResultGrqViewComponent } from './instructor-sessions-result-grq-view.component';
import { InstructorSessionsResultPageComponent } from './instructor-sessions-result-page.component';
import { InstructorSessionsResultQuestionViewComponent } from './instructor-sessions-result-question-view.component';
import { InstructorSessionsResultRgqViewComponent } from './instructor-sessions-result-rgq-view.component';
import { InstructorSessionsResultRqgViewComponent } from './instructor-sessions-result-rqg-view.component';

describe('InstructorSessionsResultPageComponent', () => {
  let component: InstructorSessionsResultPageComponent;
  let fixture: ComponentFixture<InstructorSessionsResultPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        InstructorSessionsResultPageComponent,
        InstructorSessionsResultQuestionViewComponent,
        InstructorSessionsResultRgqViewComponent,
        InstructorSessionsResultGrqViewComponent,
        InstructorSessionsResultRqgViewComponent,
        InstructorSessionsResultGqrViewComponent,
      ],
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
        FormsModule,
        NgbModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorSessionsResultPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
