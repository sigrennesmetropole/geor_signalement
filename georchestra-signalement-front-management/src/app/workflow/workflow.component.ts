import {AfterViewInit, Component, ViewChild} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatTable} from '@angular/material/table';
import {DialogWorkflowAddDialog}
  from './workflow-add-dialog/workflow-add-dialog';
import {WorkflowDataSource, WorkflowItem} from './workflow-datasource';
import {DialogWorkflowDeleteDialog}
  from './workflow-delete-dialog/workflow-delete-dialog';


@Component({
  selector: 'app-workflow',
  templateUrl: './workflow.component.html',
  styleUrls: ['./workflow.component.scss'],
})
/**
 * Workflow component is used to list, add and delete workflow
 */
export class WorkflowComponent implements AfterViewInit {
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild(MatTable) table!: MatTable<WorkflowItem>;
  dataSource: WorkflowDataSource;

  /** Columns displayed in the table*/
  displayedColumns = ['name', 'version', 'resourceName',
    'key', 'informations', 'actions'];

  /**
   * Constructor for the workflow component
   * @param {WorkflowDataSource} workflowDataSource Datasource for the component
   * @param {MatDialog} dialog The dialog component to open dialog windows
   */
  constructor(workflowDataSource :WorkflowDataSource,
    public dialog: MatDialog) {
    this.dataSource = workflowDataSource;
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
   * @param {WorkflowItem} target The WorkflowItem to delete
   */
  handleOpenSupressDialogClick(target: WorkflowItem): void {
    const dialog = this.dialog.open(DialogWorkflowDeleteDialog, {
      width: 'auto',
      height: 'auto',
      data: {
        dataSource: this.dataSource,
        target: target,
      },
    },
    );
    dialog.afterClosed().subscribe((result)=>{
      if (result && result === 'workflow') {
        this.dataSource.deleteProcessDefinition(target);
      } else if (result && result ==='version') {
        this.dataSource.deleteProcessDefinitionVersion(target);
      }
    });
  }

  /**
   * When the user clicks on the upload button, open the add Dialog
   */
  handleOpenAddDialogClick(): void {
    const dialog = this.dialog.open(DialogWorkflowAddDialog, {
      width: 'auto',
      height: 'auto',
      data: this.dataSource,
    });

    dialog.afterClosed().subscribe((data)=>{
      if (data) {
        this.dataSource
            .updateProcessDefinition(data.file, data.deploymentName);
      }
    });
  }

  /**
   * When the user clicks on the refresh button, refresh data
   */
  handleRefreshDataClick(): void {
    this.dataSource.refreshData();
  }
}
