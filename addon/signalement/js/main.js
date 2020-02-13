/*global
 Ext, GeoExt, OpenLayers, GEOR
 */
Ext.namespace("GEOR.Addons", "GEOR.data");

/**
 * Urbanisme addon
 */
GEOR.Addons.Signalement = Ext.extend(GEOR.Addons.Base, {

    /** api: config[encoding]
     * ``String`` The encoding to set in the headers when requesting the print
     * service. Prevent character encoding issues, especially when using IE.
     * Default is retrieved from document charset or characterSet if existing
     * or ``UTF-8`` if not.
     */
    encoding: document.charset || document.characterSet || "UTF-8",

    init: function(record) {
    },


    destroy: function() {

    	GEOR.Addons.Base.prototype.destroy.call(this);
    }
});
