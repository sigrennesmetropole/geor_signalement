import {createPlugin} from '../../MapStore2/web/client/utils/PluginsUtils';
import signalementExtension from './SignalementExtension';

export default createPlugin("Signalement", signalementExtension);
