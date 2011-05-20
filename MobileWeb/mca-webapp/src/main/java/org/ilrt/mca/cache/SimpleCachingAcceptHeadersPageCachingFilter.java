/*
 * Copyright (c) 2010, University of Bristol
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1) Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2) Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3) Neither the name of the University of Bristol nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package org.ilrt.mca.cache;

import net.sf.ehcache.constructs.web.filter.SimpleCachingHeadersPageCachingFilter;

import javax.servlet.http.HttpServletRequest;

/**
 * To create a key to cache a request we take into account:
 *
 * 1) The request URI and any query
 * 2) THE HTTP method, e.g. GET or HEAD
 * 3) The "Accept" header
 *
 * We need these modification because otherwise a HEAD request could cache a blank page. Also, a
 * request for RDF could result in all further requests, including HTML, returning RDF.
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class SimpleCachingAcceptHeadersPageCachingFilter extends SimpleCachingHeadersPageCachingFilter {

    @Override
    protected String calculateKey(HttpServletRequest httpRequest) {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(httpRequest.getRequestURI()).append(httpRequest.getQueryString())
                .append(httpRequest.getMethod());

        if (httpRequest.getHeader("accept") != null) {

            String accept = httpRequest.getHeader("accept");

            if (accept.contains("json") || accept.contains("n3") || accept.contains("rdf")) {
                stringBuffer.append(httpRequest.getHeader("accept"));
            }
        }

        return stringBuffer.toString();
    }

}
