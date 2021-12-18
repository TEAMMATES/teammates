import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NewInstructorDataRowComponent } from './new-instructor-data-row.component';

describe('NewInstructorDataRowComponent', () => {
  let component: NewInstructorDataRowComponent;
  let fixture: ComponentFixture<NewInstructorDataRowComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NewInstructorDataRowComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NewInstructorDataRowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
