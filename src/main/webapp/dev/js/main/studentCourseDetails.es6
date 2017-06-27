import {
    bindLinksInUnregisteredPage,
} from '../common/student.es6';

$(document).ready(() => {
    bindLinksInUnregisteredPage('[data-unreg].navLinks');
});
