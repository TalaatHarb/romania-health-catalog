import { Drug } from "@/models/Drug";

interface DrugViewProps {
  drug?: Drug;
}

function formatDate(d?: Date | string): string {
  if (!d) return '-';
  const date = d instanceof Date ? d : new Date(d);
  return date.toLocaleDateString();
}

function DrugView(props: Readonly<DrugViewProps>) {
  const drug = props.drug;

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
      </div>
    </div>
  );
}

export default DrugView;