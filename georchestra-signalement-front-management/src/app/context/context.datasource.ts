import {DataSource} from '@angular/cdk/collections';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {delayWhen, map, tap} from 'rxjs/operators';
import {Observable, of as observableOf,
  merge, Subject} from 'rxjs';
import {ContextService} from '../services/context.service';
import {Injectable} from '@angular/core';
import {ToasterUtil} from '../utils/toaster.util';
import {TranslateService} from '@ngx-translate/core';
import {ContextType, GeographicType} from '../api/models';
import {ContextItemMapper} from '../mappers/context-item.mapper';


export interface ContextItem {
  name: string;
  label: string;
  contextType: ContextType|undefined;
  geographicType: GeographicType|undefined;
  processDefinition: string;
  version: number;
}

@Injectable()
/**
 * Data source for the Context view. This class should
 * encapsulate all logic for fetching and manipulating the displayed data
 * (including sorting, pagination, and filtering).
 */
export class ContextDataSource extends DataSource<ContextItem> {
  paginator: MatPaginator | undefined;
  sort: MatSort | undefined;
  data: ContextItem[]=[];
  totalItems:number = 0;
  sortCriteria:string='';

  labelFilter: string = '';
  workflowFilter: string = '';


  actualize: Subject<void> = new Subject<void>();

  /**
   * DataSource constructor
   * @param {ContextService} contextService to interract with contexts
   * @param {ToasterUtil} toasterService to display messages
   * @param {TranslateService} translateService to translate
   */
  constructor(private contextService: ContextService,
    private toasterService:ToasterUtil,
    public translateService: TranslateService,
    private contextMapper: ContextItemMapper) {
    super();

    this.refreshData();
  }

  /**
   * Connect this data source to the table. The table will only update when
   * the returned stream emits new items.
   * @return {Observable<ContextItem[]>} A stream of the items to be rendered.
   */
  connect(): Observable<ContextItem[]> {
    if (this.paginator && this.sort) {
      // Combine everything that affects the rendered data into one update
      // stream for the data-table to consume.
      return merge(observableOf(this.data), this.paginator.page,
          this.sort.sortChange, this.actualize)
          .pipe(
              tap(() => {
                this.sortedParameters();
              }),
              delayWhen((event)=>this.loadData()),
              map(()=>{
                return this.data;
              }),
          );
    } else {
      throw Error('Please set paginator and sort on the data source.');
    }
  }

  /**
   *  Called when the table is being destroyed. Use this function, to clean up
   * any open connections or free any held resources that were set up
   * during connect.
   */
  disconnect(): void {

  }

  /**
   * Set the parameter to be used for the sort (server-side)
   * of the data.
   */
  private sortedParameters(): void {
    this.sortCriteria = '';
    if (this.sort) {
      if (this.sort?.direction == 'desc') {
        this.sortCriteria = '-';
      }
      switch (this.sort?.active) {
        case 'name': {
          this.sortCriteria+='name';
          break;
        }
        case 'contextType': {
          this.sortCriteria+='contextType';
          break;
        }
        case 'geographicType': {
          this.sortCriteria+='geographicType';
          break;
        }
        default: this.sortCriteria='';
      }
    }
  }
  /**
   * Refresh the data in our datasource
   * @return {Observable<ContextItem[]>} return an observable on data
   */
  private loadData(): Observable<ContextItem[]> {
    return this.contextService
        .getContexts(this.labelFilter,
            this.workflowFilter,
            this.paginator?.pageIndex,
            this.paginator?.pageSize,
            this.sortCriteria)
        .pipe(map(
            (response)=>{
              this.totalItems = +(response.body.totalItems ?? 0);
              this.data = (response.body.results ?? [])
                  .map(this.contextMapper.contextDescriptionToContextItem);

              return this.data;
            }),
        );
  }

  /**
   * Function called to ask a refresh of data
   */
  refreshData(): void {
    this.paginator?.firstPage();
    this.actualize.next();
  }

  /**
   * Delete a context on the user's ask
   * by calling the ContextService
   * @param {ContextItem} target The item asked to be delete
   */
  deleteContext(target : ContextItem) : void {
    this.contextService
        .deleteContext(target)
        .subscribe(
            ()=>{
              this.toasterService
                  .sendSuccessMessage('context.delete.success');
            },
            (response)=>{
              this.toasterService
                  .sendErrorMessage('context.delete.error',
                      response.error.code);
            },
            ()=>{
              this.refreshData();
            },
        );
  }

  /**
   * Update a context
   * @param {ContextItem} context
   * @param {any} data Elements to update
   */
  updateContext(context: ContextItem, data:any) : void {
    this.contextService
        .updateContext(context, data)
        .subscribe(
            (result)=>{
              if (result) {
                this.toasterService
                    .sendSuccessMessage('context.update.success');
              } else {
                this.toasterService.sendErrorMessage('context.update.error');
              }
            },
            (response)=>{
              this.toasterService
                  .sendErrorMessage('context.update.error',
                      response.error.code);
            },
            ()=>{
              this.refreshData();
            },
        );
  }
  /**
   * Post a context
   * @param {ContextItem} context
   */
  postContext(context : ContextItem): void {
    if (!context.contextType || !context.geographicType) {
      this.toasterService.sendErrorMessage('common.genericError');
    } else {
      this.contextService
          .postContext(context.name, context.label,
              context.contextType, context.geographicType,
              context.processDefinition, context.version).subscribe(
              (result)=>{
                if (result) {
                  this.toasterService.sendSuccessMessage('context.add.success');
                } else {
                  this.toasterService.sendErrorMessage('context.add.error');
                }
              },
              (response)=>{
                this.toasterService
                    .sendErrorMessage('context.add.error',
                        response.error.code);
              },
              ()=>{
                this.refreshData();
              },
          );
    }
  }
  /**
     * Set the label and workflow filter and actualize data
     * @param {string} labelFilter
     * @param {string} workflowFilter
     */
  updateFilters(labelFilter: string, workflowFilter:string) : void {
    this.labelFilter = labelFilter;
    this.workflowFilter = workflowFilter;
    this.paginator?.firstPage();
    this.refreshData();
  }
}


