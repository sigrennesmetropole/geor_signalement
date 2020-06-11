import { createSelector } from 'reselect';
import { get } from "lodash";

export const getSignalement = state => get(state, "signalement");

export const getSignalementState = createSelector(
  [ getSignalement ],
  (selector) => selector
);

export const isOpen = (state) => get(state, "signalement.open");

export const signalementsLayersSelector = (state) => get(state, "signalement.contextLayers");