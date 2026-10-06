/**
 * Type key of drugs, which have a dedicated API and detailed view
 */
export const DRUG_TYPE = 'DRUG';

/**
 * A searchable object type of a catalog version
 */
export interface ItemType {
    type: string;
    label: string;
    /**
     * Number of objects of this type in the selected version (unknown until loaded)
     */
    count?: number;
}

export const DEFAULT_ITEM_TYPES: ItemType[] = [{ type: DRUG_TYPE, label: 'Drugs' }];
