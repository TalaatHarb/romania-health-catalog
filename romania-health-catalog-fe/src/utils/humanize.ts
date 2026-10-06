/**
 * Converts a camelCase property name to a readable label (e.g. cityCode -> City code)
 */
export function humanize(key: string): string {
    const spaced = key
        .replace(/([a-z\d])([A-Z])/g, '$1 $2')
        .replace(/([A-Z]+)([A-Z][a-z])/g, '$1 $2')
        .toLowerCase();
    return spaced.charAt(0).toUpperCase() + spaced.slice(1);
}
