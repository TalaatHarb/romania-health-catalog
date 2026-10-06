import { useEffect, useState } from 'react';
import TopBar from "@/components/TopBar/TopBar";
import SearchBar from "@/components/SearchBar/SearchBar";
import SearchResults from "@/components/SearchResults/SearchResults";
import DrugView from '@/components/DrugView/DrugView';
import ItemView from '@/components/ItemView/ItemView';
import { Version } from '@/models/Version';
import HealthCatalogService from '@/services/HealthCatalogService';
import './App.css';
import { environment } from '@/environment/environment';
import ImportButton from '@/components/ImportButton/ImportButton';
import { Page, emptyPage } from '@/models/Page';
import { Drug } from '@/models/Drug';
import { CatalogItem } from '@/models/CatalogItem';
import { DEFAULT_ITEM_TYPES, DRUG_TYPE, ItemType } from '@/models/ItemType';

interface SearchParams {
  versionId: string;
  searchTerm: string;
  type: string;
}

function errorMessage(error: unknown): string {
  return error instanceof Error ? error.message : String(error);
}

function App() {

  const [versions, setVersions] = useState<Version[]>();
  const [itemTypes, setItemTypes] = useState<ItemType[]>(DEFAULT_ITEM_TYPES);
  const [selectedType, setSelectedType] = useState<string>(DRUG_TYPE);
  const [drugsResult, setDrugsResult] = useState<Page<Drug>>(emptyPage<Drug>());
  const [itemsResult, setItemsResult] = useState<Page<CatalogItem>>(emptyPage<CatalogItem>());
  const [selectedDrug, setSelectedDrug] = useState<Drug | undefined>();
  const [selectedItem, setSelectedItem] = useState<CatalogItem | undefined>();
  // track last search parameters so pagination can request additional pages
  const [lastSearch, setLastSearch] = useState<SearchParams | undefined>(undefined);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | undefined>();

  const isDrugSearch = selectedType === DRUG_TYPE;
  const selectedTypeLabel = itemTypes.find(t => t.type === selectedType)?.label ?? 'items';

  function fetchVersions() {
    HealthCatalogService.getAvailableVersions()
      .then(retrivedVersions => setVersions(retrivedVersions))
      .catch(e => setError(`Could not load catalog versions: ${errorMessage(e)}`));
  }

  function fetchItemTypes(version: Version) {
    HealthCatalogService.getItemTypes(version.id)
      .then(types => setItemTypes(types.length > 0 ? types : DEFAULT_ITEM_TYPES))
      .catch(() => setItemTypes(DEFAULT_ITEM_TYPES));
  }

  async function uploadFile(file: File): Promise<Version> {
    const version = await HealthCatalogService.uploadFile(file);
    fetchVersions();
    return version;
  }

  async function runSearch(params: SearchParams, pageNumber: number = 0): Promise<void> {
    setLastSearch(params);
    setLoading(true);
    setError(undefined);
    const pageable = { page: pageNumber, size: environment.defaultPageSize };
    try {
      if (params.type === DRUG_TYPE) {
        setDrugsResult(await HealthCatalogService.searchForDrug(params.versionId, params.searchTerm, pageable));
      } else {
        setItemsResult(await HealthCatalogService.searchForItems(params.versionId, params.type, params.searchTerm, pageable));
      }
    } catch (e) {
      setError(`Search failed: ${errorMessage(e)}`);
    } finally {
      setLoading(false);
    }
  }

  function changeType(type: string) {
    setSelectedType(type);
    setDrugsResult(emptyPage<Drug>());
    setSelectedDrug(undefined);
    setItemsResult(emptyPage<CatalogItem>());
    setSelectedItem(undefined);
    setLastSearch(undefined);
  }

  // Initial loading of the application
  useEffect(fetchVersions, []);

  return (
    <>
      <header className="app-header mb-4">
        <div className="container d-flex justify-content-between align-items-center py-3">
          <div>
            <TopBar />
            <p className="app-subtitle mb-0">Browse drugs, cities, streets, physicians and every other nomenclature of the CNAS catalog</p>
          </div>
          <div>
            <ImportButton fileChangeCallback={uploadFile} />
          </div>
        </div>
      </header>

      <div className="container">
        <main className="mb-4">
          <div className="row justify-content-center">
            <div className="col-12">
              <div className="card shadow-sm p-3">
                <SearchBar
                  versions={versions}
                  itemTypes={itemTypes}
                  searchFunctionCallback={(versionId, searchTerm) => runSearch({ versionId, searchTerm, type: DRUG_TYPE })}
                  searchItemsCallback={(versionId, searchTerm, type) => runSearch({ versionId, searchTerm, type })}
                  versionChangedCallback={fetchItemTypes}
                  typeChangedCallback={changeType} />
              </div>
            </div>
          </div>
          {error && (
            <div className="alert alert-danger alert-dismissible mt-3 mb-0" role="alert">
              {error}
              <button type="button" className="btn-close" aria-label="Close" onClick={() => setError(undefined)}></button>
            </div>
          )}
        </main>

        <section id="results-section" className="row g-3 mb-4">
          <div className="col-12 col-lg-6">
            <SearchResults
              drugs={drugsResult}
              selectDrugCallback={(drug: Drug) => setSelectedDrug(drug)}
              items={isDrugSearch ? undefined : itemsResult}
              selectItemCallback={(item: CatalogItem) => setSelectedItem(item)}
              selectedId={isDrugSearch ? selectedDrug?.id : selectedItem?.id}
              loading={loading}
              onPageRequest={(page) => {
                if (lastSearch) {
                  runSearch(lastSearch, page);
                }
              }} />
          </div>
          <div className="col-12 col-lg-6">
            {isDrugSearch
              ? <DrugView drug={selectedDrug} />
              : <ItemView item={selectedItem} typeLabel={selectedTypeLabel} />}
          </div>
        </section>
      </div>
    </>
  )
}

export default App;
