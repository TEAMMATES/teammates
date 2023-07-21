import { Component, Input } from '@angular/core';

@Component({
    selector: 'tm-creator-email',
    template: ' <a [href]="instructorHomePageLink">{{ creatorEmail }}</a> ',
    standalone: true,
})
export class CreatorEmailComponent {
    @Input() instructorHomePageLink: string = '';
    @Input() creatorEmail: string = '';
}
