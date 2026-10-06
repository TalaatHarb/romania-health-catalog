import { CatalogVersion } from "@/models/CatalogVersion";
import { Version } from "@/models/Version";

/**
 * Issue dates come either as epoch seconds or as ISO-8601 strings depending on the BE JSON configuration
 */
function toDate(issueDate: CatalogVersion['issueDate']): Date {
    return typeof issueDate === 'number' ? new Date(issueDate * 1000) : new Date(issueDate);
}

function catalogVersionToVersion(catalogVersion: CatalogVersion): Version {
    return {id: catalogVersion.id, version: toDate(catalogVersion.issueDate)}
}

function catalogVersionsToVersions(catalogVersions: CatalogVersion[]): Version[] {
    return catalogVersions.map(v => catalogVersionToVersion(v));
}

export default {
    catalogVersionToVersion,
    catalogVersionsToVersions,
    
};
