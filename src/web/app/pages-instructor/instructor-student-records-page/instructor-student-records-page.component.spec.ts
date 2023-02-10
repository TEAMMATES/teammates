import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { CommentsToCommentTableModelPipe } from '../../components/comment-box/comments-to-comment-table-model.pipe';
import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { PanelChevronModule } from '../../components/panel-chevron/panel-chevron.module';
import {
  GrqRgqViewResponsesModule,
} from '../../components/question-responses/grq-rgq-view-responses/grq-rgq-view-responses.module';
import { InstructorStudentRecordsPageComponent } from './instructor-student-records-page.component';

describe('InstructorStudentRecordsPageComponent', () => {
  let component: InstructorStudentRecordsPageComponent;
  let fixture: ComponentFixture<InstructorStudentRecordsPageComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        InstructorStudentRecordsPageComponent,
      ],
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
        NgbModule,
        GrqRgqViewResponsesModule,
        LoadingSpinnerModule,
        LoadingRetryModule,
        PanelChevronModule,
      ],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            queryParams: of({ courseid: 'su1337', studentemail: 'punk@punk.com' }),
          },
        },
        CommentsToCommentTableModelPipe,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorStudentRecordsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with populated fields', () => {
    component.studentName = 'Not John Doe';
    component.courseId = 'su1337';
    component.isStudentLoading = false;
    component.isStudentResultsLoading = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when student results are still loading', () => {
    component.studentName = 'John Doe';
    component.courseId = 'CS1111';
    component.isStudentResultsLoading = true;
    component.isStudentLoading = false;

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

});
