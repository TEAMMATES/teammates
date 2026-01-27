import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { RouterModule } from '@angular/router';
import { ResponseModerationButtonComponent } from './response-moderation-button.component';
import { TeammatesRouterModule } from '../../../components/teammates-router/teammates-router.module';

describe('ResponseModerationButtonComponent', () => {
  let component: ResponseModerationButtonComponent;
  let fixture: ComponentFixture<ResponseModerationButtonComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ResponseModerationButtonComponent],
      imports: [RouterModule.forRoot([]), TeammatesRouterModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ResponseModerationButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
