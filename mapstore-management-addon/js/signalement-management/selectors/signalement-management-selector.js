import { createSelector } from 'reselect';
import { get } from "lodash";

export const getSignalementManagement = state => get(state, "signalementManagement");

export const getSignalementManagementState = createSelector(
  [ getSignalementManagement ],
  (selector) => selector
);


export const signalementManagementContextsSelector = (state) => get(state, "signalementManagement.contexts");

export const signalementManagementMeSelector = (state) => get(state, "signalementManagement.user");