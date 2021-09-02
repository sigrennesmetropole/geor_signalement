import {NgModule} from '@angular/core';
import {Route, RouterModule} from '@angular/router';
import {ContextComponent} from './context/context.component';
import {IsSignalementAdmin} from './guards/access.guard';
import {HomePageComponent} from './home-page/home-page.component';
import {UserComponent} from './user/user.component';
import {WorkflowComponent} from './workflow/workflow.component';

const appRoutes: Route[] = [
  {path: 'contexts',
    component: ContextComponent,
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
