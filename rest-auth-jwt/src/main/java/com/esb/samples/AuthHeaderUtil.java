/*
 * Copyright (c) 2010-2017 AdroitLogic Private Ltd. (https://www.adroitlogic.com). All Rights Reserved.
 *
 * AdroitLogic PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.esb.samples;

import org.adroitlogic.x.api.XMessageContext;

import javax.xml.bind.DatatypeConverter;
import java.util.UUID;

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
        return new String(DatatypeConverter.parseBase64Binary(authHeader.substring(authTypeKey.length())));
    }
}
