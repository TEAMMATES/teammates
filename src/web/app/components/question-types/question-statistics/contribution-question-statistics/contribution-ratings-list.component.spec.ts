import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ContributionRatingsListComponent } from './contribution-ratings-list.component';
import { ContributionComponent } from './contribution.component';

describe('ContributionRatingsListComponent', () => {
  let component: ContributionRatingsListComponent;
  let fixture: ComponentFixture<ContributionRatingsListComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ContributionRatingsListComponent, ContributionComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ContributionRatingsListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
