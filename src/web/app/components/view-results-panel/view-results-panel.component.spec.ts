import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewResultsPanelComponent } from './view-results-panel.component';
import { ViewResultsPanelModule } from './view-results-panel.module';

describe('ViewResultsPanelComponent', () => {
  let component: ViewResultsPanelComponent;
  let fixture: ComponentFixture<ViewResultsPanelComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ViewResultsPanelComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewResultsPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
