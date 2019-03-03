import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { QuestionResponseModule } from '../../question-types/question-response/question-response.module';
import { SingleResponseComponent } from './single-response.component';

describe('SingleResponseComponent', () => {
  let component: SingleResponseComponent;
  let fixture: ComponentFixture<SingleResponseComponent>;

  beforeEach(async(() => {
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
