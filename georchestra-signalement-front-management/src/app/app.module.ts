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


// Data Sources
import {WorkflowDataSource} from './workflow/workflow.datasource';
import {UserDataSource} from './user/user.datasource';
import {ContextDataSource} from './context/context.datasource';
import {RoleDataSource} from './role/role.datasource';

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


// Services
import {AdministrationService, UserService} from './api/services';
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


// Translation imports
import {TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {TranslateHttpLoader} from '@ngx-translate/http-loader';


@NgModule({
  declarations: [
    AppComponent,
    HomePageComponent,
    WorkflowComponent,
    RoleComponent,
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
    ForbiddenComponent,
    ErrorComponent,
    UserComponent,
    ContextComponent,
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
    ContextItemMapper,
    UserItemMapper,
    WorkflowItemMapper,
    ToasterUtil,
    ContextDataSource,
    RoleDataSource,
    UserDataSource,
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
