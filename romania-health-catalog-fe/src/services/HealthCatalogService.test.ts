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
            json: async () => ({ id: 'job-id', status: 'SUCCEEDED', version: { id: 'version-id', issueDate: '2024-03-01' } }),
        });
        const url = 'https://www.casmb.ro/catalog.zip?download=1&version=2';
        const version = await HealthCatalogService.uploadUrl(url, 'my-secret');
        const [endpoint, request] = fetchMock.mock.calls[0];
        expect(endpoint).toMatch(/\/backend\/api\/v1\/imports\/from-url$/);
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

    test('polls until the background import has committed and returns its version', async () => {
        jest.useFakeTimers();
        try {
            fetchMock
                .mockResolvedValueOnce({ ok: true, json: async () => ({ id: 'job-id', status: 'RUNNING' }) })
                .mockResolvedValueOnce({ ok: true, json: async () => ({ id: 'job-id', status: 'RUNNING' }) })
                .mockResolvedValueOnce({ ok: true, json: async () => ({
                    id: 'job-id', status: 'SUCCEEDED', version: { id: 'version-id', issueDate: '2024-03-01' },
                }) });
            const result = HealthCatalogService.uploadUrl('https://www.casmb.ro/a.zip', 'secret');
            await jest.advanceTimersByTimeAsync(4000);
            await expect(result).resolves.toEqual({ id: 'version-id', version: new Date('2024-03-01') });
            expect(fetchMock).toHaveBeenNthCalledWith(2, expect.stringMatching(/\/imports\/job-id$/), {
                headers: { uploadSecret: 'secret' },
            });
        } finally {
            jest.useRealTimers();
        }
    });

    test('uses the background endpoint for file imports too', async () => {
        fetchMock.mockResolvedValue({
            ok: true,
            json: async () => ({ id: 'job-id', status: 'SUCCEEDED', version: { id: 'version-id', issueDate: '2024-03-01' } }),
        });
        const file = new File(['xml'], 'catalog.xml');
        await HealthCatalogService.uploadFile(file, 'secret');
        const [endpoint, request] = fetchMock.mock.calls[0];
        expect(endpoint).toMatch(/\/imports$/);
        expect(request.body.get('file')).toEqual(file);
        expect(request.headers.uploadSecret).toBe('secret');
    });

    test('surfaces background processing failures', async () => {
        fetchMock.mockResolvedValue({
            ok: true, json: async () => ({ id: 'job-id', status: 'FAILED', error: 'Catalog XML exceeds 2 GiB' }),
        });
        await expect(HealthCatalogService.uploadUrl('https://www.casmb.ro/a.zip', 'secret'))
            .rejects.toThrow('Catalog XML exceeds 2 GiB');
    });

    test('identifies an unconfirmed import instead of reporting it as failed', async () => {
        jest.useFakeTimers();
        try {
            fetchMock
                .mockResolvedValueOnce({ ok: true, json: async () => ({ id: 'job-id', status: 'RUNNING' }) })
                .mockRejectedValueOnce(new Error('Network unavailable'));
            const result = expect(HealthCatalogService.uploadUrl('https://www.casmb.ro/a.zip', 'secret'))
                .rejects.toThrow('Could not check import job-id: Network unavailable. It may still be running.');
            await jest.advanceTimersByTimeAsync(2000);
            await result;
        } finally {
            jest.useRealTimers();
        }
    });
});
