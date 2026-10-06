import { render, screen, fireEvent } from "@testing-library/react";
import SearchResults from "./SearchResults";
import { Drug } from "@/models/Drug";
import { Page } from "@/models/Page";

describe('SearchResults', () => {
    test('renders empty state when no drugs provided', () => {
        render(<SearchResults />);
        expect(screen.getByText('Search results')).toBeDefined();
        expect(screen.getByText('No results found')).toBeDefined();
    });

    test('renders empty state when drugs array is empty', () => {
        const emptyPage: Page<Drug> = {
            content: [],
            size: 0,
            number: 0,
            totalElements: 0,
            totalPages: 0,
            numberOfElements: 0
        };
        render(<SearchResults drugs={emptyPage} />);
        expect(screen.getByText('No results found')).toBeDefined();
    });

    test('renders single drug result', () => {
        const drug: Drug = {
            id: '1',
            code: 'DRG001',
            name: 'Aspirin',
            presentationMode: 'Tablet',
            isNarcotic: 0,
            qtyPerPackage: 20,
            pricePerPackage: 15.5,
            validFrom: new Date('2023-01-01'),
            concentration: '500mg',
            pharmaceuticalForm: 'Tablet',
            company: 'Bayer',
            country: 'Germany',
            atc: 'N02BA01'
        };

        const page: Page<Drug> = {
            content: [drug],
            size: 1,
            number: 0,
            totalElements: 1,
            totalPages: 1,
            numberOfElements: 1
        };

        render(<SearchResults drugs={page} />);
        
        expect(screen.getByText('Aspirin')).toBeDefined();
        expect(screen.getByText(/DRG001/)).toBeDefined();
        expect(screen.getByText(/15.50 RON/)).toBeDefined();
        expect(screen.getByText('1 items')).toBeDefined();
    });

    test('renders multiple drug results', () => {
        const drugs: Drug[] = [
            {
                id: '1',
                code: 'DRG001',
                name: 'Aspirin',
                isNarcotic: 0,
                qtyPerPackage: 20,
                pricePerPackage: 15.5,
                validFrom: new Date('2023-01-01'),
                concentration: '500mg',
                pharmaceuticalForm: 'Tablet',
                company: 'Bayer',
                country: 'Germany',
                atc: 'N02BA01'
            },
            {
                id: '2',
                code: 'DRG002',
                name: 'Paracetamol',
                isNarcotic: 0,
                qtyPerPackage: 30,
                pricePerPackage: 10.25,
                validFrom: new Date('2023-01-01'),
                concentration: '500mg',
                pharmaceuticalForm: 'Tablet',
                company: 'Generic Co',
                country: 'Romania',
                atc: 'N02BE01'
            }
        ];

        const page: Page<Drug> = {
            content: drugs,
            size: 2,
            number: 0,
            totalElements: 2,
            totalPages: 1,
            numberOfElements: 2
        };

        render(<SearchResults drugs={page} />);
        
        expect(screen.getByText('Aspirin')).toBeDefined();
        expect(screen.getByText('Paracetamol')).toBeDefined();
        expect(screen.getByText('2 items')).toBeDefined();
    });

    test('calls selectDrugCallback when drug is clicked', () => {
        const mockSelectDrug = jest.fn();
        const drug: Drug = {
            id: '1',
            code: 'DRG001',
            name: 'Aspirin',
            isNarcotic: 0,
            qtyPerPackage: 20,
            pricePerPackage: 15.5,
            validFrom: new Date('2023-01-01'),
            concentration: '500mg',
            pharmaceuticalForm: 'Tablet',
            company: 'Bayer',
            country: 'Germany',
            atc: 'N02BA01'
        };

        const page: Page<Drug> = {
            content: [drug],
            size: 1,
            number: 0,
            totalElements: 1,
            totalPages: 1,
            numberOfElements: 1
        };

        render(<SearchResults drugs={page} selectDrugCallback={mockSelectDrug} />);
        
        const drugButton = document.getElementById('1') as HTMLButtonElement;
        fireEvent.click(drugButton);
        
        expect(mockSelectDrug).toHaveBeenCalledWith(drug);
    });

    test('displays dash for missing price', () => {
        const drug: Drug = {
            id: '1',
            code: 'DRG001',
            name: 'No Price Drug',
            isNarcotic: 0,
            qtyPerPackage: 20,
            pricePerPackage: 0,
            validFrom: new Date('2023-01-01'),
            concentration: '500mg',
            pharmaceuticalForm: 'Tablet',
            company: 'Test Co',
            country: 'Romania',
            atc: 'N02BA01'
        };

        const page: Page<Drug> = {
            content: [drug],
            size: 1,
            number: 0,
            totalElements: 1,
            totalPages: 1,
            numberOfElements: 1
        };

        render(<SearchResults drugs={page} />);
        
        expect(screen.getByText('0.00 RON')).toBeDefined();
    });

    test('renders pagination when multiple pages exist', () => {
        const drugs: Drug[] = [
            {
                id: '1',
                code: 'DRG001',
                name: 'Drug 1',
                isNarcotic: 0,
                qtyPerPackage: 20,
                pricePerPackage: 15,
                validFrom: new Date('2023-01-01'),
                concentration: '500mg',
                pharmaceuticalForm: 'Tablet',
                company: 'Company',
                country: 'Romania',
                atc: 'N02BA01'
            }
        ];

        const page: Page<Drug> = {
            content: drugs,
            size: 10,
            number: 0,
            totalElements: 25,
            totalPages: 3,
            numberOfElements: 10,
            first: true,
            last: false
        };

        render(<SearchResults drugs={page} />);
        
        expect(screen.getByText('1')).toBeDefined();
        expect(screen.getByText('2')).toBeDefined();
        expect(screen.getByText('3')).toBeDefined();
        expect(screen.getByText('Previous')).toBeDefined();
        expect(screen.getByText('Next')).toBeDefined();
    });

    test('does not render pagination when only one page exists', () => {
        const drug: Drug = {
            id: '1',
            code: 'DRG001',
            name: 'Single Drug',
            isNarcotic: 0,
            qtyPerPackage: 20,
            pricePerPackage: 15,
            validFrom: new Date('2023-01-01'),
            concentration: '500mg',
            pharmaceuticalForm: 'Tablet',
            company: 'Company',
            country: 'Romania',
            atc: 'N02BA01'
        };

        const page: Page<Drug> = {
            content: [drug],
            size: 10,
            number: 0,
            totalElements: 1,
            totalPages: 1,
            numberOfElements: 1,
            first: true,
            last: true
        };

        render(<SearchResults drugs={page} />);
        
        expect(screen.queryByText('Previous')).toBeNull();
        expect(screen.queryByText('Next')).toBeNull();
    });

    test('calls onPageRequest when page number is clicked', () => {
        const mockPageRequest = jest.fn();
        const drugs: Drug[] = [
            {
                id: '1',
                code: 'DRG001',
                name: 'Drug 1',
                isNarcotic: 0,
                qtyPerPackage: 20,
                pricePerPackage: 15,
                validFrom: new Date('2023-01-01'),
                concentration: '500mg',
                pharmaceuticalForm: 'Tablet',
                company: 'Company',
                country: 'Romania',
                atc: 'N02BA01'
            }
        ];

        const page: Page<Drug> = {
            content: drugs,
            size: 10,
            number: 0,
            totalElements: 25,
            totalPages: 3,
            numberOfElements: 10,
            first: true,
            last: false
        };

        render(<SearchResults drugs={page} onPageRequest={mockPageRequest} />);
        
        const page2Button = screen.getByText('2');
        fireEvent.click(page2Button);
        
        expect(mockPageRequest).toHaveBeenCalledWith(1);
    });

    test('calls onPageRequest when Next button is clicked', () => {
        const mockPageRequest = jest.fn();
        const drugs: Drug[] = [
            {
                id: '1',
                code: 'DRG001',
                name: 'Drug 1',
                isNarcotic: 0,
                qtyPerPackage: 20,
                pricePerPackage: 15,
                validFrom: new Date('2023-01-01'),
                concentration: '500mg',
                pharmaceuticalForm: 'Tablet',
                company: 'Company',
                country: 'Romania',
                atc: 'N02BA01'
            }
        ];

        const page: Page<Drug> = {
            content: drugs,
            size: 10,
            number: 0,
            totalElements: 25,
            totalPages: 3,
            numberOfElements: 10,
            first: true,
            last: false
        };

        render(<SearchResults drugs={page} onPageRequest={mockPageRequest} />);
        
        const nextButton = screen.getByText('Next');
        fireEvent.click(nextButton);
        
        expect(mockPageRequest).toHaveBeenCalledWith(1);
    });

    test('calls onPageRequest when Previous button is clicked', () => {
        const mockPageRequest = jest.fn();
        const drugs: Drug[] = [
            {
                id: '1',
                code: 'DRG001',
                name: 'Drug 1',
                isNarcotic: 0,
                qtyPerPackage: 20,
                pricePerPackage: 15,
                validFrom: new Date('2023-01-01'),
                concentration: '500mg',
                pharmaceuticalForm: 'Tablet',
                company: 'Company',
                country: 'Romania',
                atc: 'N02BA01'
            }
        ];

        const page: Page<Drug> = {
            content: drugs,
            size: 10,
            number: 1,
            totalElements: 25,
            totalPages: 3,
            numberOfElements: 10,
            first: false,
            last: false
        };

        render(<SearchResults drugs={page} onPageRequest={mockPageRequest} />);
        
        const prevButton = screen.getByText('Previous');
        fireEvent.click(prevButton);
        
        expect(mockPageRequest).toHaveBeenCalledWith(0);
    });

    test('highlights active page in pagination', () => {
        const drugs: Drug[] = [
            {
                id: '1',
                code: 'DRG001',
                name: 'Drug 1',
                isNarcotic: 0,
                qtyPerPackage: 20,
                pricePerPackage: 15,
                validFrom: new Date('2023-01-01'),
                concentration: '500mg',
                pharmaceuticalForm: 'Tablet',
                company: 'Company',
                country: 'Romania',
                atc: 'N02BA01'
            }
        ];

        const page: Page<Drug> = {
            content: drugs,
            size: 10,
            number: 1,
            totalElements: 25,
            totalPages: 3,
            numberOfElements: 10,
            first: false,
            last: false
        };

        render(<SearchResults drugs={page} />);
        
        const pageButtons = screen.getAllByRole('button');
        const page2Button = pageButtons.find(btn => btn.textContent === '2' && btn.className.includes('page-link'));
        
        expect(page2Button?.closest('li')?.className).toContain('active');
    });

    test('disables Previous button on first page', () => {
        const drugs: Drug[] = [
            {
                id: '1',
                code: 'DRG001',
                name: 'Drug 1',
                isNarcotic: 0,
                qtyPerPackage: 20,
                pricePerPackage: 15,
                validFrom: new Date('2023-01-01'),
                concentration: '500mg',
                pharmaceuticalForm: 'Tablet',
                company: 'Company',
                country: 'Romania',
                atc: 'N02BA01'
            }
        ];

        const page: Page<Drug> = {
            content: drugs,
            size: 10,
            number: 0,
            totalElements: 25,
            totalPages: 3,
            numberOfElements: 10,
            first: true,
            last: false
        };

        render(<SearchResults drugs={page} />);
        
        const prevButton = screen.getByText('Previous');
        expect(prevButton.closest('li')?.className).toContain('disabled');
    });

    test('disables Next button on last page', () => {
        const drugs: Drug[] = [
            {
                id: '1',
                code: 'DRG001',
                name: 'Drug 1',
                isNarcotic: 0,
                qtyPerPackage: 20,
                pricePerPackage: 15,
                validFrom: new Date('2023-01-01'),
                concentration: '500mg',
                pharmaceuticalForm: 'Tablet',
                company: 'Company',
                country: 'Romania',
                atc: 'N02BA01'
            }
        ];

        const page: Page<Drug> = {
            content: drugs,
            size: 10,
            number: 2,
            totalElements: 25,
            totalPages: 3,
            numberOfElements: 5,
            first: false,
            last: true
        };

        render(<SearchResults drugs={page} />);
        
        const nextButton = screen.getByText('Next');
        expect(nextButton.closest('li')?.className).toContain('disabled');
    });
});