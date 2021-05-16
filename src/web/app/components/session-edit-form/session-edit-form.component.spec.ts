import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { TeammatesRouterModule } from '../teammates-router/teammates-router.module';
import { SessionEditFormComponent } from './session-edit-form.component';
import { SessionEditFormModule } from './session-edit-form.module';

describe('SessionEditFormComponent', () => {
  let component: SessionEditFormComponent;
  let fixture: ComponentFixture<SessionEditFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        SessionEditFormModule,
        HttpClientTestingModule,
        RouterTestingModule,
        TeammatesRouterModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SessionEditFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
