package educonnect.model.student;

import org.junit.jupiter.api.Test;

import static educonnect.testutil.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LinkTest {
    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Link(null));
    }


    @Test
    public void isValidLink() {
        // null telegram handle
        assertThrows(NullPointerException.class, () -> Link.isValidLink(null));

        // invalid links
        assertFalse(Link.isValidLink("www.murikhao.www.sample.com"));
        assertFalse(Link.isValidLink("www."));
        assertFalse(Link.isValidLink(".com"));
        assertFalse(Link.isValidLink("iamnotmentallysane"));
        assertFalse(Link.isValidLink("https://www..com"));
        assertFalse(Link.isValidLink("https://www.sda."));

        // valid links
        assertTrue(Link.isValidLink("http://www.sample.com/xyz"));
        assertTrue(Link.isValidLink("www.sample.com"));
        assertTrue(Link.isValidLink("sample.com"));
        assertTrue(Link.isValidLink("http://www.sample.com/xyz?abc=dkd&p=q&c=2"));
        assertTrue(Link.isValidLink("https://stackoverflow.com/questions/161738/what-is-th"
                + "e-best-regular-expression-to-check-if-a-string-is-a-valid-url"));

    }

}
