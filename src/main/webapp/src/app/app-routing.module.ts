import {Page404Component} from './authentication/page404/page404.component';
import {AuthLayoutComponent} from './layout/app-layout/auth-layout/auth-layout.component';
import {MainLayoutComponent} from './layout/app-layout/main-layout/main-layout.component';
import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {AuthGuard} from './core/guard/auth.guard';
import {Role} from './core/models/security/role';

const routes: Routes = [
    {
    path: '',
    component: MainLayoutComponent,
    canActivate: [AuthGuard],
    children: [
        { path: '', redirectTo: '/authentication/signin', pathMatch: 'full' },
        {
            path: 'organisation',
            canActivate: [AuthGuard],
            data: {
                roles: [Role.SUPER, Role.ADMIN, Role.ORGANISER]
            },
            loadChildren: () =>
                import('./organisation/organisation.module').then((m) => m.OrganisationModule),
        },
        {
            path: 'partnership',
            canActivate: [AuthGuard],
            data: {
                roles: [Role.SUPER, Role.PARTNER]
            },
            loadChildren: () =>
                import('./partnership/partnership.module').then((m) => m.PartnershipModule),
        },
        {
            path: 'configuration',
            canActivate: [AuthGuard],
            data: {
                roles: [Role.SUPER, Role.ADMIN]
            },
            loadChildren: () =>
                import('./configuration/configuration.module').then((m) => m.ConfigurationModule),
        },





        {
            path: 'admin',
            canActivate: [AuthGuard],
            data: {
                roles: [Role.SUPER, Role.ADMIN]
            },
            loadChildren: () =>
                import('./admin/admin.module').then((m) => m.AdminModule),
        },
        {
            path: 'teacher',
            canActivate: [AuthGuard],
            data: {
            role: Role.TRAINER,
            },
            loadChildren: () =>
            import('./teacher/teacher.module').then((m) => m.TeacherModule),
        },
        {
            path: 'student',
            canActivate: [AuthGuard],
            data: {
            role: Role.ATTENDEE,
            },
            loadChildren: () =>
            import('./student/student.module').then((m) => m.StudentModule),
        },

        // Extra components
        {path: 'calendar', loadChildren: () => import('./calendar/calendar.module').then((m) => m.CalendarsModule)},

        {
        path: 'task',
        loadChildren: () =>
        import('./task/task.module').then((m) => m.TaskModule),
        },
        {
        path: 'contacts',
        loadChildren: () =>
        import('./contacts/contacts.module').then((m) => m.ContactsModule),
        },
        {
        path: 'email',
        loadChildren: () =>
        import('./email/email.module').then((m) => m.EmailModule),
        },
        {
        path: 'apps',
        loadChildren: () =>
        import('./apps/apps.module').then((m) => m.AppsModule),
        },
        {
        path: 'widget',
        loadChildren: () =>
        import('./widget/widget.module').then((m) => m.WidgetModule),
        },
        {
        path: 'ui',
        loadChildren: () => import('./ui/ui.module').then((m) => m.UiModule),
        },
        {
        path: 'forms',
        loadChildren: () =>
        import('./forms/forms.module').then((m) => m.FormModule),
        },
        {
        path: 'tables',
        loadChildren: () =>
        import('./tables/tables.module').then((m) => m.TablesModule),
        },
        {
        path: 'media',
        loadChildren: () =>
        import('./media/media.module').then((m) => m.MediaModule),
        },
        {
        path: 'charts',
        loadChildren: () =>
        import('./charts/charts.module').then((m) => m.ChartsModule),
        },
        {
        path: 'timeline',
        loadChildren: () =>
        import('./timeline/timeline.module').then((m) => m.TimelineModule),
        },
        {
        path: 'icons',
        loadChildren: () =>
        import('./icons/icons.module').then((m) => m.IconsModule),
        },
        {
        path: 'extra-pages',
        loadChildren: () =>
        import('./extra-pages/extra-pages.module').then(
        (m) => m.ExtraPagesModule
        ),
        },
        {
        path: 'maps',
        loadChildren: () =>
        import('./maps/maps.module').then((m) => m.MapsModule),
        },
        {
        path: 'multilevel',
        loadChildren: () =>
        import('./multilevel/multilevel.module').then(
        (m) => m.MultilevelModule
        ),
        },
    ],
    },
    {
        path: 'authentication',
        component: AuthLayoutComponent,
        loadChildren: () =>
            import('./authentication/authentication.module').then((m) => m.AuthenticationModule),
    },
    { path: '**', component: Page404Component },
    ];

@NgModule({
    imports: [RouterModule.forRoot(routes, { relativeLinkResolution: 'legacy' })],
    exports: [RouterModule],
})

export class AppRoutingModule {}
