import { Version } from "@/models/Version";
import { FormEvent, useState } from "react";

interface ImportButtonProps {
    buttonText?: string;
    titleText?: string;
    fileChangeCallback?: (file: File) => Promise<Version>;
}

function ImportButton({ buttonText = '+', fileChangeCallback = (_file) => Promise.resolve({ version: new Date(), id: '1' }), titleText = 'Import file' }: Readonly<ImportButtonProps>) {

    const [loading, setLoading] = useState(false);
    const [selectedFile, setSelectedFile] = useState<File | undefined>(undefined);

    function onFileChange(event: FormEvent<HTMLInputElement>): void {
        const input: HTMLInputElement = event.target as HTMLInputElement;
        const files: FileList | null = input.files;

        if (files && files.length > 0) {
            const file = files[0];
            setSelectedFile(file);
        }
    }

    async function handleUpload(): Promise<void> {
        if (!selectedFile) return;
        try {
            setLoading(true);
            await fileChangeCallback(selectedFile);
            // close modal programmatically by clicking the dismiss button
            const dismiss = document.getElementById('dismiss-modal') as HTMLButtonElement | null;
            dismiss?.click();
        } finally {
            setLoading(false);
            setSelectedFile(undefined);
            // reset file input value so same file can be selected again
            const input = document.getElementById('file-upload') as HTMLInputElement | null;
            if (input) input.value = '';
        }
    }

    return (
        <>
            <button id="import-button" type="button" className="btn btn-success" data-bs-toggle="modal" data-bs-target="#importFileModal">
                {buttonText}
            </button>

            <div className="modal fade" id="importFileModal" aria-labelledby="importFileModalLabel" aria-hidden="true">
                <div className="modal-dialog">
                    <div className="modal-content">
                        <div className="modal-header">
                            <h5 className="modal-title" id="importFileModalLabel">{titleText}</h5>
                            <button type="button" className="btn-close" data-bs-dismiss="modal" aria-label="Close" disabled={loading}></button>
                        </div>
                        <div className="modal-body">
                            <div className="mb-3">
                                <label htmlFor="file-upload" className="form-label">{titleText}</label>
                                <input className="form-control" type="file" id="file-upload" onChange={onFileChange} disabled={loading} />
                                {selectedFile ? (
                                    <div className="small text-muted mt-2">Selected file: <strong>{selectedFile.name}</strong></div>
                                ) : (
                                    <div className="small text-muted mt-2">No file selected</div>
                                )}
                            </div>
                        </div>
                        <div className="modal-footer">
                            <button id="dismiss-modal" type="button" className="btn btn-secondary" data-bs-dismiss="modal" disabled={loading}>Close</button>
                            <button id="upload-button" type="button" className="btn btn-primary" onClick={handleUpload} disabled={!selectedFile || loading}>
                                {loading ? (
                                    <>
                                        <span id="loading" className="spinner-border spinner-border-sm me-2" aria-hidden="true"></span>{' '}
                                        Uploading...
                                    </>
                                ) : 'Upload'}
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </>
    );
}

export default ImportButton;