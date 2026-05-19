import { ComponentFixture, TestBed } from '@angular/core/testing';

import { provideRouter } from '@angular/router';
import { CourseRelatedInfoComponent } from './course-related-info.component';

describe('CourseRelatedInfoComponent', () => {
  let component: CourseRelatedInfoComponent;
  let fixture: ComponentFixture<CourseRelatedInfoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(CourseRelatedInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
