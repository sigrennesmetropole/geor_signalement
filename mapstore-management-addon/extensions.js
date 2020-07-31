import { createPlugin } from "../../MapStore2/web/client/utils/PluginsUtils";

export default {
	Signalement: createPlugin('SignalementManagement', {
        lazy: true,
        loader: () => import(/* webpackChunkName: "extensions/extension" */`./SignalementManagementExtension`)
    })
};
