import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import SearchBar from "./SearchBar";
import { Version } from "@/models/Version";
import { emptyPage } from "@/models/Page";
import { Drug } from "@/models/Drug";
import { ItemType } from "@/models/ItemType";

describe('SearchBar', () => {
    test('renders without versions', () => {
        render(<SearchBar />);
        expect(screen.getByPlaceholderText(/search drugs/i)).toBeDefined();
    });

    test('renders with versions', () => {
        const versions: Version[] = [
            { version: new Date(2024, 11), id: '1' },
            { version: new Date(2023, 7), id: '2' }
        ];
        render(<SearchBar versions={versions} />);
        expect(screen.getByText(/version: 2024 - 12/i)).toBeDefined();
    });

    test('search button is disabled when no version is selected', () => {
        render(<SearchBar versions={[]} />);
        const searchButton = document.getElementById('search-button') as HTMLButtonElement;
        expect(searchButton.disabled).toBe(true);
    });

    test('search button is enabled when version is selected', () => {
        const versions: Version[] = [{ version: new Date(2024, 11), id: '1' }];
        render(<SearchBar versions={versions} />);
        const searchButton = document.getElementById('search-button') as HTMLButtonElement;
        expect(searchButton.disabled).toBe(false);
    });

    test('updates search term on input change', () => {
        render(<SearchBar />);
        const searchInput = document.getElementById('search-bar') as HTMLInputElement;
        
        fireEvent.change(searchInput, { target: { value: 'aspirin' } });
        
        expect(searchInput.value).toBe('aspirin');
    });

    test('clears search term when clear button is clicked', () => {
        render(<SearchBar />);
        const searchInput = document.getElementById('search-bar') as HTMLInputElement;
        const clearButton = screen.getByRole('button', { name: /clear/i });
        
        fireEvent.change(searchInput, { target: { value: 'test search' } });
        expect(searchInput.value).toBe('test search');
        
        fireEvent.click(clearButton);
        expect(searchInput.value).toBe('');
    });

    test('calls searchFunctionCallback on form submit', async () => {
        const mockSearch = jest.fn().mockResolvedValue(emptyPage<Drug>());
        const versions: Version[] = [{ version: new Date(2024, 11), id: 'v1' }];
        
        render(<SearchBar versions={versions} searchFunctionCallback={mockSearch} />);
        
        const searchInput = document.getElementById('search-bar') as HTMLInputElement;
        const searchButton = document.getElementById('search-button') as HTMLButtonElement;
        
        fireEvent.change(searchInput, { target: { value: 'paracetamol' } });
        fireEvent.click(searchButton);
        
        await waitFor(() => {
            expect(mockSearch).toHaveBeenCalledWith('v1', 'paracetamol');
        });
    });

    test('calls searchFunctionCallback on form submit via Enter key', async () => {
        const mockSearch = jest.fn().mockResolvedValue(emptyPage<Drug>());
        const versions: Version[] = [{ version: new Date(2024, 11), id: 'v2' }];
        
        render(<SearchBar versions={versions} searchFunctionCallback={mockSearch} />);
        
        const searchInput = document.getElementById('search-bar') as HTMLInputElement;
        
        fireEvent.change(searchInput, { target: { value: 'ibuprofen' } });
        fireEvent.submit(searchInput.closest('form')!);
        
        await waitFor(() => {
            expect(mockSearch).toHaveBeenCalledWith('v2', 'ibuprofen');
        });
    });

    test('does not call searchFunctionCallback when no version is selected', async () => {
        const mockSearch = jest.fn().mockResolvedValue(emptyPage<Drug>());
        
        render(<SearchBar versions={[]} searchFunctionCallback={mockSearch} />);
        
        const searchButton = document.getElementById('search-button') as HTMLButtonElement;
        
        fireEvent.click(searchButton);
        
        await waitFor(() => {
            expect(mockSearch).not.toHaveBeenCalled();
        });
    });

    test('allows empty search term', async () => {
        const mockSearch = jest.fn().mockResolvedValue(emptyPage<Drug>());
        const versions: Version[] = [{ version: new Date(2024, 11), id: 'v3' }];
        
        render(<SearchBar versions={versions} searchFunctionCallback={mockSearch} />);
        
        const searchButton = document.getElementById('search-button') as HTMLButtonElement;
        
        fireEvent.click(searchButton);
        
        await waitFor(() => {
            expect(mockSearch).toHaveBeenCalledWith('v3', '');
        });
    });

    test('updates selected version when VersionSwitcher changes', async () => {
        const mockSearch = jest.fn().mockResolvedValue(emptyPage<Drug>());
        const versions: Version[] = [
            { version: new Date(2024, 11), id: 'v1' },
            { version: new Date(2023, 7), id: 'v2' }
        ];
        
        render(<SearchBar versions={versions} searchFunctionCallback={mockSearch} />);
        
        // Initially the first version is selected
        expect(screen.getByText(/version: 2024 - 12/i)).toBeDefined();
        
        // Change to second version
        const dropdownToggle = document.getElementById('toggle-dropdown') as HTMLButtonElement;
        fireEvent.click(dropdownToggle);
        
        const secondVersion = document.getElementById('v2') as HTMLButtonElement;
        fireEvent.click(secondVersion);
        
        // Search with new version
        const searchInput = document.getElementById('search-bar') as HTMLInputElement;
        fireEvent.change(searchInput, { target: { value: 'test' } });
        
        const searchButton = document.getElementById('search-button') as HTMLButtonElement;
        fireEvent.click(searchButton);
        
        await waitFor(() => {
            expect(mockSearch).toHaveBeenCalledWith('v2', 'test');
        });
    });

    describe('search type selection', () => {
        const versions: Version[] = [{ version: new Date(2024, 11), id: 'v1' }];
        const itemTypes: ItemType[] = [
            { type: 'DRUG', label: 'Drugs', count: 3 },
            { type: 'CITY', label: 'Cities', count: 18 },
            { type: 'STREET', label: 'Streets', count: 0 },
        ];

        test('defaults to drugs when no item types are given', () => {
            render(<SearchBar />);
            const select = document.getElementById('search-type') as HTMLSelectElement;
            expect(select.value).toBe('DRUG');
            expect(screen.getByRole('option', { name: 'Drugs' })).toBeDefined();
        });

        test('renders item types with counts and disables empty ones', () => {
            render(<SearchBar versions={versions} itemTypes={itemTypes} />);
            expect(screen.getByRole('option', { name: 'Cities (18)' })).toBeDefined();
            const streets = screen.getByRole('option', { name: 'Streets (0)' }) as HTMLOptionElement;
            expect(streets.disabled).toBe(true);
        });

        test('searches items of the selected type', async () => {
            const mockDrugSearch = jest.fn().mockResolvedValue(emptyPage<Drug>());
            const mockItemSearch = jest.fn().mockResolvedValue(emptyPage());
            const mockTypeChanged = jest.fn();
            render(<SearchBar versions={versions} itemTypes={itemTypes} searchFunctionCallback={mockDrugSearch}
                searchItemsCallback={mockItemSearch} typeChangedCallback={mockTypeChanged} />);

            fireEvent.change(document.getElementById('search-type')!, { target: { value: 'CITY' } });
            expect(mockTypeChanged).toHaveBeenCalledWith('CITY');
            const searchInput = document.getElementById('search-bar') as HTMLInputElement;
            expect(searchInput.placeholder).toMatch(/search cities/i);

            fireEvent.change(searchInput, { target: { value: 'Rupea' } });
            fireEvent.click(document.getElementById('search-button')!);

            await waitFor(() => {
                expect(mockItemSearch).toHaveBeenCalledWith('v1', 'Rupea', 'CITY');
            });
            expect(mockDrugSearch).not.toHaveBeenCalled();
        });

        test('notifies version changes', () => {
            const mockVersionChanged = jest.fn();
            render(<SearchBar versions={versions} versionChangedCallback={mockVersionChanged} />);
            expect(mockVersionChanged).toHaveBeenCalledWith(versions[0]);
        });

        test('falls back to drugs when the selected type is no longer available', () => {
            const mockTypeChanged = jest.fn();
            const { rerender } = render(<SearchBar versions={versions} itemTypes={itemTypes} typeChangedCallback={mockTypeChanged} />);
            fireEvent.change(document.getElementById('search-type')!, { target: { value: 'CITY' } });

            rerender(<SearchBar versions={versions} itemTypes={[itemTypes[0]]} typeChangedCallback={mockTypeChanged} />);

            expect((document.getElementById('search-type') as HTMLSelectElement).value).toBe('DRUG');
            expect(mockTypeChanged).toHaveBeenLastCalledWith('DRUG');
        });
    });
});