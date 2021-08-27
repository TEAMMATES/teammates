import { Component, OnInit } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { finalize } from "rxjs/operators";
import { AccountService } from "../services/account.service";
import { NgbModal } from "@ng-bootstrap/ng-bootstrap";
import { AuthService } from "../services/auth.service";
import { AuthInfo } from "../types/api-output";
import { ErrorMessageOutput } from "./error-message-output";
import { NavigationService } from "../services/navigation.service";
import { ErrorReportComponent } from "./components/error-report/error-report.component";

/**
 * User create account page component.
 */
@Component({
  selector: "tm-user-create-account-page",
  templateUrl: "./user-create-account-page.component.html",
  styleUrls: ["./user-create-account-page.component.scss"],
})
export class UserCreateAccountPageComponent implements OnInit {
  isLoading: boolean = true;
  hasJoined: boolean = false;
  validUrl: boolean = true;
  key: string = "";
  userId: string = "";

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private navigationService: NavigationService,
    private accountService: AccountService,
    private authService: AuthService,
    private ngbModal: NgbModal
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.key = queryParams.key;

      if (!this.key) {
        this.validUrl = false;
        this.isLoading = false;
        return;
      }

      this.authService.getAuthUser().subscribe((resp: AuthInfo) => {
        this.userId = resp.user?.id || "";
        
        if (resp.user?.isInstructor) {
          // User already has instructor account
          this.navigationService.navigateByURL(this.router, `/web/instructor`);
        }

        this.isLoading = false;
      });
    });
  }

  createAccount(): void {
    this.isLoading = true;
    this.accountService
      .createAccount(this.key)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe(
        (_resp) => {
          this.navigationService.navigateByURL(this.router, `/web/instructor`);
        },
        (resp: ErrorMessageOutput) => {
          const modalRef: any = this.ngbModal.open(ErrorReportComponent);
          modalRef.componentInstance.requestId = resp.error.requestId;
          modalRef.componentInstance.errorMessage = resp.error.message;
        }
      );
  }
}
