import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InstructorSearchBarComponent } from './instructor-search-bar.component';

describe('InstructorSearchBarComponent', () => {
  let component: InstructorSearchBarComponent;
  let fixture: ComponentFixture<InstructorSearchBarComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ InstructorSearchBarComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorSearchBarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
