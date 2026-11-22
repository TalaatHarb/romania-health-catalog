import { fireEvent, render, screen, within } from "@testing-library/react";
import InsuranceHistory from "./InsuranceHistory";
import { DrugDetails } from "@/models/DrugDetails";

describe('InsuranceHistory', () => {
    const base = { listCode: 'A', listDescription: 'Sublista A', percent: 90, source: 'DRUG' };
    const historical = { validFrom: '2025-01-01', validTo: '2025-01-31', referencePrice: 10 };
    const latest = { validFrom: '2026-11-01', referencePrice: 12, needApproval: '1' };

    test('groups repeated summaries while preserving distinct periods and prices', () => {
        const insurance: DrugDetails['insurance'] = [
            { ...base, details: historical },
            { ...base, details: latest },
            { ...base, details: { referencePrice: 10, validTo: '2025-01-31', validFrom: '2025-01-01' } },
            { ...base, source: 'ACTIVE_SUBSTANCE', details: latest },
        ];
        render(<InsuranceHistory insurance={insurance} />);
        expect(screen.getAllByText('A - Sublista A')).toHaveLength(2);
        expect(screen.getByText('Drug')).toBeDefined();
        expect(screen.getByText('Active substance')).toBeDefined();
        const summary = screen.getByText('View 2 records / validity history');
        const details = summary.closest('details')!;
        expect(details.open).toBe(false);
        fireEvent.click(summary);
        expect(details.open).toBe(true);
        const rows = within(details).getAllByRole('row');
        expect(rows).toHaveLength(3);
        expect(rows[1]).toHaveTextContent('2026-11-01');
        expect(rows[1]).toHaveTextContent('12');
        expect(rows[2]).toHaveTextContent('2025-01-31');
        expect(rows[2]).toHaveTextContent('10');
    });

    test('does not merge different percentages or different conditions in the same period', () => {
        render(<InsuranceHistory insurance={[
            { ...base, details: latest },
            { ...base, details: { ...latest, needApproval: '0' } },
            { ...base, percent: 50, details: latest },
        ]} />);
        expect(screen.getByText('90%')).toBeDefined();
        expect(screen.getByText('50%')).toBeDefined();
        expect(screen.getByText('View 2 records / validity history')).toBeDefined();
    });

    test('shows an explicit empty state', () => {
        render(<InsuranceHistory insurance={[]} />);
        expect(screen.getByText('none')).toBeDefined();
        expect(screen.queryByRole('table')).toBeNull();
    });
});
