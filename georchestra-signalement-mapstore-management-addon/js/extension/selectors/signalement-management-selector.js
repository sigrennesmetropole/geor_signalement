import { createSelector } from 'reselect';
import { get, head } from "lodash";
import {layersSelector} from '@mapstore/selectors/layers';

export const getSignalementManagement = state => get(state, "signalementManagement");

export const getSignalementManagementState = createSelector(
  [ getSignalementManagement ],
  (selector) => selector
);

export const isOpen = (state) => get(state, "signalementManagement.taskViewerOpen");

export const signalementManagementFeaturesResponseSelector = (state) => get(state, "signalementManagement.features");

export const signalementManagementClickedPointSelector = (state) => get(state, "signalementManagement.clickedPoint");

export const signalementManagementContextsSelector = (state) => get(state, "signalementManagement.contexts");

export const signalementManagementMeSelector = (state) => get(state, "signalementManagement.user");

export const signalementManagementTaskSelector = (state) => get(state, "signalementManagement.task");

/**
 * Retourne vrai si la couche du layer SignalementManagement existe, est visible et est selectionnée
 * @param state
 * @param signalementManagementLayerId
 * @returns {boolean}
 */
export function isSignalementManagementActivateAndSelected(state, signalementManagementLayerId) {
  if (signalementManagementLayerId != null) {
    let layers = state.layers?.flat;
    layers = layers != null ? layers.filter(layer => layer.id === signalementManagementLayerId) : null;
    let signalementManagementLayer = layers!= null && layers.length !== 0 ? layers[0] : null;
    return signalementManagementLayer && signalementManagementLayer.visibility && state.layers.selected.includes(signalementManagementLayerId);
  }
  return false;

}

export const signalementLayerSelector = createSelector([
  layersSelector
], (layers) => head(layers.filter(l => l.id === 'signalements'))
);
