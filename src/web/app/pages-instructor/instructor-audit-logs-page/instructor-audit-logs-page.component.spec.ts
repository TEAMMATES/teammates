import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InstructorAuditLogsPageComponent } from './instructor-audit-logs-page.component';
import { InstructorAuditLogsPageModule } from './instructor-audit-logs-page.module';

describe('InstructorAuditLogsPageComponent', () => {
  let component: InstructorAuditLogsPageComponent;
  let fixture: ComponentFixture<InstructorAuditLogsPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [InstructorAuditLogsPageModule, HttpClientTestingModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorAuditLogsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
