import { render, screen } from "@testing-library/react";
import DrugView from "./DrugView";
import { Drug } from "@/models/Drug";

describe('DrugView', () => {
    test('renders without a drug', () => {
        render(<DrugView />);
        expect(screen.getByText('Drug details')).toBeDefined();
        expect(screen.getByText('Select a drug from the results to see details here.')).toBeDefined();
    });

    test('renders with a complete drug object', () => {
        const drug: Drug = {
            id: '1',
            code: 'DRG001',
            name: 'Aspirin',
            presentationMode: 'Tablet',
            isNarcotic: 0,
            isFractional: false,
            isSpecial: true,
            isBrand: true,
            hasBioEchiv: true,
            qtyPerPackage: 20,
            pricePerPackage: 15.5,
            wholeSalePricePerPackage: 12,
            prescriptionMode: 'OTC',
            validFrom: new Date('2023-01-01'),
            validTo: new Date('2025-12-31'),
            activeSubstance: 'Acetylsalicylic acid',
            concentration: '500mg',
            pharmaceuticalForm: 'Tablet',
            company: 'Bayer',
            country: 'Germany',
            atc: 'N02BA01'
        };

        render(<DrugView drug={drug} />);

        expect(screen.getByText('Aspirin')).toBeDefined();
        expect(screen.getByText(/Bayer/)).toBeDefined();
        expect(screen.getByText(/Germany/)).toBeDefined();
        expect(screen.getByText(/DRG001/)).toBeDefined();
        expect(screen.getByText(/N02BA01/)).toBeDefined();
        expect(screen.getByText(/500mg/)).toBeDefined();
        expect(screen.getByText(/Acetylsalicylic acid/)).toBeDefined();
        expect(screen.getByText(/15.50 RON/)).toBeDefined();
    });

    test('renders with minimal drug data', () => {
        const drug: Drug = {
            id: '2',
            code: 'DRG002',
            name: 'Generic Drug',
            isNarcotic: 0,
            qtyPerPackage: 10,
            pricePerPackage: 5,
            validFrom: new Date('2024-01-01'),
            concentration: '100mg',
            pharmaceuticalForm: 'Capsule',
            company: 'Generic Co',
            country: 'USA',
            atc: 'A01AA01'
        };

        render(<DrugView drug={drug} />);

        expect(screen.getByText('Generic Drug')).toBeDefined();
        expect(screen.getByText(/Generic Co/)).toBeDefined();
        expect(screen.getByText(/DRG002/)).toBeDefined();
        expect(screen.getByText(/5.00 RON/)).toBeDefined();
    });

    test('displays narcotic badge when drug is narcotic', () => {
        const drug: Drug = {
            id: '3',
            code: 'DRG003',
            name: 'Morphine',
            isNarcotic: 1,
            qtyPerPackage: 10,
            pricePerPackage: 50,
            validFrom: new Date('2023-01-01'),
            concentration: '10mg',
            pharmaceuticalForm: 'Injection',
            company: 'Pharma Co',
            country: 'UK',
            atc: 'N02AA01'
        };

        render(<DrugView drug={drug} />);

        expect(screen.getByText('Narcotic')).toBeDefined();
    });

    test('displays special badge when drug is special', () => {
        const drug: Drug = {
            id: '4',
            code: 'DRG004',
            name: 'Special Med',
            isNarcotic: 0,
            isSpecial: true,
            qtyPerPackage: 5,
            pricePerPackage: 100,
            validFrom: new Date('2023-01-01'),
            concentration: '200mg',
            pharmaceuticalForm: 'Tablet',
            company: 'Special Pharma',
            country: 'France',
            atc: 'C01AA01'
        };

        render(<DrugView drug={drug} />);

        expect(screen.getByText('Special')).toBeDefined();
    });

    test('displays brand badge when drug is brand', () => {
        const drug: Drug = {
            id: '5',
            code: 'DRG005',
            name: 'Brand Med',
            isNarcotic: 0,
            isBrand: true,
            qtyPerPackage: 30,
            pricePerPackage: 25,
            validFrom: new Date('2023-01-01'),
            concentration: '50mg',
            pharmaceuticalForm: 'Tablet',
            company: 'Brand Co',
            country: 'Italy',
            atc: 'D01AA01'
        };

        render(<DrugView drug={drug} />);

        expect(screen.getByText('Brand')).toBeDefined();
    });

    test('displays bio equiv badge when drug has bio equivalent', () => {
        const drug: Drug = {
            id: '6',
            code: 'DRG006',
            name: 'Bio Equiv Med',
            isNarcotic: 0,
            hasBioEchiv: true,
            qtyPerPackage: 20,
            pricePerPackage: 18,
            validFrom: new Date('2023-01-01'),
            concentration: '100mg',
            pharmaceuticalForm: 'Tablet',
            company: 'Bio Pharma',
            country: 'Spain',
            atc: 'E01AA01'
        };

        render(<DrugView drug={drug} />);

        expect(screen.getByText('Bio equiv')).toBeDefined();
    });

    test('displays dash for missing optional fields', () => {
        const drug: Drug = {
            id: '7',
            code: 'DRG007',
            name: 'Minimal Drug',
            isNarcotic: 0,
            qtyPerPackage: 15,
            pricePerPackage: 10,
            validFrom: new Date('2023-01-01'),
            concentration: '75mg',
            pharmaceuticalForm: 'Capsule',
            company: 'Min Pharma',
            country: 'Romania',
            atc: 'F01AA01'
        };

        render(<DrugView drug={drug} />);

        const cardBody = document.querySelector('.card-body');
        expect(cardBody?.textContent).toContain('-');
    });

    test('formats dates correctly', () => {
        const drug: Drug = {
            id: '8',
            code: 'DRG008',
            name: 'Date Test Drug',
            isNarcotic: 0,
            qtyPerPackage: 10,
            pricePerPackage: 12,
            validFrom: new Date('2023-06-15'),
            validTo: new Date('2024-12-31'),
            concentration: '100mg',
            pharmaceuticalForm: 'Tablet',
            company: 'Date Pharma',
            country: 'Poland',
            atc: 'G01AA01'
        };

        render(<DrugView drug={drug} />);

        const cardBody = document.querySelector('.card-body');
        expect(cardBody?.textContent).toContain('6/15/2023');
        expect(cardBody?.textContent).toContain('12/31/2024');
    });

    test('renders drug card with correct id', () => {
        const drug: Drug = {
            id: '9',
            code: 'DRG009',
            name: 'ID Test Drug',
            isNarcotic: 0,
            qtyPerPackage: 25,
            pricePerPackage: 20,
            validFrom: new Date('2023-01-01'),
            concentration: '150mg',
            pharmaceuticalForm: 'Tablet',
            company: 'ID Pharma',
            country: 'Belgium',
            atc: 'H01AA01'
        };

        render(<DrugView drug={drug} />);

        expect(document.getElementById('drug-card')).toBeDefined();
    });
});