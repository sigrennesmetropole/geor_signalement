import {DataSource} from '@angular/cdk/collections';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {delayWhen, map, tap} from 'rxjs/operators';
import {Observable,
    merge, Subject, of} from 'rxjs';
import {Injectable} from '@angular/core';
import {ToasterUtil} from '../utils/toaster.util';
import {TranslateService} from '@ngx-translate/core';
import {GeographicType, Style, StyleContainer, ProcessStyling} from '../api/models';
import {StyleService} from '../services/style.service';
import {StyleProcessDialog} from "./style-process-dialog/style-process-dialog";


export interface StyleItem {
    name: string;
    type: GeographicType;

}
@Injectable()
/**
 * Data source for the Role view. This class should
 * encapsulate all logic for fetching and manipulating the displayed data
 * (including sorting, pagination, and filtering).
 */
export class StyleDataSource extends DataSource<StyleContainer> {
    paginator: MatPaginator | undefined;
    sort: MatSort | undefined;
    data: StyleContainer[]=[];
    sortCriteria:string = '';
    totalItems:number=0;

    actualize: Subject<void> = new Subject<void>();

    /**
     * DataSource constructor
     * @param {roleService} roleService to interract with roles
     * @param {ToasterUtil} toasterService to display messages
     * @param {TranslateService} translateService to translate
     */
    constructor(private styleService: StyleService,
                private toasterService:ToasterUtil,
                public translateService: TranslateService) {
        super();

        this.loadData().pipe(tap(()=>{
            this.actualize.next();
        }));
    }

    /**
     * Connect this data source to the table. The table will only update when
     * the returned stream emits new items.
     * @return {Observable<StyleContainer[]>} A stream of the items to be rendered.
     */
    connect(): Observable<StyleContainer[]> {
        if (this.paginator && this.sort) {
            // Combine everything that affects the rendered data into one update
            // stream for the data-table to consume.
            return merge(of(this.data), this.paginator.page,
                this.sort.sortChange, this.actualize)
                .pipe(tap(() => {
                    this.sortedParameters();
                }))
                .pipe(delayWhen((event)=>this.loadData()))
                .pipe(map(()=>{
                    return this.data;
                }));
        } else {
            throw Error('Please set paginator and sort on the data source.');
        }
    }

    /**
     *  Called when the table is being destroyed. Use this function,
     * to clean up any open connections or free any held resources
     * that were set up during connect.
     */
    disconnect(): void {}


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
                case 'id': {
                    this.sortCriteria+='id';
                    break;
                }
                case 'name': {
                    this.sortCriteria+='name';
                    break;
                }
                //difficult to sort on type, because type isn't existing in StylingEntity.
                /*case 'type': {
                    this.sortCriteria+='type';
                    break;
                }*/
                default: this.sortCriteria='';
            }
        }
    }

    /**
     * Refresh the data in our datasource
     * @return {Observable<Role[]>} return an observable on data
     */
    private loadData(): Observable<StyleContainer[]> {
        return this.styleService
            .getStyles(this.paginator?.pageIndex ?? 0,
                this.paginator?.pageSize ?? 10,
                this.sortCriteria)
            .pipe(map(
                (response)=>{
                    if (response.body) {
                        this.data = response.body.results ?? [];
                        this.totalItems = response.body.totalItems ?? 0;
                    }
                    return this.data;
                }),
            );
    }


    /**
     * Function called to ask a refresh of data
     */
    public refreshData() : void {
        this.paginator?.firstPage();
        this.actualize.next();
    }

    updateStyle(style: StyleContainer, data: any) {
        this.styleService
            .updateStyle(style, data)
            .subscribe(
                ()=>{
                    this.toasterService
                        .sendSuccessMessage('style.update.success');
                },
                (response)=>{
                    this.toasterService
                        .sendErrorMessage('style.update.error',
                            response.error.code);
                },
                ()=>{
                    this.refreshData();
                },);
    }
    deleteStyle(target: StyleContainer) {
        this.styleService.deleteStyle(target).subscribe(
            ()=>{
                this.toasterService
                    .sendSuccessMessage('style.delete.success');
            },
            (response)=>{
                this.toasterService
                    .sendErrorMessage('style.delete.error',
                        response.error.code);
            },
            ()=>{
                this.refreshData();
            },);
    }

    public postStyle(style :StyleContainer): void{
        this.styleService
            .postStyle(style).subscribe(
            (result)=>{
                if (result) {
                    this.toasterService.sendSuccessMessage('style.add.success');
                } else {
                    this.toasterService.sendErrorMessage('style.add.error');
                }
            },
            (response)=>{
                this.toasterService
                    .sendErrorMessage('style.add.error',
                        response.error.code);

            },

            ()=>{
                this.refreshData();
            },
        );
    }

    public postStyleProcess(processStyling: ProcessStyling, p: StyleProcessDialog): void{
        this.styleService
            .postStyleProcess(processStyling).subscribe(
            (result)=>{
                if (result) {
                    this.toasterService.sendSuccessMessage('style.add.success');
                } else {
                    this.toasterService.sendErrorMessage('style.add.error');
                }
            },
            (response)=>{
                this.toasterService
                    .sendErrorMessage('style.process.add.error',
                        response.error.code);
            },
            ()=>{
                p.refreshData();
            },
        );
    }

    deleteStyleProcess(target: ProcessStyling, p: StyleProcessDialog) {
        this.styleService.deleteStyleProcess(target).subscribe(
            ()=>{
                this.toasterService
                    .sendSuccessMessage('style.process.delete.success');
            },
            (response)=>{
                this.toasterService
                    .sendErrorMessage('style.process.delete.error',
                        response.error.code);
            },
            ()=>{
                p.refreshData();
            },);
    }
}

