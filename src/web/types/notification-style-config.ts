import { NotificationStyle } from './api-request';

enum NotificationFontColor {
    WHITE = 'text-white',
    DARK = 'text-dark',
}

interface NotificationStyleConfig {
    font: NotificationFontColor,
    description: string,
}

/**
 * Maps between notification style and actual class/description to be used.
 *
 * Ref: https://getbootstrap.com/docs/4.5/utilities/colors/#background-color
 */
export const notificationStyleConfigMap: Record<NotificationStyle, NotificationStyleConfig> = {
    PRIMARY: { font: NotificationFontColor.WHITE, description: 'Primary (blue)' },
    SECONDARY: { font: NotificationFontColor.WHITE, description: 'Secondary (grey)' },
    SUCCESS: { font: NotificationFontColor.WHITE, description: 'Success (green)' },
    DANGER: { font: NotificationFontColor.WHITE, description: 'Danger (red)' },
    WARNING: { font: NotificationFontColor.DARK, description: 'Warning (yellow)' },
    INFO: { font: NotificationFontColor.WHITE, description: 'Info (cyan)' },
    LIGHT: { font: NotificationFontColor.DARK, description: 'Light' },
    DARK: { font: NotificationFontColor.WHITE, description: 'Dark' },
};
