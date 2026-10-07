import HealthCatalogService from './HealthCatalogService';

describe('URL imports', () => {
    const originalFetch = global.fetch;
    const fetchMock = jest.fn();

    beforeEach(() => {
        global.fetch = fetchMock;
        fetchMock.mockReset();
    });

    afterAll(() => {
        global.fetch = originalFetch;
    });

    test('sends the URL as form data and the upload secret as a header', async () => {
        fetchMock.mockResolvedValue({
            ok: true,
            json: async () => ({ id: 'version-id', issueDate: '2024-03-01' }),
        });
        const url = 'https://www.casmb.ro/catalog.zip?download=1&version=2';
        const version = await HealthCatalogService.uploadUrl(url, 'my-secret');
        const [endpoint, request] = fetchMock.mock.calls[0];
        expect(endpoint).toMatch(/\/backend\/api\/v1\/versions\/from-url$/);
        expect(request.method).toBe('POST');
        expect(request.headers).toEqual({
            uploadSecret: 'my-secret',
            'Content-Type': 'application/x-www-form-urlencoded',
        });
        expect(request.body.get('url')).toBe(url);
        expect(version).toEqual({ id: 'version-id', version: new Date('2024-03-01') });
    });

    test('surfaces the backend problem detail', async () => {
        fetchMock.mockResolvedValue({
            ok: false,
            status: 403,
            json: async () => ({ detail: 'Missing or invalid upload secret' }),
        });
        await expect(HealthCatalogService.uploadUrl('https://www.casmb.ro/a.zip', 'wrong'))
            .rejects.toThrow('Missing or invalid upload secret');
    });
});
