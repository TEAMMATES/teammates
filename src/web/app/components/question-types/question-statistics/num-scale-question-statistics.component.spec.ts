import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SortableTableModule } from '../../sortable-table/sortable-table.module';
import { NumScaleQuestionStatisticsComponent } from './num-scale-question-statistics.component';

describe('NumScaleQuestionStatisticsComponent', () => {
  let component: NumScaleQuestionStatisticsComponent;
  let fixture: ComponentFixture<NumScaleQuestionStatisticsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [NumScaleQuestionStatisticsComponent],
      imports: [SortableTableModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NumScaleQuestionStatisticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
