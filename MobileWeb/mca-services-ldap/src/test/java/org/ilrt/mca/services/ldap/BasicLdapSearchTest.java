package org.ilrt.mca.services.ldap;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BasicLdapSearchTest {


    @Test
    public void testNumbers() {

        BasicLdapSearch search = new BasicLdapSearch(null, null);

        assertEquals("Unexpected number format", NUMBER_ONE_AFTER,
                search.extractNumber(NUMBER_ONE_BEFORE));

        assertEquals("Unexpected number format", NUMBER_TWO_AFTER,
                search.extractNumber(NUMBER_TWO_BEFORE));
    }

    private final String NUMBER_ONE_BEFORE = "+44 117 331 4406 or x14406";
    private final String NUMBER_ONE_AFTER = "+441173314406";

    private final String NUMBER_TWO_BEFORE = "+44 117 331 4406";
    private final String NUMBER_TWO_AFTER = "+441173314406";

}
