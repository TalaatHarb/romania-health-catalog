import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import App from "./App";
import HealthCatalogService from '@/services/HealthCatalogService';
import { Page } from "@/models/Page";
import { CatalogItem } from "@/models/CatalogItem";

afterEach(() => jest.restoreAllMocks());

test('App renders', async () => {
    const mock = jest.spyOn(HealthCatalogService, 'getAvailableVersions');
    mock.mockImplementation(() => {
        return Promise.resolve([]);
    });

    const app = render(<App />);
    expect(app).toBeDefined();
    await waitFor(() => expect(mock).toHaveBeenCalled());
});

test('App searches and displays catalog items of the chosen type', async () => {
    const city: CatalogItem = { id: 'c1', type: 'CITY', label: 'Cities', code: '4020', name: 'Rupea', details: { code: '4020', name: 'Rupea', districtCode: 'BV' } };
    const page: Page<CatalogItem> = { content: [city], size: 7, number: 0, totalElements: 1, totalPages: 1, numberOfElements: 1 };
    jest.spyOn(HealthCatalogService, 'getAvailableVersions').mockResolvedValue([{ id: 'v1', version: new Date(2024, 2) }]);
    const typesMock = jest.spyOn(HealthCatalogService, 'getItemTypes').mockResolvedValue([
        { type: 'DRUG', label: 'Drugs', count: 10 },
        { type: 'CITY', label: 'Cities', count: 18 },
    ]);
    const searchMock = jest.spyOn(HealthCatalogService, 'searchForItems').mockResolvedValue(page);

    render(<App />);

    await screen.findByRole('option', { name: 'Cities (18)' });
    expect(typesMock).toHaveBeenCalledWith('v1');

    fireEvent.change(document.getElementById('search-type')!, { target: { value: 'CITY' } });
    fireEvent.change(document.getElementById('search-bar')!, { target: { value: 'Rupea' } });
    fireEvent.click(document.getElementById('search-button')!);

    fireEvent.click(await screen.findByText('Rupea'));

    expect(searchMock).toHaveBeenCalledWith('v1', 'CITY', 'Rupea', { page: 0, size: 7 });
    expect(document.getElementById('item-card')).not.toBeNull();
    expect(screen.getByText('District code')).toBeDefined();
});

test('App shows an error when search fails', async () => {
    jest.spyOn(HealthCatalogService, 'getAvailableVersions').mockResolvedValue([{ id: 'v1', version: new Date(2024, 2) }]);
    jest.spyOn(HealthCatalogService, 'getItemTypes').mockResolvedValue([]);
    jest.spyOn(HealthCatalogService, 'searchForDrug').mockRejectedValue(new Error('boom'));

    render(<App />);
    await waitFor(() => expect((document.getElementById('search-button') as HTMLButtonElement).disabled).toBe(false));
    fireEvent.click(document.getElementById('search-button')!);

    expect((await screen.findByRole('alert')).textContent).toContain('Search failed: boom');
});
