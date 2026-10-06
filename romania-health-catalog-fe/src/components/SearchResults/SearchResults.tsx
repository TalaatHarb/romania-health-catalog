import { Drug } from "@/models/Drug";
import { Page, emptyPage } from "@/models/Page";

interface SearchResultsProps {
  drugs?: Page<Drug>;
  selectDrugCallback?: (drug: Drug) => void
  onPageRequest?: (page: number) => void;
}

function formatPrice(price?: number): string {
  if (price === undefined || price === null) return '-';
  return price.toFixed(2) + ' RON';
}

function SearchResults({ drugs = emptyPage<Drug>() , selectDrugCallback = (_drug) => {}, onPageRequest = (_p) => {} }: Readonly<SearchResultsProps>) {
  if (!drugs || drugs.content.length === 0) {
    return (
      <div className="card">
        <div className="card-body">
          <h5 className="card-title">Search results</h5>
          <div className="alert alert-info mb-0">No results found</div>
        </div>
      </div>
    );
  }

  return (
    <div className="card">
      <div className="card-body">
        <div className="d-flex justify-content-between align-items-center mb-2">
          <h5 className="card-title mb-0">Search results</h5>
          <small className="text-muted">{drugs.totalElements} items</small>
        </div>
        {/* Pagination */}
        {drugs.totalPages > 1 && (
          <nav aria-label="Search results pages" className="mb-3">
            <ul className="pagination justify-content-center mb-0">
              <li className={`page-item ${drugs.first ? 'disabled' : ''}`}>
                <button className="page-link" onClick={() => onPageRequest(Math.max(0, (drugs.number ?? 0) - 1))} aria-label="Previous">Previous</button>
              </li>

              {Array.from({ length: drugs.totalPages }).map((_, idx) => {
                const pageIndex = idx;
                const active = (drugs.number ?? 0) === pageIndex;
                return (
                  <li key={`page-${pageIndex}`} className={`page-item ${active ? 'active' : ''}`}>
                    <button className="page-link" onClick={() => onPageRequest(pageIndex)}>{pageIndex + 1}</button>
                  </li>
                );
              })}

              <li className={`page-item ${drugs.last ? 'disabled' : ''}`}>
                <button className="page-link" onClick={() => onPageRequest(Math.min((drugs.totalPages ?? 1) - 1, (drugs.number ?? 0) + 1))} aria-label="Next">Next</button>
              </li>
            </ul>
          </nav>
        )}

        <div className="list-group">
          {drugs.content.map(d => (
            <button key={d.id} id={d.id} type="button" className="list-group-item list-group-item-action d-flex justify-content-between align-items-start search-result" onClick={()=> selectDrugCallback(d)}>
              <div className="ms-2 me-auto text-start">
                <div className="fw-bold">{d.name}</div>
                <div className="small text-muted">{d.company || d.country} • {d.pharmaceuticalForm || d.presentationMode}</div>
              </div>
              <div className="text-end">
                <div className="small text-muted">{d.code}</div>
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