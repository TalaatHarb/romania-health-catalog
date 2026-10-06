import VersionSwitcher from "@/components/VersionSwitcher/VersionSwitcher";
import { DEFAULT_ITEM_TYPES, DRUG_TYPE, ItemType } from "@/models/ItemType";
import { Version } from "@/models/Version";
import { ChangeEvent, FormEvent, useEffect, useState } from "react";

interface SearchBarProps {
  versions?: Version[];
  /**
   * Searchable object types of the selected version (defaults to drugs only)
   */
  itemTypes?: ItemType[];
  /**
   * Called to search drugs
   */
  searchFunctionCallback?: (versionId: string, searchTerm: string) => Promise<unknown>;
  /**
   * Called to search any other object type (cities, streets, physicians, ...)
   */
  searchItemsCallback?: (versionId: string, searchTerm: string, type: string) => Promise<unknown>;
  versionChangedCallback?: (version: Version) => void;
  typeChangedCallback?: (type: string) => void;
}

function placeholderFor(itemType?: ItemType): string {
  if (!itemType || itemType.type === DRUG_TYPE) {
    return "Search drugs, company, code...";
  }
  return `Search ${itemType.label.toLowerCase()} by name or code...`;
}

function optionText(itemType: ItemType): string {
  return itemType.count === undefined ? itemType.label : `${itemType.label} (${itemType.count})`;
}

function SearchBar({
  versions = [],
  itemTypes = DEFAULT_ITEM_TYPES,
  searchFunctionCallback = () => Promise.resolve(),
  searchItemsCallback = () => Promise.resolve(),
  versionChangedCallback = () => { },
  typeChangedCallback = () => { },
}: Readonly<SearchBarProps>) {

  const [searchTerm, setSearchTerm] = useState<string>('');
  const [selectedVersion, setSelectedVersion] = useState<Version | undefined>(undefined);
  const [selectedType, setSelectedType] = useState<string>(DRUG_TYPE);

  // fall back to drugs when the selected type disappears (e.g. after switching version)
  useEffect(() => {
    if (!itemTypes.some(t => t.type === selectedType)) {
      setSelectedType(DRUG_TYPE);
      typeChangedCallback(DRUG_TYPE);
    }
  }, [itemTypes, selectedType, typeChangedCallback]);

  const currentType = itemTypes.find(t => t.type === selectedType);

  async function handleSubmit(event?: FormEvent) {
    if (event) event.preventDefault();
    if (!selectedVersion) return;
    if (selectedType === DRUG_TYPE) {
      await searchFunctionCallback(selectedVersion.id, searchTerm);
    } else {
      await searchItemsCallback(selectedVersion.id, searchTerm, selectedType);
    }
  }

  function onVersionChanged(version: Version) {
    setSelectedVersion(version);
    versionChangedCallback(version);
  }

  function onTypeChanged(event: ChangeEvent<HTMLSelectElement>) {
    setSelectedType(event.target.value);
    typeChangedCallback(event.target.value);
  }

  return (
    <form className="container-fluid search-form" role="search" onSubmit={handleSubmit}>
      <div className="row g-2 align-items-end">
        <div className="col-12 col-lg-3">
          <span className="form-label small text-muted d-block mb-1">Catalog version</span>
          <div className="searchbar-version-switcher">
            <VersionSwitcher versions={versions} versionChanged={onVersionChanged} />
          </div>
        </div>

        <div className="col-12 col-md-4 col-lg-3">
          <label htmlFor="search-type" className="form-label small text-muted mb-1">Search in</label>
          <select id="search-type" name="type" className="form-select" value={selectedType} onChange={onTypeChanged}>
            {itemTypes.map(t => (
              <option key={t.type} value={t.type} disabled={t.type !== DRUG_TYPE && t.count === 0}>{optionText(t)}</option>
            ))}
          </select>
        </div>

        <div className="col-12 col-md-8 col-lg-4">
          <label htmlFor="search-bar" className="form-label small text-muted mb-1">Search term</label>
          <div className="input-group">
            <input id="search-bar" name="searchTerm" type="search" className="form-control" placeholder={placeholderFor(currentType)} aria-label="Search" enterKeyHint="search" value={searchTerm} onChange={(event: ChangeEvent<HTMLInputElement>) => setSearchTerm(event.target.value)} />
            <button className="btn btn-outline-secondary" type="button" onClick={() => setSearchTerm('')} title="Clear">Clear</button>
          </div>
        </div>

        <div className="col-12 col-lg-2 d-grid">
          <button id="search-button" className="btn btn-primary" type="submit" disabled={!selectedVersion}>Search</button>
        </div>
      </div>
    </form>
  );
}

export default SearchBar;
