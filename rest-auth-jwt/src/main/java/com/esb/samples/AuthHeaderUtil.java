/*
 * Copyright 2010-2022
 * AdroitLogic Private Ltd. (https://www.adroitlogic.com). All Rights Reserved.
 *
 * AdroitLogic PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.esb.samples;

import org.adroitlogic.x.api.XMessageContext;
import org.apache.commons.codec.binary.Base64;

public class AuthHeaderUtil {

    static final String AUTH_HEADER = "Authorization";
    static final String USERNAME_KEY = "auth.username";
    static final String ERROR_KEY = "auth.error";

    public static String getAuthHeaderValue(XMessageContext ctx, String authTypeKey) {
        final String authHeader = ctx.getMessage().getFirstStringTransportHeader(AUTH_HEADER)
                .orElseThrow(() -> new RuntimeException(AUTH_HEADER + " header unavailable for " + authTypeKey + "auth"));
        if (!authHeader.startsWith(authTypeKey)) {
            throw new RuntimeException("A " + authTypeKey + AUTH_HEADER + " header is expected");
        }
        return new String(Base64.decodeBase64(authHeader.substring(authTypeKey.length())));
    }
}
