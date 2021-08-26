// Angular
import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {LayoutModule} from '@angular/cdk/layout';
import {HttpClient, HttpClientModule} from '@angular/common/http';
import {FormsModule} from '@angular/forms';

// Components
import {AppComponent} from './app.component';
import {HomePageComponent} from './home-page/home-page.component';
import {WorkflowComponent} from './workflow/workflow.component';
import {RoleComponent} from './role/role.component';
import {OperatorComponent} from './operator/operator.component';


import {WorkflowDataSource} from './workflow/workflow-datasource';

// Dialogs PopIn
import {DialogLanguageSelectionDialog}
  from './language-selection/language-selection';
import {DialogWorkflowAddDialog}
  from './workflow/workflow-add-dialog/workflow-add-dialog';
import {DialogWorkflowDeleteDialog}
  from './workflow/workflow-delete-dialog/workflow-delete-dialog';

// Services
import {AdministrationService, UserService} from './api/services';
import {WorkflowService} from './services/workflow.service';
import {IsSignalementAdmin} from './guards/access.guard';
import {ForbiddenComponent} from './forbidden/forbidden.component';
import {AccessService} from './services/access.service';

// Utils
import {ToasterUtil} from './utils/toaster.util';

// Material
import {MatButtonModule} from '@angular/material/button';
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
import {AppRoutingModule} from './app-routing.module';
import {WorkflowItemMapper} from './mappers/workflow-item.mapper';
import {ErrorComponent} from './error/error.component';


@NgModule({
  declarations: [
    AppComponent,
    HomePageComponent,
    WorkflowComponent,
    RoleComponent,
    OperatorComponent,
    DialogLanguageSelectionDialog,
    DialogWorkflowAddDialog,
    DialogWorkflowDeleteDialog,
    ForbiddenComponent,
    ErrorComponent,
  ],
  imports: [
    AppRoutingModule,
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    LayoutModule,
    MatButtonModule,
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
    ToasterUtil,
    UserService,
    WorkflowDataSource,
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
