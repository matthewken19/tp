package educonnect.model.student;
import static java.util.Objects.requireNonNull;


/**
 * Represents a Student's link in EduConnect. This link is optional as some classes do not
 * have project links.
 */
public class Link {

    public static final String MESSAGE_CONSTRAINTS = "Please provide a valid website.";
    //    @@author tanjiajiajun-reused
    //    Reused from https://stackoverflow.com/questions/42618872/regex-for-website-or-url-validation,
    //    under Mustofa Rizwan's response

    public static final String VALIDATION_REGEX = "^(?<scheme>(?:ftp|https?):\\/\\/)?+(?:(?<username>[a-zA-Z][\\w-.]{0,31})(?::(?<password>[!-~&&[^@$\\n\\r]]{6,255}))?@)?(?<subdomain>(?:[a-zA-Z0-9][a-zA-Z0-9-]{0,61}[a-zA-Z0-9]?\\.){0,127})(?<domain>[a-zA-Z0-9][a-zA-Z0-9-]{1,61}[a-zA-Z0-9])(?<tld>\\.[a-zA-Z]{3,63})(?<cctld>\\.[a-zA-Z]{2})?(?<portnumber>:\\d{1,5})?(?<path>(?:\\/{1,2}[\\w-@.~()%]*)*)(?<querystring>\\?(?:[\\w-%]+=[\\w-?/:@.~!$&'()*+,;=%]+(?:&[\\w-%]+=[\\w-?/:@.~!$&'()*+,;=%]+)*)?)?(?<fragment>#[\\w-?/:@.~!$&'()*+,;=%]+)?$";
    //@@author
    public final String url;

    /**
     * Constructs an {@code Link}.
     *
     * @param url A valid weblink.
     */
    public Link(String url) {
        requireNonNull(url);
        this.url = url;

    }

    public static boolean isValidLink(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Link)) {
            return false;
        }

        Link otherLink = (Link) other;
        return url.equals(otherLink.url);
    }

    @Override
    public String toString() {
        return url;
    }

}
