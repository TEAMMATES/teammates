import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SortableTableModule } from '../../sortable-table/sortable-table.module';
import { RankOptionsQuestionStatisticsComponent } from './rank-options-question-statistics.component';
import { default as responses } from './test-data/rankOptionQuestionResponses.json';

describe('RankOptionsQuestionStatisticsComponent', () => {
  let component: RankOptionsQuestionStatisticsComponent;
  let fixture: ComponentFixture<RankOptionsQuestionStatisticsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [RankOptionsQuestionStatisticsComponent],
      imports: [SortableTableModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RankOptionsQuestionStatisticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should calculate statistics correctly', () => {
    component.question.options = ['optionA', 'optionB', 'optionC', 'optionD'];
    component.responses = JSON.parse(JSON.stringify(responses.responses));

    const expectedRankReceivedPerOption: Record<string, number[]> = {
      optionA: [1, 2, 4], optionB: [2, 3, 3],
      optionC: [1, 2, 3], optionD: [1, 4, 4],
    };

    const expectedRankPerOption: Record<string, number> = {
      optionA: 2, optionB: 3, optionC: 1, optionD: 4,
    };

    component.calculateStatistics();

    expect(component.ranksReceivedPerOption).toEqual(expectedRankReceivedPerOption);
    expect(component.rankPerOption).toEqual(expectedRankPerOption);
  });

  it('should calculate statistics correctly if there are equal ranks', () => {
    component.question.options = ['optionA', 'optionB', 'optionC', 'optionD'];
    component.responses = JSON.parse(JSON.stringify(responses.responsesSameRank));

    const expectedRankReceivedPerOption: Record<string, number[]> = {
      optionA: [1, 2, 4], optionB: [1, 2, 3],
      optionC: [1, 2, 3], optionD: [3, 4, 4],
    };

    const expectedRankPerOption: Record<string, number> = {
      optionA: 3, optionB: 1, optionC: 1, optionD: 4,
    };

    component.calculateStatistics();

    expect(component.ranksReceivedPerOption).toEqual(expectedRankReceivedPerOption);
    expect(component.rankPerOption).toEqual(expectedRankPerOption);
  });
});
