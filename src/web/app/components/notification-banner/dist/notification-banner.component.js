"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
exports.__esModule = true;
exports.NotificationBannerComponent = void 0;
var core_1 = require("@angular/core");
var api_output_1 = require("../../../types/api-output");
var collapse_anim_1 = require("../teammates-common/collapse-anim");
/**
 * Banner used to display notifications to the user.
 */
var NotificationBannerComponent = /** @class */ (function () {
    function NotificationBannerComponent(notificationService, statusMessageService) {
        this.notificationService = notificationService;
        this.statusMessageService = statusMessageService;
        this.url = '';
        this.notificationTargetUser = api_output_1.NotificationTargetUser.GENERAL;
        this.isShown = true;
        this.notifications = [];
    }
    NotificationBannerComponent.prototype.ngOnInit = function () {
        if (this.notificationTargetUser !== api_output_1.NotificationTargetUser.GENERAL) {
            this.fetchNotifications();
        }
    };
    NotificationBannerComponent.prototype.ngOnChanges = function () {
        // Hide the notification banner if the user is on user notifications page
        if (this.url.includes('notifications')) {
            this.closeNotification();
        }
    };
    NotificationBannerComponent.prototype.fetchNotifications = function () {
        var _this = this;
        this.notificationService.getUnreadNotificationsForTargetUser(this.notificationTargetUser)
            .subscribe(function (response) {
            _this.notifications = response.notifications;
        });
    };
    NotificationBannerComponent.prototype.markNotificationAsRead = function (notification) {
        var _this = this;
        this.notificationService.markNotificationAsRead({
            notificationId: notification.notificationId,
            endTimestamp: notification.endTimestamp
        })
            .subscribe({
            next: function () {
                _this.statusMessageService.showSuccessToast('Notification marked as read.');
                _this.closeNotification();
            },
            error: function (resp) {
                _this.statusMessageService.showErrorToast(resp.error.message);
            }
        });
    };
    NotificationBannerComponent.prototype.closeNotification = function () {
        this.isShown = false;
    };
    NotificationBannerComponent.prototype.getButtonClass = function (notification) {
        return "btn btn-" + notification.style.toLowerCase();
    };
    __decorate([
        core_1.Input()
    ], NotificationBannerComponent.prototype, "url");
    __decorate([
        core_1.Input()
    ], NotificationBannerComponent.prototype, "notificationTargetUser");
    NotificationBannerComponent = __decorate([
        core_1.Component({
            selector: 'tm-notification-banner',
            templateUrl: './notification-banner.component.html',
            styleUrls: ['./notification-banner.component.scss'],
            animations: [collapse_anim_1.collapseAnim]
        })
    ], NotificationBannerComponent);
    return NotificationBannerComponent;
}());
exports.NotificationBannerComponent = NotificationBannerComponent;
