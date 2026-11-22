import { DrugDetails } from "@/models/DrugDetails";

type Insurance = DrugDetails['insurance'][number];

interface InsuranceGroup {
    insurance: Insurance;
    records: Record<string, unknown>[];
}

function sourceLabel(source: string): string {
    if (source === 'DRUG') return 'Drug';
    if (source === 'ACTIVE_SUBSTANCE') return 'Active substance';
    return source;
}

function recordKey(record: Record<string, unknown>): string {
    return JSON.stringify(Object.entries(record).sort(([a], [b]) => a.localeCompare(b)));
}

function displayValue(value: unknown): string {
    if (value === undefined || value === null || value === '') return '-';
    return typeof value === 'object' ? JSON.stringify(value) : String(value);
}

function InsuranceHistory({ insurance }: Readonly<{ insurance: DrugDetails['insurance'] }>) {
    const groups = new Map<string, InsuranceGroup>();
    for (const entry of insurance) {
        const key = JSON.stringify([entry.listCode, entry.listDescription, entry.percent, entry.source]);
        const group = groups.get(key);
        if (group) group.records.push(entry.details);
        else groups.set(key, { insurance: entry, records: [entry.details] });
    }

    return (
        <div className="mb-2">
            <strong>Insurance / copayment lists:</strong>
            {groups.size > 0 && <div className="text-muted">Includes historical records; expand a list to see validity dates.</div>}
            {groups.size === 0 ? <span className="text-muted"> none</span> : (
                <ul className="list-unstyled mt-2 mb-0">
                    {[...groups.entries()].map(([key, group]) => {
                        const entry = group.insurance;
                        const records = [...new Map(group.records.map(record => [recordKey(record), record])).values()]
                            .sort((a, b) => String(b.validFrom ?? '').localeCompare(String(a.validFrom ?? '')));
                        const fields = [...new Set(records.flatMap(record => Object.keys(record)))]
                            .filter(field => field !== 'validFrom' && field !== 'validTo');
                        return (
                            <li key={key} className="border rounded p-2 mb-2">
                                <div className="d-flex flex-wrap align-items-center gap-2">
                                    <strong>{entry.listCode || '-'}{entry.listDescription ? ` - ${entry.listDescription}` : ''}</strong>
                                    {entry.percent !== undefined && entry.percent !== null && <span className="badge bg-success">{entry.percent}%</span>}
                                    <span className="badge bg-secondary">{sourceLabel(entry.source)}</span>
                                </div>
                                <details className="mt-1">
                                    <summary>View {records.length} {records.length === 1 ? 'record' : 'records'} / validity history</summary>
                                    <div className="table-responsive mt-2" style={{ maxHeight: '24rem' }} tabIndex={0} role="region"
                                        aria-label={`${entry.listCode || 'Insurance'} ${sourceLabel(entry.source)} validity history`}>
                                        <table className="table table-sm mb-0">
                                            <caption>Catalog records, newest first. Historical periods do not imply current coverage.</caption>
                                            <thead>
                                                <tr><th scope="col">Valid from</th><th scope="col">Valid to</th><th scope="col">Conditions / pricing</th></tr>
                                            </thead>
                                            <tbody>
                                                {records.map(record => (
                                                    <tr key={recordKey(record)}>
                                                        <td className="text-nowrap">{displayValue(record.validFrom)}</td>
                                                        <td className="text-nowrap">{displayValue(record.validTo)}</td>
                                                        <td>
                                                            <dl className="mb-0">
                                                                {fields.filter(field => record[field] !== undefined && record[field] !== null).map(field => (
                                                                    <div key={field}><dt className="d-inline">{field}: </dt><dd className="d-inline">{displayValue(record[field])}{' '}</dd></div>
                                                                ))}
                                                            </dl>
                                                        </td>
                                                    </tr>
                                                ))}
                                            </tbody>
                                        </table>
                                    </div>
                                </details>
                            </li>
                        );
                    })}
                </ul>
            )}
        </div>
    );
}

export default InsuranceHistory;
