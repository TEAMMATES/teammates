import { Component, Input } from '@angular/core';

@Component({
    selector: 'tm-creator-email',
    templateUrl: './cell-with-creator-email.component.html',
    standalone: true,
})
export class CreatorEmailComponent {
    @Input() instructorHomePageLink: string = '';
    @Input() creatorEmail: string = '';
}
