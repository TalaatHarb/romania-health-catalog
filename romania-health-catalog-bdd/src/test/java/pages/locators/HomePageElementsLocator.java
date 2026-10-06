package pages.locators;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class HomePageElementsLocator {

	@FindBy(xpath = "//*[@id=\"search-bar\"]")
	public WebElement searchBox;

	@FindBy(xpath = "//*[@id=\"search-button\"]")
	public WebElement searchButton;

	@FindBy(xpath = "//*[@id=\"toggle-dropdown\"]")
	public WebElement versionsMenu;

	@FindBy(css = ".version")
	public List<WebElement> versions;
	
	@FindBy(xpath = "//*[@id=\"import-button\"]")
	public WebElement importButton;
	
	@FindBy(xpath = "//*[@id=\"upload-button\"]")
    public WebElement uploadButton;
	
	@FindBy(xpath = "//*[@id=\"dismiss-modal\"]")
	public WebElement closeModalButton;
	
	@FindBy(xpath = "//*[@id=\"file-upload\"]")
	public WebElement fileUploadInput;
	
	@FindBy(xpath = "//*[@id=\"loading\"]")
	public WebElement loadingIndicator;
	
	@FindBy(xpath = "//*[@id=\"importFileModal\"]")
	public WebElement modal;
	
	@FindBy(xpath = "//*[@id=\"drug\"]")
	public WebElement drugView;
	
	@FindBy(css = ".search-result")
	public List<WebElement> searchResults;

	public HomePageElementsLocator(WebDriver webDriver) {
		PageFactory.initElements(webDriver, this);
	}
}
