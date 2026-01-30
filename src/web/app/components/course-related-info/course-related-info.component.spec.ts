import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { RouterModule } from '@angular/router';
import { CourseRelatedInfoComponent } from './course-related-info.component';

describe('CourseRelatedInfoComponent', () => {
  let component: CourseRelatedInfoComponent;
  let fixture: ComponentFixture<CourseRelatedInfoComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterModule.forRoot([])
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CourseRelatedInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
