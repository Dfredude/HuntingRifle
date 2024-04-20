package org.example;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class LinkedInDriver {
    public static WebDriver driver = getDriver();
    public static JavascriptExecutor js;
    private final static DataTeller dataTeller = new DataTeller();
    static Wait<WebDriver> wait;
    private final static String SearchQuery = "Software Developer";
    //    private final static String SearchQuery = "Javascript";
//    private final static String SearchQuery = "Junior Developer";
//    private final static String location = "Canada";
//    private final static String location = "Los Angeles, California, United States";
    private final static String location = "Halifax, Nova Scotia, Canada";
    //    private final static String location = "Ontario, Canada";
    private final static JobsContext jobsContext = new JobsContext();

    public static class JobPage {
        private static void goToJobPage(String job_id){
            driver.get("https://www.linkedin.com/jobs/view/" + job_id);
        }

        public static void applyToJob(String job_id){
            if (job_id == null){
                throw new NullPointerException("Must have a job_id to apply to a job");
            }
            goToJobPage(job_id);
            // String xPathSelector = "//button[@data-job-id='" + job_id + "']";
            By easyApplyCSSSelector = By.cssSelector("button[data-job-id]");
            // Wait for page to load
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
            List<WebElement> applyBtns = driver.findElements(easyApplyCSSSelector);
            boolean isEasyApply = applyBtns.size() > 0;
            if (!isEasyApply){
                System.out.println("Not an Easy Apply job");
                return;
            }
            // Click on Easy Apply button
            wait = getWait();
            wait.until(d -> d.findElement(easyApplyCSSSelector).isEnabled());
            applyBtns = driver.findElements(easyApplyCSSSelector);
            WebElement apply_btn = applyBtns.get(applyBtns.size() - 1);

            // Workaround for clicking on button
            js.executeScript("arguments[0].scrollIntoView(true);", apply_btn);
            js.executeScript("arguments[0].click();", apply_btn);

            // Continue with application until submit button
            while (driver.findElements(By.cssSelector("[data-easy-apply-next-button]")).size() > 0){
                try {
                    handleEmptyInputs();
                    WebElement next_btn = driver.findElement(By.cssSelector("[data-easy-apply-next-button]"));
                    js.executeScript("arguments[0].scrollIntoView(true);", next_btn);
                    next_btn.click();
                    System.out.println("Loading next page");
                } catch (NoSuchElementException e) {
                    System.out.println("No next button found");
                    System.out.println("Error: " + e.getMessage());
                    // Wait for user to fill out fields
                }
            }

            // Check for input fields in last page
            handleEmptyInputs();

            // Check if there's a review button
            if (driver.findElements(By.cssSelector("[aria-label='Review your application']")).size() > 0){
                WebElement review_btn = driver.findElement(By.cssSelector("[aria-label='Review your application']"));
                review_btn.click();
            }

            // Submit application and uncheck follow company beforehand
            WebElement follow_company = driver.findElement(By.id("follow-company-checkbox"));
            js.executeScript("arguments[0].scrollIntoView(true);", follow_company);
            if (follow_company.isSelected()){
                // JS workaround
                js.executeScript("arguments[0].click();", follow_company);
//                follow_company.click();
            }
            WebElement submitBtn = driver.findElement(By.xpath("//button[@aria-label='Submit application']"));
            try {
                submitBtn.click();
                System.out.println("Submitted application");
                jobsContext.increaseCounter(job_id);

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }



        }

        private static void handleEmptyInputs() {
            // Check for text input fields
            List<WebElement> input_fields = driver.findElements(By.cssSelector(
                    "input[id*=form-component-formElement-urn-li-jobs-applyformcommon-easyApplyFormElement"));
            for (WebElement input_field : input_fields) {
                if (input_field.getAttribute("value").equals("") && input_field.getAttribute("required") != null) {
                    // Check if data is available in yml file
                    WebElement label;
                    try {
                        label = input_field.findElement(By.xpath("./preceding-sibling::label"));
                    } catch (NoSuchElementException e) {
                        // If no label, then it's a single-typehead-entity
                        label = input_field.findElement(By.xpath("../preceding-sibling::label/span"));
                    }
                    String field_question = label.getText();
                    try {
                        String field_value = dataTeller.getAnswer(field_question);
                        if (field_value != null) {
                            input_field.sendKeys(field_value);
                            // If it's a single-typehead-entity, then escape dropdown
                            if (input_field.getAttribute("id").contains("single-typeahead-entity")) {
                                input_field.sendKeys(Keys.ESCAPE);
                                // Wait for dropdown to close by itself
                                wait = getWait();
                                wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div[id^=typeahead-overlay]")));
                            }
                        } else {
                            throw new Exception("Text Field requires your attention: " + field_question);
                        }
                    } catch (Exception e) {
                        String alertScript = "alert(\"" + e.getMessage() + "\")";
                        js.executeScript(alertScript);
                    }

                }
            }

            // Check for empty radio select fields
            List<WebElement> radio_selects_field_sets = driver.findElements(By.cssSelector("fieldset[id^=radio-button-form-component-formElement-urn-li-jobs-applyformcommon-easyApplyFormElement]"));
            for (WebElement radio_select_field_set : radio_selects_field_sets){
                List<WebElement> radio_selects = radio_select_field_set.findElements(By.cssSelector("input[type=radio]"));
                WebElement radio_select_y = radio_selects.get(0);
                WebElement radio_select_n = radio_selects.get(1);
                if (radio_select_y.isSelected() || radio_select_n.isSelected()) continue;
                WebElement span = radio_select_field_set.findElement(By.cssSelector("[data-test-form-builder-radio-button-form-component__title]>span[aria-hidden=true]"));
                String field_name = span.getText();
                try {
                    String field_value = dataTeller.getAnswer(field_name);
                    if (field_value != null){
                        boolean isYes = field_value.equals("Yes");
                        try{
                            if (field_value.equals("Yes")){
                                isYes = true;
                                js.executeScript("arguments[0].scrollIntoView(true);", radio_select_y);
                                radio_select_y.click();
                            } else if (field_value.equals("No")){
                                isYes = false;
                                js.executeScript("arguments[0].scrollIntoView(true);", radio_select_n);
                                radio_select_n.click();
                            }
                        } catch (Exception e){
                            System.out.println(e.getMessage());
                            if (isYes) js.executeScript("arguments[0].click();", radio_select_y);
                            else js.executeScript("arguments[0].click();", radio_select_n);

                        }

                    } else {
                        throw new Exception("Radio Select requires your attention: " + field_name);
                    }
                } catch (Exception e){
                    String alertScript = "alert(\"" + e.getMessage() + "\")";
                    js.executeScript(alertScript);
                }

            }

            // Check for checkbox fields
            List<WebElement> checkbox_fields = driver.findElements(By.cssSelector("input[type=checkbox]"));
            for(WebElement checkbox_field : checkbox_fields){
                if (checkbox_field.isSelected()) continue;
                String field_name = checkbox_field.findElement(By.xpath("./following-sibling::label")).getText();
                if (field_name.equals("")) continue;
                if (checkbox_field.getAttribute("required") != null || field_name.toLowerCase().contains("terms")) {
                    js.executeScript("arguments[0].scrollIntoView(true);", checkbox_field);
                    js.executeScript("arguments[0].click();", checkbox_field);
                }
            }

            // Check for empty dropdown fields
            List<WebElement> dropdown_fields = driver.findElements(By.cssSelector("select"));
            for (WebElement dropdown_field : dropdown_fields){
                if (dropdown_field.getAttribute("required") != null && (dropdown_field.getAttribute("value").startsWith("Selec") || dropdown_field.getAttribute("value").equals("") || dropdown_field.getAttribute("value") == null)){
                    // Find label span
                    Select dropdown = new Select(dropdown_field);
                    WebElement lbl = dropdown_field.findElement(By.xpath("./preceding-sibling::label"));
                    WebElement lblText = lbl.findElement(By.xpath("./span[1]"));
                    String field_name = lblText.getText();
                    String field_value = dataTeller.getAnswer(field_name);
                    try{
                        dropdown.selectByVisibleText(field_value);
                    } catch (Exception e){
                        // Check options for select
                        List<WebElement> options = dropdown.getOptions();
                        // Select first option
                        dropdown.selectByVisibleText(options.get(1).getText());
                        System.out.println("<Select> - " + field_name + " requires your attention");
                        System.out.println("Selected first option: " + options.size());
                    }
                }
            }
        }
    }

    public static void loginManually(){
        // Go to login page
        driver.get("https://www.linkedin.com/login");
        // Logging manually by sending credentials
        String email = dataTeller.getEmail();
        String password = dataTeller.getPassword();
        driver.findElement(By.id("session_key")).sendKeys(email);
        driver.findElement(By.id("session_password")).sendKeys(password);
        driver.findElement(By.cssSelector("[type='submit'")).click();
    }

    public LinkedInDriver() {
        // Initialize JS Executor
        js = (JavascriptExecutor) driver;

        // Initialize DriverWait
        wait = getWait();

        // Program starts here
        driver.get("https://linkedin.com/jobs");
        logIn();
        // driver.findElement(By.cssSelector("[href='https://www.linkedin.com/jobs/?']")).click();
        // Wait
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        // Type to search for software in Halifax
        WebElement search_box = driver.findElement(By.cssSelector("[id^=jobs-search-box-keyword-id]"));
        // search_box.click();
        search_box.sendKeys(SearchQuery);
        wait.until(d -> d.findElement(By.cssSelector("[id^=jobs-search-box-location-id]")).isDisplayed());
        WebElement location_box = driver.findElement(By.cssSelector("[id^=jobs-search-box-location-id]"));
        // location_box.click();
        location_box.sendKeys(location);
        WebElement search_btn = driver.findElement(By.cssSelector(".jobs-search-box__submit-button"));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        js.executeScript("arguments[0].click();", search_btn); // Search for jobs
        // Get all jobs available
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5)); // Wait for jobs URL to load
        // Filter for Easy Apply jobs
        wait.until(d -> d.getCurrentUrl().equals("https://www.linkedin.com/jobs/") == false);
        wait.until(d -> ((JavascriptExecutor) d).executeScript("return document.readyState").equals("complete"));
        String c = driver.getCurrentUrl();
        driver.get(c + "&f_AL=true");
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2)); // Wait for jobs to load
        List<WebElement> jobs = driver.findElements(By.cssSelector("[data-occludable-job-id]"));
        // Store all jobs
        List<String> job_ids = new java.util.ArrayList<String>();
        try {
            job_ids = retrieveJobIDs(jobs);
        } catch (Exception e){
            System.out.println("Error getting job ids: Trying to find elements once again");
            jobs = driver.findElements(By.cssSelector("[data-occludable-job-id]"));
            job_ids = retrieveJobIDs(jobs);
        }

        // Apply to all jobs
        try {
            for (String job_id : job_ids) {
                JobPage.applyToJob(job_id);
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
        } finally {
            // driver.quit();
        }
    }
    public static void logIn(){
        // li_at Set cookie session
        String cookie = dataTeller.getCookie();
        if (cookie != null) {
            try {
                driver.manage().addCookie(new org.openqa.selenium.Cookie("li_at", cookie));
            } catch (Exception e){
                System.out.println("Error setting cookie");
                System.out.println(e.getMessage());
            }
        } else {
            try {
                loginManually();
                cookie = driver.manage().getCookieNamed("li_at").getValue();
                // dataTeller.setCookie(cookie); TODO: Create a method to set cookie
            } catch (Exception e){
                System.out.println("Error logging in manually");
                System.out.println(e.getMessage());
            }
        }

        // Refresh
        driver.navigate().refresh();
    }
    public static WebDriver getDriver(){
        if (driver == null){
            ChromeOptions options = (ChromeOptions) new ChromeOptions().setBinary("C:\\chrome-win64\\chrome.exe");
            driver = new ChromeDriver(options);
        }
        return driver;
    }

    public static JavascriptExecutor getJS(){
        if (js == null){
            js = (JavascriptExecutor) driver;
        }
        return js;
    }

    public static WebDriverWait getWait() {
        if (wait == null) {
            wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        }
        return (WebDriverWait) wait;
    }

    public static List<String> retrieveJobIDs(List<WebElement> jobs_elements){
        List<String> job_ids = new java.util.ArrayList<String>();
        for (WebElement job : jobs_elements) {
            // Scroll into view
            js.executeScript("arguments[0].scrollIntoView(true);", job);
            String jobTitle = job.findElement(By.cssSelector("[data-control-id]")).getText().toLowerCase();
            // Skip jobs that contain keywords
            List<String> keywords = dataTeller.getUnwantedKeywords();
            boolean skip = false;
            for (String keyword: keywords) {
                if (jobTitle.contains(keyword.toLowerCase())){
                    System.out.println("Skipping job: " + jobTitle);
                    skip = true;
                    break;
                };
            }
            if (skip) continue;
            // Wait for job to be displayed properly
            wait.until(d -> job.isDisplayed());
            String job_id = "";
            job_id = job.getAttribute("data-occludable-job-id");


            if (job_id != null) job_ids.add(job_id);
            else {
                System.out.println("Job ID is null");
            }
        }
        return job_ids;
    }
}
