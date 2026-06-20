import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SingleStatisticsComponent } from './single-statistics.component';

describe('SingleStatisticsComponent', () => {
  let component: SingleStatisticsComponent;
  let fixture: ComponentFixture<SingleStatisticsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({}).compileComponents();

    fixture = TestBed.createComponent(SingleStatisticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
