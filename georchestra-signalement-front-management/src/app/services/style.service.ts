import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {StylesService} from '../api/services';
import {StrictHttpResponse} from '../api/strict-http-response';
import {StyleContainer, ProcessStyling, StylePageResult} from '../api/models';


@Injectable({
    providedIn: 'root',
})
/**
 * Service to interact with API service to get styles
 */
export class StyleService {
    /**
     * Constructor of the StyleService
     * @param {StyleService} styleService The API service
     */
    constructor(private styleService: StylesService) {
    }

    /**
     * Get the roles
     * @param {number} offset
     * @param {number} limit
     * @param {string} sortCriteria
     * @return {Observable<StrictHttpResponse<Role[]>>}
     */
    getStyles(offset: number, limit: number, sortCriteria: string)
        : Observable<StrictHttpResponse<StylePageResult>> {
        return this.styleService.searchStyleResponse({
            offset: offset,
            limit: limit,
            sortExpression: sortCriteria
        });
    }

    updateStyle(style: StyleContainer, data: any) {
        if(style.type !=null && style.type.toString() == data.type.toString()){
            style.name = data.name;
            style.style = data.style;
        }
        return this.styleService
            .updateStyle(style);
    }

    postStyle(style:StyleContainer) : Observable<StyleContainer> {
        return this.styleService
            .createStyle(style);
    }


    deleteStyle(style: StyleContainer) : Observable<null> {
        return this.styleService.deleteStyle(style.id as number);
    }

    getListProcessStyling(styleId: number) : Observable<Array<ProcessStyling>>{
        return this.styleService.getStyleProcessById(styleId);
    }

    postStyleProcess(styleProcess: ProcessStyling) {
        return this.styleService
            .createProcessStyling(styleProcess);
    }

    deleteStyleProcess(style: ProcessStyling) : Observable<null> {
        return this.styleService.deleteProcessStyling(style.id as number);
    }
}