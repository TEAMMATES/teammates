import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SessionsTableComponent } from './sessions-table.component';
import { SessionsTableModule } from './sessions-table.module';

describe('SessionsTableComponent', () => {
  let component: SessionsTableComponent;
  let fixture: ComponentFixture<SessionsTableComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [SessionsTableModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SessionsTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
