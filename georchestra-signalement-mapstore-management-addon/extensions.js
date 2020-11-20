import { createPlugin } from "@mapstore/utils/PluginsUtils";

export default {
	Signalement: createPlugin('Signalement', {
        lazy: true,
        loader: () => import(/* webpackChunkName: "extensions/extension" */`./plugins/SignalementExtension`)
    })
};
