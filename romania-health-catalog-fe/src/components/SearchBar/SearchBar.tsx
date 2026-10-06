import VersionSwitcher from "@/components/VersionSwitcher/VersionSwitcher";
import { Drug } from "@/models/Drug";
import { Page, emptyPage } from "@/models/Page";
import { Version } from "@/models/Version";
import { ChangeEvent, FormEvent, useState } from "react";

interface SearchBarProps {
  versions?: Version[];
  searchFunctionCallback?: (versionId: string, searchTerm: string) => Promise<Page<Drug>>;
}

function SearchBar({ versions = [], searchFunctionCallback = (_searchTerm: string) => Promise.resolve(emptyPage<Drug>()) }: Readonly<SearchBarProps>) {

  const [searchTerm, setSearchTerm] = useState<string>('');
  const [selectedVersion, setSelectedVersion] = useState<Version | undefined>(undefined);

  async function handleSubmit(event?: FormEvent) {
    if (event) event.preventDefault();
    if (selectedVersion) {
      await searchFunctionCallback(selectedVersion.id, searchTerm);
    }
  }

  return (
    <form className="container-fluid" onSubmit={handleSubmit}>
      <div className="row g-2 align-items-center">
        <div className="col-12 col-md-3">
          <div className="searchbar-version-switcher">
            <VersionSwitcher versions={versions} versionChanged={(version: Version) => setSelectedVersion(version)} />
          </div>
        </div>

        <div className="col-12 col-md-6">
          <div className="input-group">
            <input id="search-bar" type="search" className="form-control" placeholder="Search drugs, company, code..." aria-label="Search" value={searchTerm} onChange={(event: ChangeEvent<HTMLInputElement>) => setSearchTerm(event.target.value)} />
            <button className="btn btn-outline-secondary" type="button" onClick={() => setSearchTerm('')} title="Clear">Clear</button>
          </div>
        </div>

        <div className="col-12 col-md-2 d-grid">
          <button id="search-button" className="btn btn-primary" type="submit" disabled={!selectedVersion}>Search</button>
        </div>
      </div>
    </form>
  );
}

export default SearchBar;