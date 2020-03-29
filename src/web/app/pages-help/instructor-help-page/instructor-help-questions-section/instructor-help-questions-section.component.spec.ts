import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { InstructorHelpDataSharingService } from '../../../../services/instructor-help-data-sharing.service';
import { InstructorHelpQuestionsSectionComponent } from './instructor-help-questions-section.component';

describe('InstructorHelpQuestionsSectionComponent', () => {
  let component: InstructorHelpQuestionsSectionComponent;
  let fixture: ComponentFixture<InstructorHelpQuestionsSectionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [InstructorHelpQuestionsSectionComponent],
      imports: [NgbModule, RouterTestingModule],
      providers: [InstructorHelpDataSharingService],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorHelpQuestionsSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
