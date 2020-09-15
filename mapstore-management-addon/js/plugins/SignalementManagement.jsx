import {createPlugin} from '@mapstore/utils/PluginsUtils';
import signalementManagementExtension from './SignalementManagementExtension';


export default createPlugin("SignalementManagement", signalementManagementExtension);
