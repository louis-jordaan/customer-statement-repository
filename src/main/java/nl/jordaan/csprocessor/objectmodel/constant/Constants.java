package nl.jordaan.csprocessor.objectmodel.constant;

public class Constants {
    public static final String JWT_TOKEN_HEADER = "Authorization";
    public static final String JWT_TOKEN_PREFIX = "Bearer ";
    public static final String DEFAULT_JWT_AUTHORITY_BASE = "CUSTOMER_STATEMENT_PROCESSOR_CLIENT";
    public static final String DEFAULT_JWT_AUTHORITY = "ROLE_" + DEFAULT_JWT_AUTHORITY_BASE;
}
