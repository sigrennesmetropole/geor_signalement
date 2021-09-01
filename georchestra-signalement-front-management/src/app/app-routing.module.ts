import {NgModule} from '@angular/core';
import {Route, RouterModule} from '@angular/router';
import {IsSignalementAdmin} from './guards/access.guard';
import {HomePageComponent} from './home-page/home-page.component';
import {RoleComponent} from './role/role.component';
import {UserComponent} from './user/user.component';
import {WorkflowComponent} from './workflow/workflow.component';

const appRoutes: Route[] = [
  {path: 'roles',
    component: RoleComponent,
    canActivate: [
      IsSignalementAdmin,
    ]},

  {path: 'users',
    component: UserComponent,
    canActivate: [
      IsSignalementAdmin,
    ]},

  {path: 'workflows',
    component: WorkflowComponent,
    canActivate: [
      IsSignalementAdmin,
    ]},
  {path: '',
    component: HomePageComponent,
    canActivate: [
      IsSignalementAdmin,
    ]},
  {
    path: '**',
    redirectTo: '',
  },
];

@NgModule({
  imports: [RouterModule.forRoot(appRoutes)],
  exports: [RouterModule],
})
/**
* Routing module
*/
export class AppRoutingModule { }
