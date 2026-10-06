import { CatalogVersion } from "@/models/CatalogVersion";
import { Version } from "@/models/Version";

function catalogVersionToVersion(catalogVersion: CatalogVersion): Version {
    return {id: catalogVersion.id, version:  new Date(catalogVersion.issueDate * 1000)}
}

function catalogVersionsToVersions(catalogVersions: CatalogVersion[]): Version[] {
    return catalogVersions.map(v => catalogVersionToVersion(v));
}

export default {
    catalogVersionToVersion,
    catalogVersionsToVersions,
    
};