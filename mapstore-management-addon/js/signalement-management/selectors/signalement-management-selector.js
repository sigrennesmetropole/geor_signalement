import { createSelector } from 'reselect';
import { get, head } from "lodash";
import {layersSelector} from '../../../MapStore2/web/client/selectors/layers';

export const getSignalementManagement = state => get(state, "signalementManagement");

export const getSignalementManagementState = createSelector(
  [ getSignalementManagement ],
  (selector) => selector
);


export const signalementManagementContextsSelector = (state) => get(state, "signalementManagement.contexts");

export const signalementManagementMeSelector = (state) => get(state, "signalementManagement.user");

export const signalementManagementTaskSelector = (state) => get(state, "signalementManagement.task");

export const signalementLayerSelector = createSelector([
  layersSelector
], (layers) => head(layers.filter(l => l.id === 'signalements'))
);
