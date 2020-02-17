/*global
 Ext, GeoExt, OpenLayers, GEOR
 */
Ext.namespace("GEOR.Addons");

/**
 * Urbanisme addon
 */
GEOR.Addons.Signalement = Ext.extend(GEOR.Addons.Base, {

	/**
	 * api: config[encoding] ``String`` The encoding to set in the headers when
	 * requesting the print service. Prevent character encoding issues,
	 * especially when using IE. Default is retrieved from document charset or
	 * characterSet if existing or ``UTF-8`` if not.
	 */
	encoding : document.charset || document.characterSet || "UTF-8",

	init : function(record) {
		this.log("init..." + record + " with target "
				+ (this.target !== null ? true : false) + " at "
				+ this.position);

		this.window = new Ext.Window({
			title : this.getText(record),
			closable : true,
			closeAction : "hide",
			resizable : false,
			border : false,
			cls : 'measurements',
			items : [ {
				xtype : 'toolbar',
				border : false,
				items : [ '-' ]
			} ],
			listeners : {
				hide : function() {
					this.item && this.item.setChecked(false);
					this.components && this.components.toggle(false);
				},
				scope : this
			}
		});

		if (this.target) {
			// create a button to be inserted in toolbar:
			this.components = this.target.insertButton(this.position, {
				xtype : 'button',
				tooltip : this.getTooltip(record),
				iconCls : "addon-signalement",
				handler : this._onCheckchange,
				scope : this,
				listeners : {
					"afterrender" : function() {
						if (this.options.openToolbarOnLoad) { // ???
							this._onCheckchange(this.item, true);
						}
					},
					delay : 500,
					scope : this
				}
			});
			this.target.doLayout();
		}
		this.log("Init done.");
	},

	destroy : function() {

		GEOR.Addons.Base.prototype.destroy.call(this);
	},

	/**
	 * Le log de base
	 */
	log : function(message) {
		if (this.option.log === "true") {
			console.log(message);
		}
	},

	/**
	 * Method: _onCheckchange Callback on checkbox state changed
	 */
	_onCheckchange : function(item, checked) {
		this.log("Change:" + item);
		if (checked) {
			this.window.show();
			this.window.alignTo(Ext.get(this.map.div), "t-t", [ 0, 5 ], true);
		} else {
			this.window.hide();
		}
	}
});
