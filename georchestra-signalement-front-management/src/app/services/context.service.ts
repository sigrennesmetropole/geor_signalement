import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {ContextDescriptionPageResult, ContextType, GeographicType}
  from '../api/models';
import {ContextDescriptionsService} from '../api/services';
import {StrictHttpResponse} from '../api/strict-http-response';
import {ContextItem} from '../context/context.datasource';
import {ContextItemMapper} from '../mappers/context-item.mapper';


@Injectable({
  providedIn: 'root',
})
/**
 * ContextService is used to manipulate contexts
 */
export class ContextService {
  /**
   * Constructor of contextService
   * @param {ContextDescriptionsService} contextService
   * @param {ContextItemMapper} contextItemMapper
   */
  constructor(private contextService: ContextDescriptionsService,
    private contextItemMapper : ContextItemMapper) {

  }

  /**
   * Get a context by is name
   * @param {string} name Name of the context
   * @return {Observable<ContextItem>}
   */
  getContext(name : string) : Observable<ContextItem> {
    return this.contextService
        .getContextDescription(name).pipe(map(
            this.contextItemMapper.contextDescriptionToContextItem,
        ));
  }

  /**
   * Get contexts, in pages
   * @param {string} label
   * @param {string} workflow
   * @param {number} offset start index
   * @param {number} limit number of elements to get
   * @param {string} sortExpression sort Expression
   * @return {Observable<StrictHttpResponse<ContextDescription[]>>}
   */
  getContexts(label ?: string,
      workflow ?: string,
      offset ?: number,
      limit?: number,
      sortExpression ?: string) :
      Observable<StrictHttpResponse<ContextDescriptionPageResult>> {
    return this.contextService
        .searchContextDescriptionsResponse(
            {
              description: label,
              workflow: workflow,
              offset: offset,
              limit: limit,
              sortExpression: sortExpression,
            },
        );
  }
  /**
 * Delete context
 * @param {ContextItem} context
 * @return {Observable<boolean>}
 */
  deleteContext(context: ContextItem) : Observable<null> {
    return this.contextService.deleteContextDescription(context.name);
  }

  /**
   * Update a context
   * @param {ContextItem} context
   * @param {any} data
   * @return {Observable<ContextItem>}
   */
  updateContext(context: ContextItem, data: any) : Observable<ContextItem> {
    context.processDefinition = data.process;
    context.version = data.version;
    context.label = data.label;
    return this.contextService
        .updateContextDescription(this.contextItemMapper
            .contextItemToContextDescription(context))
        .pipe(map(this.contextItemMapper.contextDescriptionToContextItem));
  }


  /**
   * Post a context
   * @param {string} name
   * @param {string} label
   * @param {ContextType} contextType
   * @param {GeographicType} geographicType
   * @param {string} processDefinition
   * @param {number} version
   * @return {Observable<ContextItem>}
   */
  postContext(name : string, label: string, contextType: ContextType,
      geographicType: GeographicType, processDefinition: string,
      version: number) : Observable<ContextItem> {
    const contextItem : ContextItem = {
      name, label, contextType, geographicType, processDefinition, version,
    };


    return this.contextService
        .createContextDescription(this.contextItemMapper
            .contextItemToContextDescription(contextItem))
        .pipe(map(this.contextItemMapper.contextDescriptionToContextItem));
  }
}
