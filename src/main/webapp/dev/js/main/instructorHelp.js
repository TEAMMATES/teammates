$('.collapse-link').on('click', function () {
    const x = this.getAttribute('data-target');
    $(x).collapse('show');
});
