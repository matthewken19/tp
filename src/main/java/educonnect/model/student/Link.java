package educonnect.model.student;
import static java.util.Objects.requireNonNull;


/**
 * Represents a Student's link in EduConnect. This link is optional as some classes do not
 * have project links.
 */
public class Link {

    public static final String MESSAGE_CONSTRAINTS = "Please provide a valid website.";

    //    This validation regex is obtained from Mustofa Rizwan at
    //    https://stackoverflow.com/questions/42618872/regex-for-website-or-url-validation
    public static final String VALIDATION_REGEX =
            "^((ftp|http|https):\\/\\/)?(www.)?(?!.*(ftp|http|https|www.))[a-zA-Z0-9_-]+(\\.[a-zA-Z]+)+((\\/)[\\w#]+)" +
                    "*(\\/\\w+\\?[a-zA-Z0-9_]+=\\w+(&[a-zA-Z0-9_]+=\\w+)*)?\\/?$";
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
