import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FeedbackRankRecipientsResponseDetails } from 'src/web/types/api-output';
import { SortableTableModule } from '../../sortable-table/sortable-table.module';
import { RankRecipientsQuestionStatisticsComponent } from './rank-recipients-question-statistics.component';
import { Response } from './question-statistics';

/**
 * Loads data for testing.
 */
const loadTestData: (filename: string) => Response<FeedbackRankRecipientsResponseDetails>[] = (filename: string): Response<FeedbackRankRecipientsResponseDetails>[] => {
  return require(`./test-data/${filename}`);
};

describe('RankRecipientsQuestionStatisticsComponent', () => {
  let component: RankRecipientsQuestionStatisticsComponent;
  let fixture: ComponentFixture<RankRecipientsQuestionStatisticsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [RankRecipientsQuestionStatisticsComponent],
      imports: [SortableTableModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RankRecipientsQuestionStatisticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should rank correctly within team', () => {
    const responses: Response<FeedbackRankRecipientsResponseDetails>[] = loadTestData("rankRecipientsResponses.json");
    component.responses = responses;

    component.calculateStatistics();

    expect(component.rankPerOption['charlie']).toBe(3);
    expect(component.rankPerOptionInTeam['charlie']).toBe(2);
    expect(component.rankPerOptionInTeam['bob']).toBe(1);

  });

});
