import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import SearchBar from "./SearchBar";
import { Version } from "@/models/Version";
import { emptyPage } from "@/models/Page";
import { Drug } from "@/models/Drug";

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
});