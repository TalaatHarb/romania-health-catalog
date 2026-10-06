export type ItemDetailValue = string | number | boolean | null;

/**
 * Any catalog object other than drugs (city, street, physician, ...)
 */
export interface CatalogItem {
    id: string;
    type: string;
    label: string;
    code?: string;
    name?: string;
    details: Record<string, ItemDetailValue>;
}
