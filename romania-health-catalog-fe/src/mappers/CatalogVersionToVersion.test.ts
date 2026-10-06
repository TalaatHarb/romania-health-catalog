import CatalogVersionToVersion from "./CatalogVersionToVersion";

describe('CatalogVersionToVersion', () => {
    test('maps epoch seconds', () => {
        const version = CatalogVersionToVersion.catalogVersionToVersion({ id: '1', issueDate: 1709299366 });
        expect(version).toEqual({ id: '1', version: new Date('2024-03-01T13:22:46Z') });
    });

    test('maps ISO-8601 strings', () => {
        const versions = CatalogVersionToVersion.catalogVersionsToVersions([{ id: '2', issueDate: '2024-03-01T13:22:46Z' }]);
        expect(versions).toEqual([{ id: '2', version: new Date('2024-03-01T13:22:46Z') }]);
    });
});
