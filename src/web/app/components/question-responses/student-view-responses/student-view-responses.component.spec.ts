import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { StudentViewResponsesComponent } from './student-view-responses.component';
import { CommentBoxModule } from '../../comment-box/comment-box.module';
import { SingleResponseModule } from '../single-response/single-response.module';

describe('StudentViewResponsesComponent', () => {
  let component: StudentViewResponsesComponent;
  let fixture: ComponentFixture<StudentViewResponsesComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [StudentViewResponsesComponent],
      imports: [SingleResponseModule, CommentBoxModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StudentViewResponsesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
