package com.bustracking.shared.application.dto;


/*
 * TokensDTO is only an internal backend structure; it is never 
 * serialized to JSON nor exposed to the client. The frontend 
 * only receives the cookies in the headers, and the browser 
 * manages them transparently.
 */

public record TokensDTO(
    String accessToken, 
    String refreshToken) 
{}
