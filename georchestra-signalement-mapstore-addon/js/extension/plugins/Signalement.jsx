import {createPlugin} from '@mapstore/utils/PluginsUtils';
import signalementExtension from './SignalementExtension';

export default createPlugin("Signalement", signalementExtension);
