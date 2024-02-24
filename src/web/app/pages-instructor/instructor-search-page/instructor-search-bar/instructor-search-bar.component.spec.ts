import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';

import { InstructorSearchBarComponent } from './instructor-search-bar.component';
import { SingleResponseModule } from '../../../components/question-responses/single-response/single-response.module';

describe('InstructorSearchBarComponent', () => {
  let component: InstructorSearchBarComponent;
  let fixture: ComponentFixture<InstructorSearchBarComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [InstructorSearchBarComponent],
      imports: [FormsModule, SingleResponseModule],
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
