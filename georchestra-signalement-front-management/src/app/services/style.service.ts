import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {StylesContainerService} from '../api/services';
import {StrictHttpResponse} from '../api/strict-http-response';
import {StylePageResult} from '../api/models';


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
    constructor(private styleService: StylesContainerService) {
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
}