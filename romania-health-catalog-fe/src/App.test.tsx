import { render } from "@testing-library/react";
import App from "./App";
import HealthCatalogService from '@/services/HealthCatalogService';

test('App renders', () => {
    const mock = jest.spyOn(HealthCatalogService, 'getAvailableVersions');
    mock.mockImplementation(() => {
        return Promise.resolve([]);
    });

    const app = render(<App />);
    expect(app).toBeDefined();
});