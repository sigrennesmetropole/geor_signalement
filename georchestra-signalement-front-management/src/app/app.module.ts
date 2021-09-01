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


// Data Sources
import {WorkflowDataSource} from './workflow/workflow.datasource';
import {UserDataSource} from './user/user.datasource';

// Dialogs PopIn
import {DialogLanguageSelectionDialog}
  from './language-selection/language-selection';
import {WorkflowAddDialog}
  from './workflow/workflow-add-dialog/workflow-add-dialog';
import {WorkflowDeleteDialog}
  from './workflow/workflow-delete-dialog/workflow-delete-dialog';
import {DialogUserDeleteDialog}
  from './user/user-delete-dialog/user-delete-dialog';
import {DialogUserAddDialog}
  from './user/user-add-dialog/user-add-dialog';

// Services
import {AdministrationService, UserService} from './api/services';
import {WorkflowService} from './services/workflow.service';
import {IsSignalementAdmin} from './guards/access.guard';
import {ForbiddenComponent} from './forbidden/forbidden.component';
import {AccessService} from './services/access.service';

// Utils
import {ToasterUtil} from './utils/toaster.util';

// Mappers
import {WorkflowItemMapper} from './mappers/workflow-item.mapper';
import {UserItemMapper} from './mappers/user-item.mapper';

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
    DialogLanguageSelectionDialog,
    WorkflowAddDialog,
    WorkflowDeleteDialog,
    DialogUserAddDialog,
    DialogUserDeleteDialog,
    ForbiddenComponent,
    ErrorComponent,
    UserComponent,
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
    WorkflowItemMapper,
    UserItemMapper,
    UserService,
    ToasterUtil,
    UserService,
    WorkflowDataSource,
    UserDataSource,
    WorkflowService,
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
