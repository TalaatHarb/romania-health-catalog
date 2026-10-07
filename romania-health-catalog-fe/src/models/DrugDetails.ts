export interface DrugDetails {
    drugId: string;
    atc?: { code: string; description?: string };
    restrictions: {
        narcotic: boolean;
        special: boolean;
        prescriptionMode?: string;
        needsApproval: boolean;
    };
    pricing: {
        pricePerPackage?: number;
        wholeSalePricePerPackage?: number;
        maxPrice?: number;
        referencePrice?: number;
    };
    insurance: {
        listCode?: string;
        listDescription?: string;
        percent?: number;
        source: string;
        details: Record<string, unknown>;
    }[];
    protocols: Record<string, unknown>[];
}
