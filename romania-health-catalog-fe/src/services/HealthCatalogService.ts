import { environment } from "@/environment/environment";
import { Version } from "@/models/Version";
import CatalogVersionToVersion from "@/mappers/CatalogVersionToVersion";
import { CatalogVersion } from "@/models/CatalogVersion";
import { Drug } from "@/models/Drug";
import { Pageable } from "@/models/Pageable";
import { Page } from "@/models/Page";

const API_URL = `${environment.apiUrl}/backend`;
const VERSIONS_API = `${API_URL}/api/v1/versions`;
const DRUGS_API = "/drugs";
const SEARCH_API = `${DRUGS_API}?searchTerm=`;
const DEFAULT_PAGE_SIZE = environment.defaultPageSize;
const DEFAULT_SORT = environment.defaultSort;


/**
 * Fetches available versions of the health catlaog from BE
 */
async function getAvailableVersions(): Promise<Version[]> {
    const data = await fetch(VERSIONS_API);
    const json = await data.json();

    return CatalogVersionToVersion.catalogVersionsToVersions(json as CatalogVersion[]);
}

/**
 * Upload catalog file
 * @param file File to upload
 * @returns The version that got uploaded
 */
async function uploadFile(file: File): Promise<Version> {
    const formData = new FormData()
    formData.append('file', file, file.name)
    
    const data = await fetch(VERSIONS_API, { method: "POST", body: formData });
    const json = await data.json();

    return CatalogVersionToVersion.catalogVersionToVersion(json as CatalogVersion);
}

async function searchForDrug(versionId: string, searchTerm: string, pageable: Pageable = { page: 0, size: DEFAULT_PAGE_SIZE }): Promise<Page<Drug>>{
    const searchApiURL = `${VERSIONS_API}/${versionId}${SEARCH_API}${searchTerm}&page=${pageable.page}&size=${pageable.size}&sort=${DEFAULT_SORT}`;
    const data = await fetch(searchApiURL);
    const json = await data.json();

    return json as Page<Drug>;
}

async function loadDrug(drugId: string): Promise<Drug>{
    const drugApiURL =  `${API_URL}/api/v1${DRUGS_API}/${drugId}`;

    const data = await fetch(drugApiURL);
    const json = await data.json();

    return json as Drug;
}

export default {
    getAvailableVersions,
    uploadFile,
    searchForDrug,
    loadDrug,

};