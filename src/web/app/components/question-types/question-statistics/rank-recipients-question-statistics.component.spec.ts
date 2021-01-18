import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FeedbackParticipantType, FeedbackRankRecipientsResponseDetails } from '../../../../types/api-output';
import { SortableTableModule } from '../../sortable-table/sortable-table.module';
import { Response } from './question-statistics';
import { RankRecipientsQuestionStatisticsComponent } from './rank-recipients-question-statistics.component';

/**
 * Loads data for testing.
 */
const loadTestData: (filename: string) => Response<FeedbackRankRecipientsResponseDetails>[] =
(filename: string): Response<FeedbackRankRecipientsResponseDetails>[] => {
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

  it('should not calculate team rank if recipient type is invalid', () => {
    component.responses = loadTestData('rankRecipientsResponses.json');

    // ranks inside teams are meaningless when ranking across teams
    component.recipientType = FeedbackParticipantType.TEAMS;

    component.calculateStatistics();

    expect(component.rankPerOptionInTeam).toMatchObject({});
  });

  it('should rank correctly within team when recipient type is valid', () => {
    component.responses = loadTestData('rankRecipientsResponses.json');
    component.recipientType = FeedbackParticipantType.OWN_TEAM_MEMBERS;

    const bob: string = 'bob';
    const charlie: string = 'charlie';
    const delta: string = 'delta';

    component.calculateStatistics();

    expect(component.rankPerOption[delta]).toBe(2);
    expect(component.rankPerOptionInTeam[bob]).toBe(1);
    expect(component.rankPerOptionInTeam[charlie]).toBe(2);
    expect(component.rankPerOptionInTeam[delta]).toBe(1);
    expect(component.rankPerOptionInTeamExcludeSelf[delta]).toBe(2);

  });

});
