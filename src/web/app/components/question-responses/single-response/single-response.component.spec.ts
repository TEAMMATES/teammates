import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { SingleResponseComponent } from './single-response.component';
import { QuestionResponseModule } from '../../question-types/question-response/question-response.module';

describe('SingleResponseComponent', () => {
  let component: SingleResponseComponent;
  let fixture: ComponentFixture<SingleResponseComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [SingleResponseComponent],
      imports: [QuestionResponseModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SingleResponseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
