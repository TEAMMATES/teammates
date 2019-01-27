import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { InstructorHelpPageComponent } from './instructor-help-page.component';

describe('InstructorHelpPageComponent', () => {
  let component: InstructorHelpPageComponent;
  let fixture: ComponentFixture<InstructorHelpPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [InstructorHelpPageComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorHelpPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
