import { Version } from "@/models/Version";
import { environment } from "@/environment/environment";
import { FormEvent, useState } from "react";

interface ImportButtonProps {
    buttonText?: string;
    titleText?: string;
    defaultUploadSecret?: string;
    fileChangeCallback?: (file: File, uploadSecret: string) => Promise<Version>;
    urlUploadCallback?: (url: string, uploadSecret: string) => Promise<Version>;
}

function ImportButton({ buttonText = '+', fileChangeCallback = () => Promise.resolve({ version: new Date(), id: '1' }), urlUploadCallback, titleText = 'Import file', defaultUploadSecret = environment.defaultUploadSecret }: Readonly<ImportButtonProps>) {

    const [loading, setLoading] = useState(false);
    const [selectedFile, setSelectedFile] = useState<File | undefined>(undefined);
    const [uploadSecret, setUploadSecret] = useState(defaultUploadSecret);
    const [uploadError, setUploadError] = useState<string | undefined>(undefined);
    const [source, setSource] = useState('file');
    const [catalogUrl, setCatalogUrl] = useState('');

    function onFileChange(event: FormEvent<HTMLInputElement>): void {
        const input: HTMLInputElement = event.target as HTMLInputElement;
        const files: FileList | null = input.files;

        if (files && files.length > 0) {
            const file = files[0];
            setSelectedFile(file);
            setUploadError(undefined);
        }
    }

    function onSecretChange(event: FormEvent<HTMLInputElement>): void {
        setUploadSecret((event.target as HTMLInputElement).value);
        setUploadError(undefined);
    }

    async function handleUpload(): Promise<void> {
        if (!uploadSecret || (source === 'file' ? !selectedFile : !catalogUrl.trim())) return;
        if (source === 'url') {
            const input = document.getElementById('catalog-url') as HTMLInputElement | null;
            if (!input?.reportValidity()) return;
        }
        setUploadError(undefined);
        setLoading(true);
        try {
            if (source === 'url') {
                if (!urlUploadCallback) throw new Error('URL imports are unavailable');
                await urlUploadCallback(catalogUrl.trim(), uploadSecret);
            } else if (selectedFile) {
                await fileChangeCallback(selectedFile, uploadSecret);
            }
        } catch (e) {
            // keep the dialog and the selected file so the upload can be retried (e.g. with the right secret)
            setUploadError(e instanceof Error ? e.message : String(e));
            return;
        } finally {
            setLoading(false);
        }
        setSelectedFile(undefined);
        setCatalogUrl('');
        // reset file input value so same file can be selected again
        const input = document.getElementById('file-upload') as HTMLInputElement | null;
        if (input) input.value = '';
        // close modal programmatically by clicking the dismiss button
        const dismiss = document.getElementById('dismiss-modal') as HTMLButtonElement | null;
        dismiss?.click();
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
                            {urlUploadCallback && (
                                <div className="mb-3">
                                    <label htmlFor="upload-source" className="form-label">Import from</label>
                                    <select id="upload-source" className="form-select" value={source} disabled={loading}
                                        onChange={event => { setSource(event.target.value); setUploadError(undefined); }}>
                                        <option value="file">File</option>
                                        <option value="url">URL</option>
                                    </select>
                                </div>
                            )}
                            {source === 'file' ? (
                            <div className="mb-3">
                                <label htmlFor="file-upload" className="form-label">{titleText}</label>
                                <input className="form-control" type="file" id="file-upload" onChange={onFileChange} disabled={loading} />
                                {selectedFile ? (
                                    <div className="small text-muted mt-2">Selected file: <strong>{selectedFile.name}</strong></div>
                                ) : (
                                    <div className="small text-muted mt-2">No file selected</div>
                                )}
                            </div>
                            ) : (
                                <div className="mb-3">
                                    <label htmlFor="catalog-url" className="form-label">Catalog URL</label>
                                    <div id="catalog-url-help" className="form-text mb-2">
                                        HTTPS link to XML or ZIP on a server-approved host. The backend downloads and imports it; keep this dialog open until it finishes.
                                    </div>
                                    <input id="catalog-url" className="form-control" type="url" pattern="https://.*" required
                                        aria-describedby="catalog-url-help" value={catalogUrl} disabled={loading}
                                        onChange={event => { setCatalogUrl(event.target.value); setUploadError(undefined); }} />
                                </div>
                            )}
                            <div className="mb-3">
                                <label htmlFor="upload-secret" className="form-label">Upload secret</label>
                                <input className="form-control" type="password" id="upload-secret" autoComplete="off" spellCheck={false}
                                    value={uploadSecret} onChange={onSecretChange} disabled={loading} required
                                    aria-invalid={uploadError ? true : undefined} aria-describedby="upload-secret-help" />
                                <div id="upload-secret-help" className="form-text">Required by the server to accept uploads.</div>
                            </div>
                            {uploadError && (
                                <div id="upload-error" className="alert alert-danger mb-0" role="alert">
                                    Upload failed: {uploadError}
                                </div>
                            )}
                        </div>
                        <div className="modal-footer">
                            <button id="dismiss-modal" type="button" className="btn btn-secondary" data-bs-dismiss="modal" disabled={loading}>Close</button>
                            <button id="upload-button" type="button" className="btn btn-primary" onClick={handleUpload} disabled={(source === 'file' ? !selectedFile : !catalogUrl.trim()) || !uploadSecret || loading}>
                                {loading ? (
                                    <>
                                        <span id="loading" className="spinner-border spinner-border-sm me-2" aria-hidden="true"></span>{' '}
                                        {source === 'url' ? 'Downloading and importing...' : 'Uploading...'}
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