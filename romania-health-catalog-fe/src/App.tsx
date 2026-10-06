import { useEffect, useState } from 'react';
import TopBar from "@/components/TopBar/TopBar";
import SearchBar from "@/components/SearchBar/SearchBar";
import SearchResults from "@/components/SearchResults/SearchResults";
import DrugView from '@/components/DrugView/DrugView';
import { Version } from '@/models/Version';
import HealthCatalogService from '@/services/HealthCatalogService';
import './App.css';
import { environment } from '@/environment/environment';
import ImportButton from '@/components/ImportButton/ImportButton';
import { Page, emptyPage } from '@/models/Page';
import { Drug } from '@/models/Drug';

function App() {

  function fetchVersions() {
    HealthCatalogService.getAvailableVersions().then(retrivedVersions => setVersions(retrivedVersions));
  }

  async function uploadFile(file: File): Promise<Version>{
    const version = await HealthCatalogService.uploadFile(file);
    fetchVersions();
    return version;
  }

  // track last search parameters so pagination can request additional pages
  const [lastSearchVersionId, setLastSearchVersionId] = useState<string | undefined>(undefined);
  const [lastSearchTerm, setLastSearchTerm] = useState<string | undefined>(undefined);

  async function search(versionId: string, searchTerm: string, pageNumber: number = 0) : Promise<Page<Drug>>{
    // remember last search so pagination can reuse the parameters
    setLastSearchVersionId(versionId);
    setLastSearchTerm(searchTerm);

  const drugsPage = await HealthCatalogService.searchForDrug(versionId, searchTerm, { page: pageNumber, size: environment.defaultPageSize });
    setDrugsResult(drugsPage);
    return drugsPage;
  }

  const [versions, setVersions] = useState<Version[]>();
  const [drugsResult, setDrugsResult] = useState<Page<Drug>>(emptyPage<Drug>());
  const [selectedDrug, setSelectedDrug] = useState<Drug | undefined>();

  // Initial loading of the application
  useEffect(fetchVersions, []);

  return (
    <div className="container">
      <header className="d-flex justify-content-between align-items-center py-3">
        <div>
          <TopBar />
        </div>
        <div>
          <ImportButton fileChangeCallback={uploadFile} />
        </div>
      </header>

      <main className="mb-4">
        <div className="row justify-content-center">
          <div className="col-12 col-md-10">
            <div className="card p-3">
              <SearchBar versions={versions} searchFunctionCallback={search}/>
            </div>
          </div>
        </div>
      </main>

      <section id="results-section" className="row g-3">
        <div className="col-12 col-md-6">
          <SearchResults drugs={drugsResult} selectDrugCallback={(drug: Drug) => setSelectedDrug(drug)} onPageRequest={(page) => {
              if (lastSearchVersionId && lastSearchTerm !== undefined) {
                search(lastSearchVersionId, lastSearchTerm, page);
              }
            }} />
        </div>
        <div className="col-12 col-md-6">
          <DrugView drug={selectedDrug}/>
        </div>
      </section>
    </div>
  )
}

export default App;
