import {ContextDescription} from '../api/models';
import {ContextItem} from '../context/context.datasource';

/**
 * ContextItemMapper to concert Item to Object
 * and Object to Item
 */
export class ContextItemMapper {
  /**
   * constructor
   */
  constructor() {}

  /**
   * Convert a ContextDescription into a ContextItem
   * @param {ContextDescription} contextDescription
   * @return {ContextItem}
   */
  contextDescriptionToContextItem(contextDescription : ContextDescription)
  : ContextItem {
    return {
      name: contextDescription.name ?? '',
      label: contextDescription.label ?? '',
      contextType: contextDescription.contextType,
      geographicType: contextDescription.geographicType,
      processDefinition: contextDescription.processDefinitionKey ?? '',
      version: contextDescription.revision ?? 0,
    };
  }

  /**
   * Convert a ContextItem into a ContextDescription
   * @param {ContextItem} contextItem
   * @return {ContextDescription}
   */
  contextItemToContextDescription(contextItem : ContextItem)
  : ContextDescription {
    return {
      name: contextItem.name,
      label: contextItem.label,
      contextType: contextItem.contextType,
      geographicType: contextItem.geographicType,
      processDefinitionKey: contextItem.processDefinition,
      revision: contextItem.version,
    };
  }
}
