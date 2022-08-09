import {AfterViewInit, Component, ViewChild} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatTable} from '@angular/material/table';
import {TranslateService} from '@ngx-translate/core';
import {Observable, of} from 'rxjs';
import {WorkflowService} from '../services/workflow.service';
import {WorkflowItem} from '../workflow/workflow.datasource';
import {ContextAddDialog} from './context-add-dialog/context-add-dialog';
import {ContextDeleteDialog}
  from './context-delete-dialog/context-delete-dialog';
import {ContextEditDialog} from './context-edit-dialog/context-edit-dialog';
import {ContextDataSource, ContextItem} from './context.datasource';
import {ContextMapDialog} from "./context-map-dialog/context-map-dialog.component";


@Component({
  selector: 'app-context',
  templateUrl: './context.component.html',
  styleUrls: ['./context.component.scss'],
})
/**
 * Context component is used to list, add and delete context
 */
export class ContextComponent implements AfterViewInit {
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild(MatTable) table!: MatTable<ContextItem>;
  dataSource: ContextDataSource;

  /** Columns displayed in the table*/
  displayedColumns = ['name', 'label', 'contextType',
    'geographicType', 'processDefinition', 'version', 'actions'];

  workflows : WorkflowItem[] = [];

  labelFilter : string = '';
  workflowFilter : string = '';

  /**
   * Constructor for the context component
   * @param {ContextDataSource} contextDataSource Datasource for the component
   * @param {MatDialog} dialog The dialog component to open dialog windows
   */
  constructor(contextDataSource :ContextDataSource,
    public dialog: MatDialog,
    private workflowService : WorkflowService,
    private translateService: TranslateService) {
    this.dataSource = contextDataSource;
    this.workflowService.getWorkflows().subscribe(
        (data)=>{
          this.workflows = data ?? [];
        },
    );
    this.labelFilter = this.dataSource.labelFilter;
    this.workflowFilter = this.dataSource.workflowFilter;
  }

  /**
   * Actions after init
   */
  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
    this.table.dataSource = this.dataSource;
  }

  /**
   * When the user clicks on the delete button open the suprresion dialog
   * @param {ContextItem} target The ContextItem to delete
   */
  handleOpenSupressDialogClick(target: ContextItem): void {
    const dialog = this.dialog.open(ContextDeleteDialog, {
      width: 'auto',
      height: 'auto',
      data: target,
    },
    );
    dialog.afterClosed().subscribe((result)=>{
      if (result && result === 'Confirm') {
        this.dataSource.deleteContext(target);
      }
    });
  }

  /**
   * When the user clicks on the upload button, open the add Dialog
   */
  handleOpenAddDialogClick(): void {
    this.dialog.open(ContextAddDialog, {
      width: 'auto',
      height: 'auto',
      data: this.workflows,
    }).afterClosed().subscribe(
        (data)=>{
          if (data) {
            this.dataSource.postContext(data);
          }
        },
    );
  }

  /**
   * When the user clicks on the edit button, open the edit Dialog
   * @param {ContextItem} target
   */
  handleOpenEditDialogClick(target: ContextItem): void {
    this.dialog.open(ContextEditDialog, {
      width: 'auto',
      height: 'auto',
      data: {workflows: this.workflows,
        target: target},
        }).afterClosed().subscribe(
        (data)=>{
          if (data) {
            this.dataSource.updateContext(target, data);
          }
        },
    );
  }

  /**
   * When the user clicks on the refresh button, refresh data
   */
  handleRefreshDataClick(): void {
    this.dataSource.refreshData();
  }

  handleOpenMapDialogClick(target: ContextItem): void {
    this.dialog.open(ContextMapDialog, {
      width: 'auto',
      height: 'auto',
      panelClass: 'custom-map-dialog-container',
      data: {target: target},
    })
  }

  /**
   * Get a workflow name by his key
   * @param {string} workflowKey
   * @return {string}
   */
  getWorkflowName(workflowKey : string) : string {
    for (const workflow of this.workflows) {
      if (workflow.key == workflowKey) {
        return workflow.name;
      }
    }
    return '';
  }

  /**
   * Convert version 0 into traducted "last version"
   * else return the version
   * @param {number} version
   * @return {Observable<string>}
   */
  getVersion(version: number) : Observable<string> {
    if (version !=0) {
      return of(String(version));
    } else {
      return this.translateService.get('common.lastVersion');
    }
  }

  timeoutFilters:any;
  /**
   * Update search filters
   */
  updateFilters(): void {
    clearTimeout(this.timeoutFilters);
    this.timeoutFilters = setTimeout(()=>{
      this.dataSource.updateFilters(this.labelFilter, this.workflowFilter);
    }, 250);
  }
}
