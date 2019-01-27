import {
    bindLinksInUnregisteredPage,
} from '../common/student';

$(document).ready(() => {
    bindLinksInUnregisteredPage('[data-unreg].navLinks');
});
