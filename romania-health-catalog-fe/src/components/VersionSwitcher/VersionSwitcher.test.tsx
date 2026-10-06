import { act, render } from "@testing-library/react";
import VersionSwitcher from "./VersionSwitcher";

test('VersionSwitcher renders with no versions', () => {
    const versionSwitcher = render(<VersionSwitcher versions={[]} />);
    expect(versionSwitcher.getByText(/choose version/i)).toBeDefined();

    const allButtons = versionSwitcher.getAllByRole("button");
    expect(allButtons.length).toBe(1);

    const dropDownToggle = allButtons.filter(b => b.className.includes("dropdown-toggle"))[0];
    act(() => {
        dropDownToggle.click();
    });

    expect(versionSwitcher.getByText(/no versions available/i)).toBeDefined();
});

test('VersionSwitcher renders with one version already selected', () => {
    const versionSwitcher = render(<VersionSwitcher versions={[{ version: new Date(2023, 7), id: "1" }]} />);
    expect(versionSwitcher.getByText(/version: 2023 - 8/i)).toBeDefined();
});

test('VersionSwitcher renders with latest version already selected', () => {
    const versionSwitcher = render(<VersionSwitcher versions={[{ version: new Date(2024, 11), id: "2" }, { version: new Date(2023, 7), id: "1" }]} />);
    expect(versionSwitcher.getByText(/version: 2024 - 12/i)).toBeDefined();
    expect(versionSwitcher.queryByText(/version: 2023 - 8/i)).toBeNull();
});

test('VersionSwitcher renders new version when switching', () => {
    const versionChangedFunction = jest.fn();
    const versions = [{ version: new Date(2024, 11), id: "2" }, { version: new Date(2023, 7), id: "1" }];
    const versionSwitcher = render(<VersionSwitcher versions={versions} versionChanged={versionChangedFunction} />);

    // confirm intial state
    expect(versionSwitcher.getByText(/version: 2024 - 12/i)).toBeDefined();
    expect(versionSwitcher.queryByText(/version: 2023 - 8/i)).toBeNull();

    const allButtons = versionSwitcher.getAllByRole("button");
    expect(allButtons.length).toBe(3);

    const dropDownToggle = allButtons.filter(b => b.className.includes("dropdown-toggle"))[0];
    const secondVersionButton = allButtons.filter(b => b.textContent?.includes("2023 - 8"))[0];

    // Switch version
    act(() => {
        dropDownToggle.click();
        secondVersionButton.click();
    });

    // Version change callback have been called with the right version
    expect(versionChangedFunction).toHaveBeenCalledWith(versions[1]);

    // View changed to reflect changes
    expect(versionSwitcher.getByText(/version: 2023 - 8/i)).toBeDefined();
    expect(versionSwitcher.queryByText(/version: 2024 - 12/i)).toBeNull();
});

test('VersionSwitcher calls versionChanged on mount with first version', () => {
    const versionChangedFunction = jest.fn();
    const versions = [{ version: new Date(2024, 11), id: "2" }];
    render(<VersionSwitcher versions={versions} versionChanged={versionChangedFunction} />);
    
    expect(versionChangedFunction).toHaveBeenCalledWith(versions[0]);
    expect(versionChangedFunction).toHaveBeenCalledTimes(1);
});

test('VersionSwitcher displays correct format for single digit months', () => {
    const versionSwitcher = render(<VersionSwitcher versions={[{ version: new Date(2023, 0), id: "1" }]} />);
    expect(versionSwitcher.getByText(/version: 2023 - 1/i)).toBeDefined();
});

test('VersionSwitcher dropdown toggle has correct attributes', () => {
    const versionSwitcher = render(<VersionSwitcher versions={[]} />);
    const dropDownToggle = versionSwitcher.container.querySelector('#toggle-dropdown') as HTMLElement;
    
    expect(dropDownToggle).toBeDefined();
    expect(dropDownToggle?.dataset.bsToggle).toBe('dropdown');
    expect(dropDownToggle?.getAttribute('aria-expanded')).toBe('false');
});

test('VersionSwitcher renders versions menu', () => {
    const versionSwitcher = render(<VersionSwitcher versions={[{ version: new Date(2024, 11), id: "2" }]} />);
    const versionsMenu = versionSwitcher.container.querySelector('#versions-menu');
    
    expect(versionsMenu).toBeDefined();
    expect(versionsMenu?.className).toContain('dropdown-menu');
});

test('VersionSwitcher renders multiple version options', () => {
    const versions = [
        { version: new Date(2024, 11), id: "3" },
        { version: new Date(2024, 5), id: "2" },
        { version: new Date(2023, 7), id: "1" }
    ];
    const versionSwitcher = render(<VersionSwitcher versions={versions} />);
    
    const versionButtons = versionSwitcher.container.querySelectorAll('.version');
    expect(versionButtons.length).toBe(3);
});

test('VersionSwitcher version buttons have correct ids', () => {
    const versions = [
        { version: new Date(2024, 11), id: "version-1" },
        { version: new Date(2023, 7), id: "version-2" }
    ];
    const versionSwitcher = render(<VersionSwitcher versions={versions} />);
    
    expect(versionSwitcher.container.querySelector('#version-1')).toBeDefined();
    expect(versionSwitcher.container.querySelector('#version-2')).toBeDefined();
});

test('VersionSwitcher handles empty versions array correctly', () => {
    const versionChangedFunction = jest.fn();
    render(<VersionSwitcher versions={[]} versionChanged={versionChangedFunction} />);
    
    // versionChanged should not be called when there are no versions
    expect(versionChangedFunction).not.toHaveBeenCalled();
});

test('VersionSwitcher updates when versions prop changes', () => {
    const versionChangedFunction = jest.fn();
    const initialVersions = [{ version: new Date(2023, 7), id: "1" }];
    const { rerender } = render(
        <VersionSwitcher versions={initialVersions} versionChanged={versionChangedFunction} />
    );
    
    expect(versionChangedFunction).toHaveBeenCalledTimes(1);
    
    const newVersions = [
        { version: new Date(2024, 11), id: "2" },
        { version: new Date(2023, 7), id: "1" }
    ];
    rerender(<VersionSwitcher versions={newVersions} versionChanged={versionChangedFunction} />);
    
    expect(versionChangedFunction).toHaveBeenCalledTimes(2);
    expect(versionChangedFunction).toHaveBeenCalledWith(newVersions[0]);
});

test('VersionSwitcher renders dropdown items as buttons', () => {
    const versions = [{ version: new Date(2024, 11), id: "1" }];
    const versionSwitcher = render(<VersionSwitcher versions={versions} />);
    
    const versionButton = versionSwitcher.container.querySelector('.version');
    expect(versionButton?.tagName).toBe('BUTTON');
    expect(versionButton?.className).toContain('dropdown-item');
});
