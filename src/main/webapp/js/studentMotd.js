'use strict';

$(document).ready(function() {
    StudentMotd.fetchMotd(motdUrl, '#student-motd', '#student-motd-container');
    StudentMotd.bindCloseMotdButton('#btn-close-motd', '#student-motd-container');
});

/**
 * Contains functions related to student MOTD.
 */
var StudentMotd = {

    fetchMotd: function(motdUrl, motdContentSelector, motdContainerSelector) {
        $.ajax({
            type: 'GET',
            url: motdUrl,
            success: function(data) {
                $(motdContentSelector).html(data);
            },
            error: function() {
                $(motdContainerSelector).html('');
            }
        });
    },

    bindCloseMotdButton: function(btnSelector, motdContainerSelector) {
        $(document).on('click', btnSelector, function() {
            $(motdContainerSelector).hide();
        });
    }

};
