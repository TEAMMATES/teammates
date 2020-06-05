import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { QuestionStatisticsModule } from '../../question-types/question-statistics/question-statistics.module';
import { RemoveMissingResponsesPipe } from './remove-missing-responses.pipe';
import { SingleStatisticsComponent } from './single-statistics.component';

describe('SingleStatisticsComponent', () => {
  let component: SingleStatisticsComponent;
  let fixture: ComponentFixture<SingleStatisticsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [SingleStatisticsComponent, RemoveMissingResponsesPipe],
      imports: [QuestionStatisticsModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SingleStatisticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
