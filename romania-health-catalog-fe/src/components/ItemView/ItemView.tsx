import { CatalogItem, ItemDetailValue } from "@/models/CatalogItem";
import { humanize } from "@/utils/humanize";

interface ItemViewProps {
  item?: CatalogItem;
  /**
   * Label of the currently selected object type (e.g. "Cities")
   */
  typeLabel?: string;
}

function formatValue(value: ItemDetailValue): string {
  if (value === null || value === undefined || value === '') return '-';
  if (typeof value === 'boolean') return value ? 'Yes' : 'No';
  return String(value);
}

function ItemView({ item, typeLabel = 'items' }: Readonly<ItemViewProps>) {
  if (!item) {
    return (
      <div className="card shadow-sm">
        <div className="card-body">
          <h5 className="card-title">Details</h5>
          <div className="text-muted">Select one of the {typeLabel.toLowerCase()} from the results to see details here.</div>
        </div>
      </div>
    );
  }

  const details = Object.entries(item.details ?? {});

  return (
    <div className="card shadow-sm" id="item-card">
      <div className="card-body">
        <div className="d-flex justify-content-between align-items-start gap-2">
          <div>
            <h5 className="card-title">{item.name || item.code || '-'}</h5>
            <h6 className="card-subtitle mb-2 text-muted">Code: {item.code || '-'}</h6>
          </div>
          <span className="badge text-bg-primary">{item.label}</span>
        </div>

        <table className="table table-sm table-striped mt-3 mb-0 item-details">
          <caption className="visually-hidden">{item.label} details</caption>
          <tbody>
            {details.map(([key, value]) => (
              <tr key={key}>
                <th scope="row" className="fw-semibold">{humanize(key)}</th>
                <td>{formatValue(value)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default ItemView;
