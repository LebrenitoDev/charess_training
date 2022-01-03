// Sidebar route metadata
export interface RouteInfo {
    path: string;
    title: string;
    moduleName: string;
    iconType: string;
    icon: string;
    class: string;
    groupTitle: boolean;
    badge: string;
    badgeClass: string;
    roles: string[];
    submenu: RouteInfo[];
}
