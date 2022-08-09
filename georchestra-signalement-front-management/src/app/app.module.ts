// Angular
import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {LayoutModule} from '@angular/cdk/layout';
import {HttpClient, HttpClientModule} from '@angular/common/http';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {AppRoutingModule} from './app-routing.module';

// Components
import {AppComponent} from './app.component';
import {HomePageComponent} from './home-page/home-page.component';
import {WorkflowComponent} from './workflow/workflow.component';
import {RoleComponent} from './role/role.component';
import {UserComponent} from './user/user.component';
import {ErrorComponent} from './error/error.component';
import {ContextComponent} from './context/context.component';
import {UserRoleContextComponent}
  from './userRoleContext/userRoleContext.component';


// Data Sources
import {WorkflowDataSource} from './workflow/workflow.datasource';
import {UserDataSource} from './user/user.datasource';
import {ContextDataSource} from './context/context.datasource';
import {RoleDataSource} from './role/role.datasource';
import {UserRoleContextDataSource}
  from './userRoleContext/userRoleContext.datasource';

// Dialogs PopIn
import {LanguageSelectionDialog}
  from './language-selection/language-selection';
import {WorkflowAddDialog}
  from './workflow/workflow-add-dialog/workflow-add-dialog';
import {WorkflowDeleteDialog}
  from './workflow/workflow-delete-dialog/workflow-delete-dialog';
import {UserDeleteDialog}
  from './user/user-delete-dialog/user-delete-dialog';
import {UserAddDialog}
  from './user/user-add-dialog/user-add-dialog';
import {ContextDeleteDialog}
  from './context/context-delete-dialog/context-delete-dialog';
import {ContextAddDialog}
  from './context/context-add-dialog/context-add-dialog';
import {ContextEditDialog}
  from './context/context-edit-dialog/context-edit-dialog';
import {RoleAddDialog}
  from './role/role-add-dialog/role-add-dialog';
import {RoleDeleteDialog}
  from './role/role-delete-dialog/role-delete-dialog';
import {UserRoleContextAddDialog} from
  './userRoleContext/userRoleContext-add-dialog/userRoleContext-add-dialog';
import {UserRoleContextDeleteDialog}
  from './userRoleContext/userRoleContext-delete-dialog/userRoleContext-delete-dialog';
import {ConfirmUserRoleContextDeleteDialog}
  from './userRoleContext/userRoleContext-delete-dialog/confirm-delete-dialog/confirmUserRoleContext-delete-dialog';


// Services
import {AdministrationService, UserRoleContextService, UserService}
  from './api/services';
import {WorkflowService} from './services/workflow.service';
import {IsSignalementAdmin} from './guards/access.guard';
import {ForbiddenComponent} from './forbidden/forbidden.component';
import {AccessService} from './services/access.service';
import {UserItemService} from './services/user.service';
import {ContextService} from './services/context.service';
import {RoleService} from './services/role.service';

// Utils
import {ToasterUtil} from './utils/toaster.util';

// Mappers
import {WorkflowItemMapper} from './mappers/workflow-item.mapper';
import {UserItemMapper} from './mappers/user-item.mapper';
import {ContextItemMapper} from './mappers/context-item.mapper';

// Material
import {MatButtonModule} from '@angular/material/button';
import {MatButtonToggleModule} from '@angular/material/button-toggle';
import {MatDialogModule} from '@angular/material/dialog';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatGridListModule} from '@angular/material/grid-list';
import {MatIconModule} from '@angular/material/icon';
import {MatInputModule} from '@angular/material/input';
import {MatListModule} from '@angular/material/list';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatSelectModule} from '@angular/material/select';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatTableModule} from '@angular/material/table';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {MatSortModule} from '@angular/material/sort';
import {MatSelectInfiniteScrollModule} from 'ng-mat-select-infinite-scroll';


// Translation imports
import {TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {TranslateHttpLoader} from '@ngx-translate/http-loader';
import { ContextMapDialog } from './context/context-map-dialog/context-map-dialog.component';
import { MapInfo } from './context/context-map-dialog/map-info/map-info.component';
import {StyleComponent} from './style/style.component';
import {StyleDataSource} from "./style/style.datasource";
import {StyleDeleteDialog} from "./style/style-delete-dialog/style-delete-dialog";
import {StyleProcessDialog} from "./style/style-process-dialog/style-process-dialog";
import {StyleProcessAddDialog} from "./style/style-process-dialog/style-process-add-dialog/style-process-add-dialog";
import {StyleProcessDeleteDialog} from "./style/style-process-dialog/style-process-delete-dialog/style-process-delete-dialog";
import {StyleDialog} from "./style/style-dialog/style-dialog";
import {PointDialog} from "./style/style-dialog/style-type-dialog/point-dialog";
import {LineDialog} from "./style/style-dialog/style-type-dialog/line-dialog";
import {PolygonDialog} from "./style/style-dialog/style-type-dialog/polygon-dialog";

@NgModule({
  declarations: [
    AppComponent,
    HomePageComponent,
    WorkflowComponent,
    RoleComponent,
    UserRoleContextComponent,
    LanguageSelectionDialog,
    WorkflowAddDialog,
    WorkflowDeleteDialog,
    UserAddDialog,
    UserDeleteDialog,
    ContextDeleteDialog,
    ContextAddDialog,
    ContextEditDialog,
    RoleAddDialog,
    RoleDeleteDialog,
    UserRoleContextAddDialog,
    UserRoleContextDeleteDialog,
    ConfirmUserRoleContextDeleteDialog,
    ForbiddenComponent,
    ErrorComponent,
    UserComponent,
    ContextComponent,
    StyleDialog,
    PointDialog,
    LineDialog,
    PolygonDialog,
    StyleDeleteDialog,
    StyleComponent,
    StyleProcessDialog,
    StyleProcessAddDialog,
    StyleProcessDeleteDialog,
    ContextMapDialog,
    MapInfo,
  ],
  imports: [
    AppRoutingModule,
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    LayoutModule,
    MatButtonModule,
    MatButtonToggleModule,
    MatDialogModule,
    MatFormFieldModule,
    MatGridListModule,
    MatIconModule,
    MatInputModule,
    MatListModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatSelectModule,
    MatSelectInfiniteScrollModule,
    MatSidenavModule,
    MatSnackBarModule,
    MatSortModule,
    MatTableModule,
    MatToolbarModule,
    ReactiveFormsModule,
    HttpClientModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient],
      },
    }),
  ],
  providers: [
    AccessService,
    AdministrationService,
    ContextService,
    UserItemService,
    UserService,
    WorkflowService,
    RoleService,
    UserRoleContextService,
    ContextItemMapper,
    UserItemMapper,
    WorkflowItemMapper,
    UserItemMapper,
    UserService,
    ToasterUtil,
    ContextDataSource,
    RoleDataSource,
    UserDataSource,
    UserRoleContextDataSource,
    WorkflowDataSource,
    IsSignalementAdmin,
  ],
  bootstrap: [AppComponent],
})

/**
 * Our App module
 */
export class AppModule { }

/**
 * HttpLoaderFactory
 * @param {HttpClient} http The httpClient
 * @return {TranslateHttpLoader} The TranslateHttpLoader
 */
export function HttpLoaderFactory(http: HttpClient): TranslateHttpLoader {
  return new TranslateHttpLoader(http);
}
