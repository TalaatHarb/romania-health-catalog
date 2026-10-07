import { environment } from "@/environment/environment";
import { Version } from "@/models/Version";
import CatalogVersionToVersion from "@/mappers/CatalogVersionToVersion";
import { CatalogVersion } from "@/models/CatalogVersion";
import { Drug } from "@/models/Drug";
import { Pageable } from "@/models/Pageable";
import { Page } from "@/models/Page";
import { ItemType } from "@/models/ItemType";
import { CatalogItem } from "@/models/CatalogItem";

const API_URL = `${environment.apiUrl}/backend`;
const API_V1 = `${API_URL}/api/v1`;
const VERSIONS_API = `${API_V1}/versions`;
const DRUGS_API = "/drugs";
const ITEMS_API = "/items";
const ITEM_TYPES_API = "/item-types";
const DEFAULT_PAGE_SIZE = environment.defaultPageSize;
const DEFAULT_SORT = environment.defaultSort;
const DEFAULT_ITEM_SORT = environment.defaultItemSort;
const UPLOAD_SECRET_HEADER = "uploadSecret";

async function fetchJson<T>(url: string, init?: RequestInit): Promise<T> {
    const response = await fetch(url, init);
    if (!response.ok) {
        let message = `Request failed (${response.status})`;
        try {
            const problem = await response.json();
            message = problem?.detail ?? problem?.title ?? message;
        } catch {
            // body isn't JSON, keep the generic message
        }
        throw new Error(message);
    }
    return await response.json() as T;
}

function pageQuery(searchTerm: string, pageable: Pageable, sort: string): string {
    return `searchTerm=${encodeURIComponent(searchTerm)}&page=${pageable.page}&size=${pageable.size}&sort=${sort}`;
}

/**
 * Fetches available versions of the health catlaog from BE
 */
async function getAvailableVersions(): Promise<Version[]> {
    const json = await fetchJson<CatalogVersion[]>(VERSIONS_API);
    return CatalogVersionToVersion.catalogVersionsToVersions(json);
}

/**
 * Upload catalog file
 * @param file File to upload
 * @param uploadSecret Secret the backend requires to accept uploads
 * @returns The version that got uploaded
 */
async function uploadFile(file: File, uploadSecret: string): Promise<Version> {
    const formData = new FormData()
    formData.append('file', file, file.name)

    const json = await fetchJson<CatalogVersion>(VERSIONS_API, {
        method: "POST",
        headers: { [UPLOAD_SECRET_HEADER]: uploadSecret },
        body: formData,
    });
    return CatalogVersionToVersion.catalogVersionToVersion(json);
}

async function searchForDrug(versionId: string, searchTerm: string, pageable: Pageable = { page: 0, size: DEFAULT_PAGE_SIZE }): Promise<Page<Drug>>{
    const searchApiURL = `${VERSIONS_API}/${versionId}${DRUGS_API}?${pageQuery(searchTerm, pageable, DEFAULT_SORT)}`;
    return fetchJson<Page<Drug>>(searchApiURL);
}

async function loadDrug(drugId: string): Promise<Drug>{
    return fetchJson<Drug>(`${API_V1}${DRUGS_API}/${drugId}`);
}

/**
 * Fetches all searchable object types of a version with their item counts
 */
async function getItemTypes(versionId: string): Promise<ItemType[]> {
    return fetchJson<ItemType[]>(`${VERSIONS_API}/${versionId}${ITEM_TYPES_API}`);
}

async function searchForItems(versionId: string, type: string, searchTerm: string, pageable: Pageable = { page: 0, size: DEFAULT_PAGE_SIZE }): Promise<Page<CatalogItem>> {
    const url = `${VERSIONS_API}/${versionId}${ITEMS_API}?type=${encodeURIComponent(type)}&${pageQuery(searchTerm, pageable, DEFAULT_ITEM_SORT)}`;
    return fetchJson<Page<CatalogItem>>(url);
}

async function loadItem(itemId: string): Promise<CatalogItem> {
    return fetchJson<CatalogItem>(`${API_V1}${ITEMS_API}/${itemId}`);
}

export default {
    getAvailableVersions,
    uploadFile,
    searchForDrug,
    loadDrug,
    getItemTypes,
    searchForItems,
    loadItem,
};
