import {createPlugin} from '../../MapStore2/web/client/utils/PluginsUtils';
import signalementManagementExtension from './SignalementManagementExtension';


export default createPlugin("SignalementManagement", signalementManagementExtension);
