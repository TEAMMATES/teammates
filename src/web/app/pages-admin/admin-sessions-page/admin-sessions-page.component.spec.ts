import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { AdminSessionsPageComponent } from './admin-sessions-page.component';

describe('AdminSessionsPageComponent', () => {
  let component: AdminSessionsPageComponent;
  let fixture: ComponentFixture<AdminSessionsPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AdminSessionsPageComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminSessionsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
