/**
 * Builds a geostyler-format style object from a FeatureCollection returned by the
 * signalement backend. MapStore2's VectorLegend component requires this format to
 * render a legend for vector layers.
 *
 * The backend produces a legacy MapStore style per feature (iconGlyph/iconShape/iconColor
 * for points, color/weight for lines, fillColor/fillOpacity for polygons). This utility
 * converts the first feature's style into a geostyler symbolizer so the TOC can display
 * a readable legend entry.
 *
 * @param {Object} featureCollection - GeoJSON FeatureCollection as returned by /task/search/geojson
 * @param {string} [ruleName='Signalements'] - Label shown next to the legend icon
 * @returns {Object|undefined} geostyler style object, or undefined if no style can be derived
 */
export function buildGeostylerStyle(featureCollection, ruleName = 'Signalements') {
    const features = featureCollection && featureCollection.features;
    if (!features || features.length === 0) {
        return undefined;
    }

    // Find the first feature that carries a style array
    const featureWithStyle = features.find(
        (f) => f.style && Array.isArray(f.style) && f.style.length > 0
    );
    if (!featureWithStyle) {
        return undefined;
    }

    const legacyStyle = featureWithStyle.style[0];
    const geometryType = featureWithStyle.geometry && featureWithStyle.geometry.type;
    const symbolizer = legacyStyleToGeostylerSymbolizer(legacyStyle, geometryType);

    if (!symbolizer) {
        return undefined;
    }

    return {
        format: 'geostyler',
        body: {
            rules: [
                {
                    name: ruleName,
                    symbolizers: [symbolizer]
                }
            ]
        }
    };
}

/**
 * Maps a single legacy MapStore style object to a geostyler symbolizer.
 *
 * @param {Object} legacyStyle - Style object from the backend (e.g. { iconGlyph, iconShape, iconColor } or { color, weight })
 * @param {string} geometryType - GeoJSON geometry type ('Point', 'LineString', 'Polygon', etc.)
 * @returns {Object|null} geostyler symbolizer or null if unrecognised
 */
function legacyStyleToGeostylerSymbolizer(legacyStyle, geometryType) {
    // Point-like geometry or point style detected via iconGlyph/iconShape
    if (
        geometryType === 'Point' ||
        geometryType === 'MultiPoint' ||
        legacyStyle.iconGlyph !== undefined ||
        legacyStyle.iconShape !== undefined
    ) {
        return buildMarkSymbolizer(legacyStyle);
    }

    // Line geometry or line style detected via weight without fillColor
    if (
        geometryType === 'LineString' ||
        geometryType === 'MultiLineString' ||
        (legacyStyle.color !== undefined && legacyStyle.fillColor === undefined)
    ) {
        return buildLineSymbolizer(legacyStyle);
    }

    // Polygon geometry
    if (
        geometryType === 'Polygon' ||
        geometryType === 'MultiPolygon' ||
        legacyStyle.fillColor !== undefined
    ) {
        return buildFillSymbolizer(legacyStyle);
    }

    return null;
}

/**
 * Maps legacy icon shape names (lowercase) to geostyler wellKnownName values.
 * Geostyler uses sentence-case well-known names per OGC SLD spec.
 */
const ICON_SHAPE_TO_WELL_KNOWN_NAME = {
    square: 'Square',
    circle: 'Circle',
    triangle: 'Triangle',
    star: 'Star',
    cross: 'Cross',
    x: 'X'
};

function buildMarkSymbolizer(legacyStyle) {
    const rawShape = (legacyStyle.iconShape || 'square').toLowerCase();
    const wellKnownName = ICON_SHAPE_TO_WELL_KNOWN_NAME[rawShape] || 'Square';
    const color = mapIconColor(legacyStyle.iconColor) || '#ff8800';

    return {
        kind: 'Mark',
        wellKnownName,
        color,
        opacity: 1,
        rotate: 0
    };
}

function buildLineSymbolizer(legacyStyle) {
    return {
        kind: 'Line',
        color: legacyStyle.color || '#ffcc33',
        width: legacyStyle.weight !== undefined ? legacyStyle.weight : 3,
        opacity: legacyStyle.opacity !== undefined ? legacyStyle.opacity : 1
    };
}

function buildFillSymbolizer(legacyStyle) {
    return {
        kind: 'Fill',
        color: legacyStyle.fillColor || '#f6e5c1',
        fillOpacity: legacyStyle.fillOpacity !== undefined ? legacyStyle.fillOpacity : 0.5,
        outlineColor: legacyStyle.color || '#e29c10',
        outlineWidth: legacyStyle.weight !== undefined ? legacyStyle.weight : 3
    };
}

/**
 * The backend stores icon colours as named colours (e.g. "orange"). These are valid CSS
 * colour names and can be passed directly to SVG attributes, but for explicit control
 * a basic mapping is provided. Falls back to the original value if not found.
 */
function mapIconColor(iconColor) {
    const COLOR_MAP = {
        orange: '#ff8800',
        red: '#ff0000',
        green: '#00aa00',
        blue: '#0055aa',
        yellow: '#ffcc00',
        purple: '#880088',
        gray: '#888888',
        grey: '#888888',
        white: '#ffffff',
        black: '#000000'
    };
    if (!iconColor) return null;
    return COLOR_MAP[iconColor.toLowerCase()] || iconColor;
}
