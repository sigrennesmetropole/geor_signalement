import {DataSource} from '@angular/cdk/collections';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {map} from 'rxjs/operators';
import {Observable, of as observableOf,
  merge, Subject} from 'rxjs';
import {WorkflowService} from '../services/workflow.service';
import {Injectable} from '@angular/core';
import {ToasterUtil} from '../utils/toaster.util';
import {TranslateService} from '@ngx-translate/core';


export interface WorkflowItem {
  name: string;
  version: number;
  key: string;
  resourceName: string;
  description: string;
}

@Injectable()
/**
* Data source for the Workflow view. This class should
* encapsulate all logic for fetching and manipulating the displayed data
* (including sorting, pagination, and filtering).
*/
export class WorkflowDataSource extends DataSource<WorkflowItem> {
  paginator: MatPaginator | undefined;
  sort: MatSort | undefined;
  data: WorkflowItem[]=[];

  actualize: Subject<void> = new Subject<void>();

  /**
  * DataSource constructor
  * @param {WorkflowService} workflowService to interract with workflows
  * @param {ToasterUtil} toasterService to display messages
  * @param {TranslateService} translateService to translate
  */
  constructor(private workflowService: WorkflowService,
    private toasterService:ToasterUtil,
    public translateService: TranslateService) {
    super();

    this.refreshData();
  }

  /**
    * Connect this data source to the table. The table will only update when
    * the returned stream emits new items.
    * @return {Observable<WorkflowItem[]>} A stream of the items to be rendered.
    */
  connect(): Observable<WorkflowItem[]> {
    if (this.paginator && this.sort) {
      // Combine everything that affects the rendered data into one update
      // stream for the data-table to consume.
      return merge(observableOf(this.data), this.paginator.page,
          this.sort.sortChange, this.actualize)
          .pipe(map(() => {
            return this.getPagedData(this.getSortedData([...this.data]));
          }));
    } else {
      throw Error('Please set paginator and sort on the data source.');
    }
  }

  /**
    *  Called when the table is being destroyed. Use this function, to clean up
    * any open connections or free any held resources that were set up
    * during connect.
    */
  disconnect(): void {}

  /**
    * Paginate the data (client-side). If you're using server-side pagination,
    * this would be replaced by requesting the appropriate data from the server.
    * @param {WorkflowItem[]} data Data to paginate
    * @return {WorkflowItem[]} The paged data
    */
  private getPagedData(data: WorkflowItem[]): WorkflowItem[] {
    if (this.paginator) {
      const startIndex = this.paginator.pageIndex * this.paginator.pageSize;
      return data.slice(startIndex, startIndex+this.paginator.pageSize);
    } else {
      return data;
    }
  }

  /**
    * Sort the data (client-side). If you're using server-side sorting,
    * this would be replaced by requesting the appropriate data from the server.
    * @param {WorkflowItem[]} data Data to sort
    * @return {WorkflowItem} Sorted data
    */
  private getSortedData(data: WorkflowItem[]): WorkflowItem[] {
    if (!this.sort) {
      return data;
    }
    if (this.sort.direction === '' || !this.sort.active) {
      return data.sort((a, b)=>this.compareName(a, b, true));
    }

    return data.sort((a, b) => {
      const isAsc = this.sort?.direction === 'asc';
      switch (this.sort?.active) {
        case 'name': return this.compareName(a, b, isAsc);
        case 'resourceName': return this.compareResourceName(a, b, isAsc);
        case 'key': return this.compareKey(a, b, isAsc);
        default: return 0;
      }
    });
  }

  /**
    * Refresh the data in our datasource
    * At the end emit an event to update the table
    */
  refreshData(): void {
    this.workflowService
        .getWorkflows()
        .subscribe(
            (data)=>{
              this.data = data;
            },
            (response)=>{
              this.toasterService
                  .sendErrorMessage('common.genericError',
                      response.error.code);
            },
            ()=>{
              this.paginator?.firstPage();
              this.actualize.next();
            },
        );
  }

  /**
      * Delete a processDefinition on the user's ask
      * by calling the WorkflowService
      * @param {WorkfowItem} target The item asked to be delete
      */
  deleteProcessDefinition(target : WorkflowItem) : void {
    this.workflowService
        .deleteProcessDefinition(target)
        .subscribe(
            (result)=>{
              if (result) {
                this.toasterService
                    .sendSuccessMessage('workflow.delete.workflow.success');
              } else {
                this.toasterService
                    .sendWarningMessage(
                        'workflow.delete.workflow.partialError');
              }
            },
            (response)=>{
              this
                  .toasterService
                  .sendErrorMessage('workflow.delete.workflow.error',
                      response.error.code);
            },
            ()=>{
              this.refreshData();
            },
        );
  }

  /**
        * Delete a processDefinition on the user's ask
        * by calling the WorkflowService
        * @param {WorkflowItem} target The item asked to be delete
        */
  deleteProcessDefinitionVersion(target : WorkflowItem) : void {
    this.workflowService
        .deleteProcessDefinitionVersion(target)
        .subscribe(
            (result)=>{
              if (result) {
                this.toasterService
                    .sendSuccessMessage('workflow.delete.version.success');
              } else {
                this.toasterService
                    .sendErrorMessage('workflow.delete.version.usedError');
              }
            },
            (response)=>{
              this.toasterService
                  .sendErrorMessage('workflow.delete.version.error',
                      response.error.code);
            },
            ()=>{
              this.refreshData();
            },
        );
  }

  /**
          * Upload a processDefinition on the user's ask
          * by calling the WorkflowService
          * @param {Blob} file The file to upload
          * @param {string} deploymentName The deploymentName of the file
          */
  updateProcessDefinition(file:Blob, deploymentName: string) : void {
    this.workflowService
        .updateProcessDefinition(file, deploymentName)
        .subscribe(
            (result)=>{
              if (result) {
                this.toasterService.sendSuccessMessage('workflow.add.success');
              } else {
                this.toasterService.sendErrorMessage('workflow.add.error');
              }
            },
            (response)=>{
              this.toasterService
                  .sendErrorMessage('workflow.add.error',
                      response.error.code);
            },
            ()=>{
              this.refreshData();
            },
        );
  }

  /**
            * Simple sort
            * @param {string|number} a
            * @param {string|number} b
            * @param {boolean} isAsc
            * @return {number}
            */
  compare(a: string | number, b: string | number, isAsc: boolean): number {
    if (a == b) {
      return 0;
    }
    return (a < b ? -1 : 1) * (isAsc ? 1 : -1);
  }
  /**
            * Compare WorkflowItem by name
            * @param {WorkflowItem} a
            * @param {WorkflowItem} b
            * @param {boolean} isAsc
            * @return {number}
            */
  compareName(a: WorkflowItem, b:WorkflowItem, isAsc:boolean) : number {
    const result = this.compare(a.name, b.name, isAsc);

    return (result !=0 ? result: this.compareVersion(a, b));
  }
  /**
            * Compare WorkflowItem by key
            * @param {WorkflowItem} a
            * @param {WorkflowItem} b
            * @param {boolean} isAsc
            * @return {number}
            */
  compareKey(a: WorkflowItem, b:WorkflowItem, isAsc:boolean) : number {
    const result = this.compare(a.key, b.key, isAsc);

    return (result !=0 ? result: this.compareName(a, b, isAsc));
  }

  /**
   * Compare WorkflowItem by ResourceName
   * @param {WorkflowItem} a
   * @param {WorkflowItem} b
   * @param {boolean} isAsc
   * @return {number}
   */
  compareResourceName(a: WorkflowItem, b:WorkflowItem, isAsc:boolean) : number {
    const result = this.compare(a.resourceName, b.resourceName, isAsc);

    return (result !=0 ? result: this.compareName(a, b, isAsc));
  }

  /**
            * Compare WorkflowItem by version
            * @param {WorkflowItem} a
            * @param {WorkflowItem} b
            * @return {number}
            */
  compareVersion(a: WorkflowItem, b:WorkflowItem) : number {
    return this.compare(a.version, b.version, true);
  }
}


