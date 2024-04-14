package educonnect.model.student;

import static educonnect.testutil.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

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
        assertFalse(Link.isValidLink("www."));
        assertFalse(Link.isValidLink(".com"));

        assertFalse(Link.isValidLink("iamnotmentallysane"));
        assertFalse(Link.isValidLink("https://www..com"));
        assertFalse(Link.isValidLink("https://www.sda."));

        // ------Invalid Links------
        // http: should be followed with //
        assertFalse(Link.isValidLink("http:com.com"));

        // port must be between 0..65535
        assertFalse(Link.isValidLink("ftp://user:password@host.dom:port/path"));

        // ssh password needs to be 6 minimum characters (not working)
        assertFalse(Link.isValidLink("ftp://sample:123@wee.com"));

        // main domain name cannot contain @, only a-zA-Z0-9 and hyphens
        assertFalse(Link.isValidLink("www.password@.com"));
        // gTLD name must only contain a-z characters
        assertFalse(Link.isValidLink("http://www.murihao.thr3e"));

        // password cannot contain @ symbol
        assertFalse(Link.isValidLink("ftp://user:pas@sword@host.dom:8080/path"));

        // domain name should not start with -
        assertFalse(Link.isValidLink("www.-sample.com"));

        // domain name should not end with -
        assertFalse(Link.isValidLink("www.sample-.com"));

        // TLD or ccTLD invalid, at least 2 characters for ccTLD and 3 for TLD
        assertFalse(Link.isValidLink("www.sample.u/"));


        // valid links
        assertTrue(Link.isValidLink("www.murikhao.www.sample.com"));
        assertTrue(Link.isValidLink("http://www.sample.com/xyz"));
        assertTrue(Link.isValidLink("www.sample.com"));
        assertTrue(Link.isValidLink("sample.com"));
        assertTrue(Link.isValidLink("http://www.sample.com/xyz?abc=dkd&p=q&c=2"));
        assertTrue(Link.isValidLink("https://stackoverflow.com/questions/161738/what-is-th"
                + "e-best-regular-expression-to-check-if-a-string-is-a-valid-url"));
        assertTrue(Link.isValidLink("https://www.asd.google.com/search?q=some+text&param=3#dfsdf"));
        assertTrue(Link.isValidLink("https://www.google.com"));
        assertTrue(Link.isValidLink("http://google.com/?q=some+text&param=3#dfsdf"));
        assertTrue(Link.isValidLink("https://www.google.com/?q=some+text&param=3&gws_rd=ssl#dfsdf"));
        assertTrue(Link.isValidLink("https://www.google.com/api/?"));
        assertTrue(Link.isValidLink("https://www.google.com/api/login.php"));
        assertTrue(Link.isValidLink("http://www.sample.com"));
        assertTrue(Link.isValidLink("https://www.sample.com"));
        assertTrue(Link.isValidLink("http://www.sample.com/xyz"));
        assertTrue(Link.isValidLink("www.sample.com/xyz/#/xyz"));
        assertTrue(Link.isValidLink("www.sample.com"));
        assertTrue(Link.isValidLink("sample.com"));
        assertTrue(Link.isValidLink("samwwwple.com"));
        assertTrue(Link.isValidLink("http.com"));
        assertTrue(Link.isValidLink("www.sample.vu/"));
        assertTrue(Link.isValidLink("https://stackoverflow.com/questions/42618872/"
                + "regex-for-website-or-url-validation"));
        assertTrue(Link.isValidLink("www.google.com/search?q=cats&oq=cats&gs_lcrp=Eg"
                + "ZjaHJvbWUyBggAEEUYOTIHCAEQABiPAjIHCAIQABiPAjIGCAMQRRg9MgYIBBBFGD3SAQg0NjM0"
                + "ajBqMagCALACAA&sourceid=chrome&ie=UTF-8"));

    }

}
