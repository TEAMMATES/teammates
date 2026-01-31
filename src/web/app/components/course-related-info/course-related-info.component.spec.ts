import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { RouterModule } from '@angular/router';
import { CourseRelatedInfoComponent } from './course-related-info.component';
import { TeammatesRouterModule } from '../teammates-router/teammates-router.module';

describe('CourseRelatedInfoComponent', () => {
  let component: CourseRelatedInfoComponent;
  let fixture: ComponentFixture<CourseRelatedInfoComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [CourseRelatedInfoComponent],
      imports: [RouterModule.forRoot([]), TeammatesRouterModule],
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
