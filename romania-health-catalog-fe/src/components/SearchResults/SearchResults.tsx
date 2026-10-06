import { CatalogItem } from "@/models/CatalogItem";
import { Drug } from "@/models/Drug";
import { Page, emptyPage } from "@/models/Page";

interface SearchResultsProps {
  drugs?: Page<Drug>;
  selectDrugCallback?: (drug: Drug) => void
  /**
   * Generic catalog objects; when provided they are displayed instead of drugs
   */
  items?: Page<CatalogItem>;
  selectItemCallback?: (item: CatalogItem) => void;
  selectedId?: string;
  loading?: boolean;
  onPageRequest?: (page: number) => void;
}

/**
 * Max number of page buttons shown at once around the current page
 */
const PAGE_WINDOW = 7;

function formatPrice(price?: number): string {
  if (price === undefined || price === null) return '-';
  return price.toFixed(2) + ' RON';
}

function pageWindow(current: number, total: number): number[] {
  const half = Math.floor(PAGE_WINDOW / 2);
  const start = Math.max(0, Math.min(current - half, total - PAGE_WINDOW));
  const end = Math.min(total, start + PAGE_WINDOW);
  return Array.from({ length: end - start }, (_, i) => start + i);
}

function itemSummary(item: CatalogItem): string {
  return Object.entries(item.details ?? {})
    .filter(([, value]) => value !== null && value !== '' && String(value) !== item.name && String(value) !== item.code)
    .slice(0, 2)
    .map(([, value]) => String(value))
    .join(' • ');
}

function Pagination({ page, onPageRequest }: Readonly<{ page: Page<unknown>, onPageRequest: (page: number) => void }>) {
  const current = page.number ?? 0;
  const pages = pageWindow(current, page.totalPages);
  return (
    <nav aria-label="Search results pages" className="mb-3">
      <ul className="pagination pagination-sm flex-wrap justify-content-center mb-0">
        <li className={`page-item ${page.first ? 'disabled' : ''}`}>
          <button className="page-link" onClick={() => onPageRequest(Math.max(0, current - 1))} aria-label="Previous">Previous</button>
        </li>

        {pages.map(pageIndex => {
          const active = current === pageIndex;
          return (
            <li key={`page-${pageIndex}`} className={`page-item ${active ? 'active' : ''}`}>
              <button className="page-link" aria-current={active ? 'page' : undefined} onClick={() => onPageRequest(pageIndex)}>{pageIndex + 1}</button>
            </li>
          );
        })}

        <li className={`page-item ${page.last ? 'disabled' : ''}`}>
          <button className="page-link" onClick={() => onPageRequest(Math.min((page.totalPages ?? 1) - 1, current + 1))} aria-label="Next">Next</button>
        </li>
      </ul>
    </nav>
  );
}

function SearchResults({ drugs = emptyPage<Drug>(), selectDrugCallback = () => {}, items, selectItemCallback = () => {}, selectedId, loading = false, onPageRequest = () => {} }: Readonly<SearchResultsProps>) {
  const page: Page<Drug> | Page<CatalogItem> = items ?? drugs;

  if (!page || page.content.length === 0) {
    return (
      <div className="card shadow-sm" aria-busy={loading}>
        <div className="card-body">
          <h5 className="card-title">Search results</h5>
          {loading
            ? <div className="text-muted" role="status"><span className="spinner-border spinner-border-sm me-2" aria-hidden="true"></span>Searching...</div>
            : <div className="alert alert-info mb-0">No results found</div>}
        </div>
      </div>
    );
  }

  return (
    <div className="card shadow-sm" aria-busy={loading}>
      <div className="card-body">
        <div className="d-flex justify-content-between align-items-center mb-2">
          <h5 className="card-title mb-0">Search results</h5>
          <small className="text-muted" aria-live="polite">{page.totalElements} items</small>
        </div>
        {page.totalPages > 1 && <Pagination page={page} onPageRequest={onPageRequest} />}

        <div className={`list-group ${loading ? 'opacity-50' : ''}`}>
          {items
            ? items.content.map(item => (
              <button key={item.id} id={item.id} type="button" className={`list-group-item list-group-item-action d-flex justify-content-between align-items-start search-result ${selectedId === item.id ? 'active' : ''}`} onClick={() => selectItemCallback(item)}>
                <div className="ms-2 me-auto text-start">
                  <div className="fw-bold">{item.name || item.code || '-'}</div>
                  <div className="small result-summary">{itemSummary(item)}</div>
                </div>
                <div className="text-end">
                  <span className="badge text-bg-light border">{item.code || '-'}</span>
                </div>
              </button>
            ))
            : drugs.content.map(d => (
              <button key={d.id} id={d.id} type="button" className={`list-group-item list-group-item-action d-flex justify-content-between align-items-start search-result ${selectedId === d.id ? 'active' : ''}`} onClick={() => selectDrugCallback(d)}>
                <div className="ms-2 me-auto text-start">
                  <div className="fw-bold">{d.name}</div>
                  <div className="small result-summary">{d.company || d.country} • {d.pharmaceuticalForm || d.presentationMode}</div>
                </div>
                <div className="text-end">
                  <div className="small result-summary">{d.code}</div>
                  <div className="fw-semibold">{formatPrice(d.pricePerPackage)}</div>
                </div>
              </button>
            ))}
        </div>
      </div>
    </div>
  );
}

export default SearchResults;
