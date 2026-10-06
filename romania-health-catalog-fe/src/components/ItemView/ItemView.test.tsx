import { render, screen } from "@testing-library/react";
import ItemView from "./ItemView";
import { humanize } from "@/utils/humanize";
import { CatalogItem } from "@/models/CatalogItem";

describe('ItemView', () => {
    const item: CatalogItem = {
        id: 'p1',
        type: 'PHYSICIAN',
        label: 'Physicians',
        code: 'E98533',
        name: 'COSTEA ION',
        details: { stencilNo: 'E98533', lastName: 'COSTEA', firstName: 'ION', isActive: true, validTo: null }
    };

    test('renders placeholder when no item is selected', () => {
        render(<ItemView typeLabel="Cities" />);
        expect(screen.getByText('Details')).toBeDefined();
        expect(screen.getByText(/select one of the cities/i)).toBeDefined();
        expect(document.getElementById('item-card')).toBeNull();
    });

    test('renders item details', () => {
        render(<ItemView item={item} />);

        expect(document.getElementById('item-card')).not.toBeNull();
        expect(screen.getByText('COSTEA ION')).toBeDefined();
        expect(screen.getByText('Code: E98533')).toBeDefined();
        expect(screen.getByText('Physicians')).toBeDefined();
        expect(screen.getByText('Stencil no')).toBeDefined();
        expect(screen.getByText('Last name')).toBeDefined();
        expect(screen.getByText('Yes')).toBeDefined();
        expect(screen.getByText('-')).toBeDefined();
    });

    test('falls back to code when name is missing', () => {
        render(<ItemView item={{ ...item, name: undefined }} />);
        expect(screen.getByRole('heading', { level: 5 }).textContent).toBe('E98533');
    });

    test.each([
        ['cityCode', 'City code'],
        ['name', 'Name'],
        ['atcCode', 'Atc code'],
        ['personPID', 'Person pid'],
        ['ICD10Code', 'Icd10 code'],
    ])('humanize(%s) = %s', (key, expected) => {
        expect(humanize(key)).toBe(expected);
    });
});
