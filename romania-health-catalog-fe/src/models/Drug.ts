export interface Drug {
    id: string;
    code: string;
    name: string;
    presentationMode?: string;
    isNarcotic: number;
    isFractional?: boolean;
    isSpecial?: boolean;
    isBrand?: boolean;
    hasBioEchiv?: boolean;
    qtyPerPackage: number;
    pricePerPackage: number;
    wholeSalePricePerPackage?: number;
    prescriptionMode?: string;
    validFrom: Date;
    validTo?: Date;
    activeSubstance?: string;
    concentration: string;
    pharmaceuticalForm: string;
    company: string;
    country: string;
    atc: string;
}