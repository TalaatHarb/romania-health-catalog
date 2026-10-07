import { Drug } from "@/models/Drug";
import { DrugDetails } from "@/models/DrugDetails";

interface DrugViewProps {
  drug?: Drug;
  /** additional details, loaded separately after the drug */
  details?: DrugDetails;
  detailsLoading?: boolean;
  detailsError?: boolean;
}

function formatDate(d?: Date | string): string {
  if (!d) return '-';
  const date = d instanceof Date ? d : new Date(d);
  return date.toLocaleDateString();
}

function DrugView(props: Readonly<DrugViewProps>) {
  const { drug, details, detailsLoading, detailsError } = props;

  if (!drug) {
    return (
      <div className="card">
        <div className="card-body">
          <h5 className="card-title">Drug details</h5>
          <div className="text-muted">Select a drug from the results to see details here.</div>
        </div>
      </div>
    );
  }

  return (
    <div className="card" id="drug-card">
      <div className="card-body">
        <div className="d-flex justify-content-between align-items-start">
          <div>
            <h5 className="card-title">{drug.name}</h5>
            <h6 className="card-subtitle mb-2 text-muted">{drug.company} • {drug.country}</h6>
          </div>
          <div className="text-end">
            <div className="small text-muted">Code: {drug.code}</div>
            <div className="small text-muted">ATC: {drug.atc || '-'}</div>
          </div>
        </div>

        <div className="row mt-3">
          <div className="col-6">
            <ul className="list-unstyled small">
              <li><strong>Form:</strong> {drug.pharmaceuticalForm || '-'}</li>
              <li><strong>Presentation:</strong> {drug.presentationMode || '-'}</li>
              <li><strong>Concentration:</strong> {drug.concentration || '-'}</li>
              <li><strong>Active substance:</strong> {drug.activeSubstance || '-'}</li>
            </ul>
          </div>
          <div className="col-6">
            <ul className="list-unstyled small">
              <li><strong>Qty / package:</strong> {drug.qtyPerPackage ?? '-'}</li>
              <li><strong>Price / package:</strong> {drug.pricePerPackage ? drug.pricePerPackage.toFixed(2) + ' RON' : '-'}</li>
              <li><strong>Valid from:</strong> {formatDate(drug.validFrom)}</li>
              <li><strong>Valid to:</strong> {formatDate(drug.validTo)}</li>
            </ul>
          </div>
        </div>

        <div className="mt-3">
          <div className="small text-muted">Flags:</div>
          <div className="mt-1">
            {drug.isNarcotic === 1 ? <span className="badge bg-danger me-1">Narcotic</span> : ''}
            {drug.isSpecial ? <span className="badge bg-warning text-dark me-1">Special</span> : ''}
            {drug.isBrand ? <span className="badge bg-info text-dark me-1">Brand</span> : ''}
            {drug.hasBioEchiv ? <span className="badge bg-success me-1">Bio equiv</span> : ''}
          </div>
        </div>

        {detailsLoading && (
          <div id="drug-details-loading" className="small text-muted mt-3">
            <span className="spinner-border spinner-border-sm me-2" aria-hidden="true"></span>Loading additional details...
          </div>
        )}
        {detailsError && <div id="drug-details-error" className="small text-muted mt-3">Additional details are unavailable.</div>}
        {details && <DrugExtraDetails details={details} />}
      </div>
    </div>
  );
}

function money(value?: number): string {
  return value === undefined || value === null ? '-' : value.toFixed(2) + ' RON';
}

function DrugExtraDetails({ details }: Readonly<{ details: DrugDetails }>) {
  const { atc, restrictions, pricing, insurance, protocols } = details;
  return (
    <div id="drug-extra-details" className="mt-3 border-top pt-3 small">
      <div className="mb-2">
        <strong>ATC:</strong> {atc ? `${atc.code}${atc.description ? ' - ' + atc.description : ''}` : '-'}
      </div>
      <div className="mb-2">
        <strong>Restrictions:</strong>{' '}
        {restrictions.narcotic ? <span className="badge bg-danger me-1">Narcotic / restricted</span> : <span className="badge bg-secondary me-1">Not narcotic</span>}
        {restrictions.special ? <span className="badge bg-warning text-dark me-1">Special</span> : ''}
        {restrictions.needsApproval ? <span className="badge bg-info text-dark me-1">Needs approval</span> : ''}
        {restrictions.prescriptionMode ? <span className="text-muted">Prescription: {restrictions.prescriptionMode}</span> : ''}
      </div>
      <div className="mb-2">
        <strong>Pricing:</strong> package {money(pricing.pricePerPackage)} • wholesale {money(pricing.wholeSalePricePerPackage)}
        {' '}• max {money(pricing.maxPrice)} • reference {money(pricing.referencePrice)}
      </div>
      <div className="mb-2">
        <strong>Insurance / copayment lists:</strong>
        {insurance.length === 0 ? <span className="text-muted"> none</span> : (
          <ul className="mb-0">
            {insurance.map((i, idx) => (
              <li key={`${i.listCode}-${i.source}-${idx}`}>
                {i.listCode}{i.listDescription ? ` - ${i.listDescription}` : ''}{i.percent !== undefined && i.percent !== null ? ` (${i.percent}%)` : ''}
                <span className="text-muted"> via {i.source === 'DRUG' ? 'drug' : 'active substance'}</span>
              </li>
            ))}
          </ul>
        )}
      </div>
      <div>
        <strong>Therapeutic protocols:</strong>
        {protocols.length === 0 ? <span className="text-muted"> none</span> : (
          <ul className="mb-0">
            {protocols.map((p, idx) => (
              <li key={`${String(p.codeProtocolTherap)}-${idx}`}>{String(p.codeProtocolTherap ?? '')} {p.descProtocolTherap ? `- ${String(p.descProtocolTherap)}` : ''}</li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
}

export default DrugView;