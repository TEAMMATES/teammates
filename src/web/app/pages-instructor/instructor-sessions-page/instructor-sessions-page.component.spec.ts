import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { InstructorSessionsPageComponent } from './instructor-sessions-page.component';
import { InstructorSessionsPageModule } from './instructor-sessions-page.module';

describe('InstructorSessionsPageComponent', () => {
  let component: InstructorSessionsPageComponent;
  let fixture: ComponentFixture<InstructorSessionsPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientTestingModule,
        InstructorSessionsPageModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorSessionsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
