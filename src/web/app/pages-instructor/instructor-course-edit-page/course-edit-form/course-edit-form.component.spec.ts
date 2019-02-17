import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { CourseEditFormComponent } from './course-edit-form.component';
import { CourseEditFormModule } from './course-edit-form.module';

describe('CourseEditFormComponent', () => {
  let component: CourseEditFormComponent;
  let fixture: ComponentFixture<CourseEditFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        CourseEditFormModule,
        HttpClientTestingModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CourseEditFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
