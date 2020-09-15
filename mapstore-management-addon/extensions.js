import { createPlugin } from "@mapstore/utils/PluginsUtils";

export default {
	Signalement: createPlugin('SignalementManagement', {
        lazy: true,
        loader: () => import(/* webpackChunkName: "extensions/extension" */`./plugins/SignalementManagementExtension`)
    })
};
