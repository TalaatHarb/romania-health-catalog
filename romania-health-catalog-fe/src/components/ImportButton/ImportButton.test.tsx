import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import ImportButton from "./ImportButton";

describe('ImportButton', () => {
    test('renders with default props', () => {
        render(<ImportButton />);
        
        const button = screen.getByRole('button', { name: '+' });
        expect(button).toBeDefined();
    });

    test('renders with custom button text', () => {
        render(<ImportButton buttonText="Import CSV" />);
        
        const button = screen.getByRole('button', { name: 'Import CSV' });
        expect(button).toBeDefined();
    });

    test('opens modal when button is clicked', () => {
        const { container } = render(<ImportButton titleText="Upload Data File" />);
        
        const button = screen.getByRole('button', { name: '+' });
        fireEvent.click(button);
        
        const modalTitle = container.querySelector('#importFileModalLabel');
        expect(modalTitle).toHaveTextContent('Upload Data File');
    });

    test('shows no file selected initially', () => {
        render(<ImportButton />);
        
        const button = screen.getByRole('button', { name: '+' });
        fireEvent.click(button);
        
        expect(screen.getByText('No file selected')).toBeDefined();
    });

    test('shows selected file name after file selection', () => {
        render(<ImportButton />);
        
        const button = screen.getByRole('button', { name: '+' });
        fireEvent.click(button);
        
        const fileInput = document.getElementById('file-upload') as HTMLInputElement;
        const file = new File(['test content'], 'test.csv', { type: 'text/csv' });
        
        Object.defineProperty(fileInput, 'files', {
            value: [file],
            writable: false,
        });
        
        fireEvent.change(fileInput);
        
        expect(screen.getByText(/test.csv/)).toBeDefined();
    });

    test('upload button is disabled when no file is selected', () => {
        render(<ImportButton />);
        
        const button = screen.getByRole('button', { name: '+' });
        fireEvent.click(button);
        
        const uploadButton = document.getElementById('upload-button') as HTMLButtonElement;
        expect(uploadButton.disabled).toBe(true);
    });

    test('upload button is enabled when file is selected', () => {
        render(<ImportButton />);
        
        const button = screen.getByRole('button', { name: '+' });
        fireEvent.click(button);
        
        const fileInput = document.getElementById('file-upload') as HTMLInputElement;
        const file = new File(['test content'], 'test.csv', { type: 'text/csv' });
        
        Object.defineProperty(fileInput, 'files', {
            value: [file],
            writable: false,
        });
        
        fireEvent.change(fileInput);
        
        const uploadButton = document.getElementById('upload-button') as HTMLButtonElement;
        expect(uploadButton.disabled).toBe(false);
    });

    test('calls fileChangeCallback when upload is clicked', async () => {
        const mockCallback = jest.fn().mockResolvedValue({ version: new Date(), id: '1' });
        render(<ImportButton fileChangeCallback={mockCallback} />);
        
        const button = screen.getByRole('button', { name: '+' });
        fireEvent.click(button);
        
        const fileInput = document.getElementById('file-upload') as HTMLInputElement;
        const file = new File(['test content'], 'test.csv', { type: 'text/csv' });
        
        Object.defineProperty(fileInput, 'files', {
            value: [file],
            writable: false,
        });
        
        fireEvent.change(fileInput);
        
        const uploadButton = document.getElementById('upload-button') as HTMLButtonElement;
        fireEvent.click(uploadButton);
        
        await waitFor(() => {
            expect(mockCallback).toHaveBeenCalledWith(file);
        });
    });

    test('shows loading state during upload', async () => {
        const mockCallback = jest.fn().mockImplementation(() => new Promise(resolve => setTimeout(resolve, 100)));
        render(<ImportButton fileChangeCallback={mockCallback} />);
        
        const button = screen.getByRole('button', { name: '+' });
        fireEvent.click(button);
        
        const fileInput = document.getElementById('file-upload') as HTMLInputElement;
        const file = new File(['test content'], 'test.csv', { type: 'text/csv' });
        
        Object.defineProperty(fileInput, 'files', {
            value: [file],
            writable: false,
        });
        
        fireEvent.change(fileInput);
        
        const uploadButton = document.getElementById('upload-button') as HTMLButtonElement;
        fireEvent.click(uploadButton);
        
        expect(screen.getByText('Uploading...')).toBeDefined();
        expect(document.getElementById('loading')).toBeDefined();
    });

    test('disables buttons during upload', async () => {
        const mockCallback = jest.fn().mockImplementation(() => new Promise(resolve => setTimeout(resolve, 100)));
        render(<ImportButton fileChangeCallback={mockCallback} />);
        
        const button = screen.getByRole('button', { name: '+' });
        fireEvent.click(button);
        
        const fileInput = document.getElementById('file-upload') as HTMLInputElement;
        const file = new File(['test content'], 'test.csv', { type: 'text/csv' });
        
        Object.defineProperty(fileInput, 'files', {
            value: [file],
            writable: false,
        });
        
        fireEvent.change(fileInput);
        
        const uploadButton = document.getElementById('upload-button') as HTMLButtonElement;
        const closeButton = document.getElementById('dismiss-modal') as HTMLButtonElement;
        
        fireEvent.click(uploadButton);
        
        expect(uploadButton.disabled).toBe(true);
        expect(closeButton.disabled).toBe(true);
        expect(fileInput.disabled).toBe(true);
    });

    test('does not upload when no file is selected', async () => {
        const mockCallback = jest.fn().mockResolvedValue({ version: new Date(), id: '1' });
        render(<ImportButton fileChangeCallback={mockCallback} />);
        
        const button = screen.getByRole('button', { name: '+' });
        fireEvent.click(button);
        
        const uploadButton = document.getElementById('upload-button') as HTMLButtonElement;
        fireEvent.click(uploadButton);
        
        await waitFor(() => {
            expect(mockCallback).not.toHaveBeenCalled();
        });
    });
});
