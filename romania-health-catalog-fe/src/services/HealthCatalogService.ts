import { environment } from "@/environment/environment";
import { Version } from "@/models/Version";
import CatalogVersionToVersion from "@/mappers/CatalogVersionToVersion";
import { CatalogVersion } from "@/models/CatalogVersion";
import { Drug } from "@/models/Drug";
import { DrugDetails } from "@/models/DrugDetails";
import { Pageable } from "@/models/Pageable";
import { Page } from "@/models/Page";
import { ItemType } from "@/models/ItemType";
import { CatalogItem } from "@/models/CatalogItem";

const API_URL = `${environment.apiUrl}/backend`;
const API_V1 = `${API_URL}/api/v1`;
const VERSIONS_API = `${API_V1}/versions`;
const IMPORTS_API = `${API_V1}/imports`;
const DRUGS_API = "/drugs";
const ITEMS_API = "/items";
const ITEM_TYPES_API = "/item-types";
const DEFAULT_PAGE_SIZE = environment.defaultPageSize;
const DEFAULT_SORT = environment.defaultSort;
const DEFAULT_ITEM_SORT = environment.defaultItemSort;
const UPLOAD_SECRET_HEADER = "uploadSecret";

interface CatalogImport {
    id: string;
    status: 'RUNNING' | 'SUCCEEDED' | 'FAILED';
    version?: CatalogVersion;
    error?: string;
}

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

    const job = await fetchJson<CatalogImport>(IMPORTS_API, {
        method: "POST",
        headers: { [UPLOAD_SECRET_HEADER]: uploadSecret },
        body: formData,
    });
    return waitForImport(job, uploadSecret);
}

async function uploadUrl(url: string, uploadSecret: string): Promise<Version> {
    const job = await fetchJson<CatalogImport>(`${IMPORTS_API}/from-url`, {
        method: "POST",
        headers: {
            [UPLOAD_SECRET_HEADER]: uploadSecret,
            "Content-Type": "application/x-www-form-urlencoded",
        },
        body: new URLSearchParams({ url }),
    });
    return waitForImport(job, uploadSecret);
}

async function waitForImport(initial: CatalogImport, uploadSecret: string): Promise<Version> {
    let job = initial;
    while (job.status === 'RUNNING') {
        await new Promise(resolve => setTimeout(resolve, 2000));
        try {
            job = await fetchJson<CatalogImport>(`${IMPORTS_API}/${encodeURIComponent(job.id)}`, {
                headers: { [UPLOAD_SECRET_HEADER]: uploadSecret },
            });
        } catch (error) {
            const message = error instanceof Error ? error.message : String(error);
            throw new Error(`Could not check import ${job.id}: ${message}. It may still be running. Check available versions before retrying.`);
        }
    }
    if (job.status === 'FAILED') throw new Error(job.error || 'Catalog import failed');
    if (job.status !== 'SUCCEEDED' || !job.version) throw new Error(`Invalid status returned for import ${job.id}`);
    return CatalogVersionToVersion.catalogVersionToVersion(job.version);
}

async function searchForDrug(versionId: string, searchTerm: string, pageable: Pageable = { page: 0, size: DEFAULT_PAGE_SIZE }): Promise<Page<Drug>>{
    const searchApiURL = `${VERSIONS_API}/${versionId}${DRUGS_API}?${pageQuery(searchTerm, pageable, DEFAULT_SORT)}`;
    return fetchJson<Page<Drug>>(searchApiURL);
}

async function loadDrug(drugId: string): Promise<Drug>{
    return fetchJson<Drug>(`${API_V1}${DRUGS_API}/${drugId}`);
}

/**
 * Fetches the additional details of a drug (ATC, restrictions, pricing, insurance, protocols)
 */
async function loadDrugDetails(drugId: string): Promise<DrugDetails> {
    return fetchJson<DrugDetails>(`${API_V1}${DRUGS_API}/${drugId}/details`);
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
    uploadUrl,
    searchForDrug,
    loadDrug,
    loadDrugDetails,
    getItemTypes,
    searchForItems,
    loadItem,
};
