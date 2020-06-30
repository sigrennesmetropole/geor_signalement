import { createSelector } from 'reselect';
import { get } from "lodash";

export const getSignalement = state => get(state, "signalement");

export const getSignalementState = createSelector(
  [ getSignalement ],
  (selector) => selector
);

export const isOpen = (state) => get(state, "signalement.open");

export const signalementAttachmentConfigurationSelector = (state) => get(state, "signalement.attachmentConfiguration");

export const signalementLayersSelector = (state) => get(state, "signalement.contextLayers");

export const signalementThemasSelector = (state) => get(state, "signalement.contextThemas");

export const signalementMeSelector = (state) => get(state, "signalement.user");