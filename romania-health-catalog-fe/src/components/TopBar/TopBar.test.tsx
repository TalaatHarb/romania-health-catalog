import { render, screen } from "@testing-library/react";
import TopBar from "./TopBar";

describe('TopBar', () => {
    test('renders the title', () => {
        render(<TopBar />);
        const title = screen.getByText(/romania health catalog/i);
        expect(title).toBeInTheDocument();
    });

    test('renders as h1 element', () => {
        render(<TopBar />);
        const heading = screen.getByRole('heading', { level: 1 });
        expect(heading).toBeInTheDocument();
        expect(heading).toHaveTextContent('Romania Health Catalog');
    });

    test('matches snapshot', () => {
        const { container } = render(<TopBar />);
        expect(container).toMatchSnapshot();
    });

    test('has correct text content', () => {
        const { container } = render(<TopBar />);
        expect(container.textContent).toBe('Romania Health Catalog');
    });

    test('is visible in the document', () => {
        render(<TopBar />);
        const title = screen.getByText('Romania Health Catalog');
        expect(title).toBeVisible();
    });
});