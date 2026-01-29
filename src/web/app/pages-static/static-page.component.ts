import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { AuthInfo } from '../../types/api-output';

/**
 * Base skeleton for static pages.
 */
@Component({
    selector: 'tm-static-page',
    templateUrl: './static-page.component.html',
})
export class StaticPageComponent implements OnInit {

    studentLoginUrl: string = '';
    instructorLoginUrl: string = '';
    user: string = '';
    isInstructor: boolean = false;
    isStudent: boolean = false;
    isAdmin: boolean = false;
    isMaintainer: boolean = false;
    navItems: any[] = [
        { url: '/web/front', display: 'Home' },
        { url: '/web/front/features', display: 'Features' },
        { url: '/web/front/about', display: 'About' },
        { url: '/web/front/contact', display: 'Contact' },
        { url: '/web/front/terms', display: 'Terms' },
        {
            display: 'Help',
            children: [
                { url: '/web/front/help/student', display: 'Help for Students' },
                { url: '/web/front/help/instructor', display: 'Help for Instructors' },
                { url: '/web/front/help/session-links-recovery', display: 'Recover Session Links' },
            ],
        },
    ];
    isFetchingAuthDetails: boolean = false;

    constructor(private authService: AuthService) {}

    ngOnInit(): void {
        this.isFetchingAuthDetails = true;
        this.authService.getAuthUser().subscribe({
            next: (res: AuthInfo) => {
                if (res.user) {
                    this.setUserInfo(res);
                } else {
                    this.studentLoginUrl = res.studentLoginUrl ?? '';
                    this.instructorLoginUrl = res.instructorLoginUrl ?? '';
                }
                this.isFetchingAuthDetails = false;
            },
            error: () => {
                this.resetUserInfo();
                this.isFetchingAuthDetails = false;
            },
        });
    }

    /**
     * Helper to set user info from AuthInfo.
     */
    private setUserInfo(res: AuthInfo): void {
        this.user = res.user?.id ?? '';
        if (res.masquerade) {
            this.user += ' (M)';
        }
        this.isInstructor = !!res.user?.isInstructor;
        this.isStudent = !!res.user?.isStudent;
        this.isAdmin = !!res.user?.isAdmin;
        this.isMaintainer = !!res.user?.isMaintainer;
    }

    /**
     * Helper to reset user info when error occurs.
     */
    private resetUserInfo(): void {
        this.user = '';
        this.isInstructor = false;
        this.isStudent = false;
        this.isAdmin = false;
        this.isMaintainer = false;
    }


}
