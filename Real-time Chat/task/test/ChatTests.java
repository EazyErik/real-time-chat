import com.microsoft.playwright.*;
import org.hyperskill.hstest.dynamic.DynamicTest;
import org.hyperskill.hstest.dynamic.input.DynamicTesting;
import org.hyperskill.hstest.stage.SpringTest;
import org.hyperskill.hstest.testcase.CheckResult;
import org.junit.After;
import org.junit.Before;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.hyperskill.hstest.testcase.CheckResult.correct;
import static org.hyperskill.hstest.testcase.CheckResult.wrong;

public class ChatTests extends SpringTest {
    final static Random random = new Random();
    final static Pattern dateLengthPattern = Pattern.compile(".{5,}");
    final static Pattern overflowPattern = Pattern.compile("^(auto|scroll)$");
    final static int TIMEOUT = 10_000;
    final static int NUM_OF_MESSAGES = 7;
    final static String URL = "http://localhost:28852";
    final static String TITLE = "Chat";

    final static String INPUT_MSG_ID_SELECTOR = "#input-msg";
    final static String INPUT_USERNAME_ID_SELECTOR = "#input-username";
    final static String SEND_MSG_BTN_ID_SELECTOR = "#send-msg-btn";
    final static String SEND_USERNAME_BTN_ID_SELECTOR = "#send-username-btn";
    final static String MESSAGES_ID_SELECTOR = "#messages";
    final static String MESSAGE_CONTAINER_CLASS_SELECTOR = ".message-container";
    final static String SENDER_CLASS_SELECTOR = ".sender";
    final static String MESSAGE_CLASS_SELECTOR = ".message";
    final static String DATE_CLASS_SELECTOR = ".date";
    final static String USERS_ID_SELECTOR = "#users";
    final static String USER_CLASS_SELECTOR = ".user";
    final static String INCORRECT_OR_MISSING_TITLE_TAG_ERR = "tag \"title\" should have correct text";

    final static String[] USERNAMES = {
            "A_USER_" + random.nextInt(1000, 10000),
            "B_USER_" + random.nextInt(1000, 10000),
            "C_USER_" + random.nextInt(1000, 10000),
    };
    final static String[] RANDOM_MESSAGES = Stream
            .generate(ChatTests::generateRandomMessage)
            .limit(NUM_OF_MESSAGES)
            .toArray(String[]::new);

    final static ArrayList<String[]> sentMessagesWithSenders = new ArrayList<>();
    final List<String> loginedUsers = new ArrayList<>();

    Playwright playwright;
    Browser browser;
    List<Page> pages = new ArrayList<>();

    @Before
    public void initBrowser() {
        playwright = Playwright.create();

        browser = playwright.firefox().launch(
                new BrowserType
                        .LaunchOptions()
                        .setHeadless(false)
//                        .setSlowMo(15)
                        .setTimeout(1000 * 120));
    }

    @After
    public void closeBrowser() {
        if (playwright != null) {
            playwright.close();
        }
    }

    // Helper functions
    static Page openNewPage(String url, Browser browser, int defaultTimeout) {
        Page page = browser.newContext().newPage();
        page.navigate(url);
        page.setDefaultTimeout(defaultTimeout);
        return page;
    }

    static String generateRandomMessage() {
        return "Test message " + random.nextInt();
    }

    static CheckResult closePage(Page page) {
        page.close();
        return correct();
    }

    // Tests

    @DynamicTest
    DynamicTesting[] dt = new DynamicTesting[]{

            // --- LOGIN TESTS WITH TWO USERS
            // user 0
            () -> {
                pages.add(openNewPage(URL, browser, TIMEOUT));
                return correct();
            },
            () -> testShouldContainProperTitle(pages.get(0), TITLE),
            () -> testElementShouldBeHidden(pages.get(0), INPUT_MSG_ID_SELECTOR),
            () -> testElementShouldBeHidden(pages.get(0), SEND_MSG_BTN_ID_SELECTOR),
            () -> testElementShouldBeHidden(pages.get(0), MESSAGES_ID_SELECTOR),
            () -> testElementShouldBeHidden(pages.get(0), USERS_ID_SELECTOR),
            () -> testFillInputField(pages.get(0), USERNAMES[0], INPUT_USERNAME_ID_SELECTOR),
            () -> {
                loginedUsers.add(USERNAMES[0]);
                return testPressBtn(pages.get(0), SEND_USERNAME_BTN_ID_SELECTOR);
            },
            () -> testUserListShouldHaveProperStructureAndContent(0),

            // user 1
            () -> {
                pages.add(openNewPage(URL, browser, TIMEOUT));
                return correct();
            },
            () -> testShouldContainProperTitle(pages.get(1), TITLE),
            () -> testElementShouldBeHidden(pages.get(1), INPUT_MSG_ID_SELECTOR),
            () -> testElementShouldBeHidden(pages.get(1), SEND_MSG_BTN_ID_SELECTOR),
            () -> testElementShouldBeHidden(pages.get(1), MESSAGES_ID_SELECTOR),
            () -> testElementShouldBeHidden(pages.get(1), USERS_ID_SELECTOR),
            () -> testFillInputField(pages.get(1), USERNAMES[1], INPUT_USERNAME_ID_SELECTOR),
            () -> {
                loginedUsers.add(USERNAMES[1]);
                return testPressBtn(pages.get(1), SEND_USERNAME_BTN_ID_SELECTOR);
            },
            () -> testUserListShouldHaveProperStructureAndContent(1),

            () -> testUserListShouldHaveProperStructureAndContent(0),

            // --- CHAT TESTS WITH TWO USERS
            () -> testElementShouldBeHidden(pages.get(0), INPUT_USERNAME_ID_SELECTOR),
            () -> testElementShouldBeHidden(pages.get(1), INPUT_USERNAME_ID_SELECTOR),
            () -> testElementShouldBeHidden(pages.get(0), SEND_USERNAME_BTN_ID_SELECTOR),
            () -> testElementShouldBeHidden(pages.get(1), SEND_USERNAME_BTN_ID_SELECTOR),
            // message 0
            () -> testFillInputField(pages.get(0), RANDOM_MESSAGES[0], INPUT_MSG_ID_SELECTOR),
            () -> {
                sentMessagesWithSenders.add(new String[]{USERNAMES[0], RANDOM_MESSAGES[0]});
                return testPressBtn(pages.get(0), SEND_MSG_BTN_ID_SELECTOR);
            },
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(0), sentMessagesWithSenders),
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(1), sentMessagesWithSenders),
            // message 1
            () -> testFillInputField(pages.get(1), RANDOM_MESSAGES[1], INPUT_MSG_ID_SELECTOR),
            () -> {
                sentMessagesWithSenders.add(new String[]{USERNAMES[1], RANDOM_MESSAGES[1]});
                return testPressBtn(pages.get(1), SEND_MSG_BTN_ID_SELECTOR);
            },
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(1), sentMessagesWithSenders),
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(0), sentMessagesWithSenders),
            // message 2
            () -> testFillInputField(pages.get(0), RANDOM_MESSAGES[2], INPUT_MSG_ID_SELECTOR),
            () -> {
                sentMessagesWithSenders.add(new String[]{USERNAMES[0], RANDOM_MESSAGES[2]});
                return testPressBtn(pages.get(0), SEND_MSG_BTN_ID_SELECTOR);
            },
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(0), sentMessagesWithSenders),
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(1), sentMessagesWithSenders),
            // message 3
            () -> testFillInputField(pages.get(1), RANDOM_MESSAGES[3], INPUT_MSG_ID_SELECTOR),
            () -> {
                sentMessagesWithSenders.add(new String[]{USERNAMES[1], RANDOM_MESSAGES[3]});
                return testPressBtn(pages.get(1), SEND_MSG_BTN_ID_SELECTOR);
            },
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(1), sentMessagesWithSenders),
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(0), sentMessagesWithSenders),
            // message 4
            () -> testFillInputField(pages.get(0), RANDOM_MESSAGES[4], INPUT_MSG_ID_SELECTOR),
            () -> {
                sentMessagesWithSenders.add(new String[]{USERNAMES[0], RANDOM_MESSAGES[4]});
                return testPressBtn(pages.get(0), SEND_MSG_BTN_ID_SELECTOR);
            },
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(0), sentMessagesWithSenders),
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(1), sentMessagesWithSenders),

            // --- TESTS WITH THREE USERS
            // user 2
            () -> {
                pages.add(openNewPage(URL, browser, TIMEOUT));
                return correct();
            },
            () -> testElementShouldBeHidden(pages.get(2), INPUT_MSG_ID_SELECTOR),
            () -> testElementShouldBeHidden(pages.get(2), SEND_MSG_BTN_ID_SELECTOR),
            () -> testElementShouldBeHidden(pages.get(2), MESSAGES_ID_SELECTOR),
            () -> testElementShouldBeHidden(pages.get(2), USERS_ID_SELECTOR),
            () -> testFillInputField(pages.get(2), USERNAMES[2], INPUT_USERNAME_ID_SELECTOR),
            () -> {
                loginedUsers.add(USERNAMES[2]);
                return testPressBtn(pages.get(2), SEND_USERNAME_BTN_ID_SELECTOR);
            },

            () -> testUserListShouldHaveProperStructureAndContent(2),
            () -> testUserListShouldHaveProperStructureAndContent(1),
            () -> testUserListShouldHaveProperStructureAndContent(0),

            () -> testElementShouldBeHidden(pages.get(2), INPUT_USERNAME_ID_SELECTOR),
            () -> testElementShouldBeHidden(pages.get(2), SEND_USERNAME_BTN_ID_SELECTOR),

            // message 5
            () -> testFillInputField(pages.get(2), RANDOM_MESSAGES[5], INPUT_MSG_ID_SELECTOR),
            () -> {
                sentMessagesWithSenders.add(new String[]{USERNAMES[2], RANDOM_MESSAGES[5]});
                return testPressBtn(pages.get(2), SEND_MSG_BTN_ID_SELECTOR);
            },
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(0), sentMessagesWithSenders),
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(1), sentMessagesWithSenders),
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(2), sentMessagesWithSenders),

            // message 6
            () -> testFillInputField(pages.get(2), RANDOM_MESSAGES[6], INPUT_MSG_ID_SELECTOR),
            () -> {
                sentMessagesWithSenders.add(new String[]{USERNAMES[2], RANDOM_MESSAGES[6]});
                return testPressBtn(pages.get(2), SEND_MSG_BTN_ID_SELECTOR);
            },
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(0), sentMessagesWithSenders),
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(1), sentMessagesWithSenders),
            () -> testUserMessagesShouldHaveProperStructureAndContent(pages.get(2), sentMessagesWithSenders),

            // --- TEST STATE AFTER CLOSING PAGE
            () -> {
                loginedUsers.remove(2);
                return closePage(pages.get(2));
            },
            () -> testUserListShouldHaveProperStructureAndContent(0),
            () -> testUserListShouldHaveProperStructureAndContent(1),
            () -> {
                loginedUsers.remove(1);
                return closePage(pages.get(1));
            },
            () -> testUserListShouldHaveProperStructureAndContent(0),
    };

    CheckResult testShouldContainProperTitle(Page page, String title) {
        return title.equals(page.title()) ? correct() : wrong(INCORRECT_OR_MISSING_TITLE_TAG_ERR);
    }

    CheckResult testElementShouldBeHidden(Page page, String elemSelector) {
        try {
            assertThat(page.locator(elemSelector)).isHidden();
            return correct();
        } catch (AssertionError e) {
            return wrong(e.getMessage());
        }
    }

    CheckResult testFillInputField(Page page, String msg, String inputFieldSelector) {
        try {
            assertThat(page.locator(inputFieldSelector)).isEmpty();
            page.fill(inputFieldSelector, msg);
            return correct();
        } catch (PlaywrightException | AssertionError e) {
            return wrong(e.getMessage());
        }
    }

    CheckResult testPressBtn(Page page, String btnSelector) {
        try {
            page.click(btnSelector);
            return correct();
        } catch (PlaywrightException e) {
            return wrong(e.getMessage());
        }
    }

    CheckResult testUserListShouldHaveProperStructureAndContent(int pageAndUserIndex) {
        int numOfLoginedUsersExceptCurrent = loginedUsers.size() - 1;
        Locator usersLocator = pages.get(pageAndUserIndex).locator(USERS_ID_SELECTOR);
        Locator userLocator = usersLocator.locator(USER_CLASS_SELECTOR);

        try {
            assertThat(usersLocator).hasCSS("overflow-y", overflowPattern);
            assertThat(userLocator).hasCount(numOfLoginedUsersExceptCurrent);

            for (int i = 0, j = 0; i < numOfLoginedUsersExceptCurrent; i++, j++) {
                if (i == pageAndUserIndex) {
                    j++;
                }
                assertThat(userLocator.nth(i)).isVisible();
                assertThat(userLocator.nth(i)).hasText(loginedUsers.get(j));
            }

            return correct();
        } catch (AssertionError e) {
            return wrong(e.getMessage());
        }
    }

    CheckResult testUserMessagesShouldHaveProperStructureAndContent(Page page, List<String[]> sentMessagesWithSenders) {
        Locator messagesLocator = page.locator(MESSAGES_ID_SELECTOR);
        Locator messageContainersLocator = messagesLocator.locator(MESSAGE_CONTAINER_CLASS_SELECTOR);

        try {
            assertThat(messagesLocator).hasCSS("overflow-y", overflowPattern);
            assertThat(messageContainersLocator).hasCount(sentMessagesWithSenders.size());

            for (int i = 0; i < sentMessagesWithSenders.size(); i++) {
                Locator messageContainerLocator = messageContainersLocator.nth(i);

                assertThat(messageContainerLocator.locator(SENDER_CLASS_SELECTOR)).isVisible();
                assertThat(messageContainerLocator.locator(MESSAGE_CLASS_SELECTOR)).isVisible();
                assertThat(messageContainerLocator.locator(DATE_CLASS_SELECTOR)).isVisible();

                assertThat(messageContainerLocator.locator(SENDER_CLASS_SELECTOR)).hasCount(1);
                assertThat(messageContainerLocator.locator(MESSAGE_CLASS_SELECTOR)).hasCount(1);
                assertThat(messageContainerLocator.locator(DATE_CLASS_SELECTOR)).hasCount(1);

                assertThat(messageContainerLocator.locator(SENDER_CLASS_SELECTOR)).hasText(sentMessagesWithSenders.get(i)[0]);
                assertThat(messageContainerLocator.locator(MESSAGE_CLASS_SELECTOR)).hasText(sentMessagesWithSenders.get(i)[1]);
                assertThat(messageContainerLocator.locator(DATE_CLASS_SELECTOR)).hasText(dateLengthPattern);
            }

            return correct();
        } catch (AssertionError e) {
            return wrong(e.getMessage());
        }
    }
}
