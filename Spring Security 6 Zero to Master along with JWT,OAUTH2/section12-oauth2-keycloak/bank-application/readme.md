# Backend rest services
## Services without security
  - `/contact` - this service should accept the details from the contact us page in the UI and save to the DB
  - `/notices` - this service should send the notice details from the DB to the notices page in the UI
  
## Service with security
  - `/myAccount` - this service should send the account details of the logged in use from the DB to the UI
  - `/myBalance` - this service should send the balance of transaction details of the logged in user from the DB to the UI
  - `/myLoans` - this service should send the loan details of the logged in user from the DB to the UI
  - `/myCards` - this service should send the cards details of the logged in user from the DB to the UI

## Configuring security

### The default security filter chain

By default all paths are secured because of the defaultSecurityFilterChain in SpringBootWebSecurityConfiguration class 
This class is in  the org.springframework.boot:spring-boot-autoconfigure:3.1.4 dependency

     @Bean
     @Order(SecurityProperties.BASIC_AUTH_ORDER)
     SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
         http.authorizeHttpRequests((requests) -> requests.anyRequest().authenticated());
     	 http.formLogin(withDefaults());
     	 http.httpBasic(withDefaults());
     	 return http.build();
     }

### Step 1a: Configuring the security filter chain
We can create a more custom security filter chain in a configuration class. 
1. Copy the method from SpringBootWebSecurityConfiguration
2. Create a AppSecurityConfig class annotated as configuration 
3. Add the same bean (without Order annotation)


    @Configuration
    public class AppSecurityConfig {
        @Bean
        SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
            http.authorizeHttpRequests((requests) -> requests.anyRequest().authenticated());
            http.formLogin(withDefaults());
            http.httpBasic(withDefaults());
            return http.build();
        }
    
    }

In debut mode, the defaultSecurityFilter chain is called. 

![img.png](img.png)


### Step 1b: Customizing the security filter chain with secured and unsecured routes
We pass request matcher that are string patterns for routes requiring authentication and routes that are 
permitted to all users.

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((requests) -> requests
                .requestMatchers("/myAccount", "/myBalance", "/myLoans", "/myCards").authenticated()
                .requestMatchers("/notices", "/contact").permitAll());
        http.formLogin(withDefaults());
        http.httpBasic(withDefaults());
        return http.build();
    }

the form login and httpBasic and build can be added to the first line: 

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests((requests) -> requests
                .requestMatchers("/myAccount", "/myBalance", "/myLoans", "/myCards").authenticated()
                .requestMatchers("/notices", "/contact").permitAll())
                .formLogin(withDefaults())
                .httpBasic(withDefaults())
                .build();
    }

### Step 2: managing users
At this stage, only one user can access the application.

    spring.security.user.name=root
    spring.security.user.password=root

We can configure multiple users and store credentials in application memory or into database and give more flexibility. 

First approach allow to manage users using in memory storage: 
Two solutions for this approach : 

    @Bean
    InMemoryUserDetailsManager userDetailsService() {
        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("admin")
               .password("12345")
               .authorities("admin")
               .build();
    
      UserDetails user = User.withDefaultPasswordEncoder()
             .username("user")
             .password("12345")
             .authorities("read")
             .build();
     return new InMemoryUserDetailsManager(admin, user);
    }

or: 

     @Bean
     InMemoryUserDetailsManager userDetailsService() {
         InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager();

         UserDetails admin = User.withUsername("admin")
                .password("12345")
                .authorities("admin")
                .build();

         UserDetails user = User.withUsername("user")
                 .password("12345")
                 .authorities("read")
                 .build();
         inMemoryUserDetailsManager.createUser(admin);
         inMemoryUserDetailsManager.createUser(user);
      return inMemoryUserDetailsManager;
     }

     @Bean
    PasswordEncoder passwordEncoder() {
         return NoOpPasswordEncoder.getInstance();
     }

Note: those 2 approaches should not be used for production, just for test purpose.
- UserDetails - service is the core interface which loads user specific data 
- UserDetailsManager - extends the UserDetailsService and allow user creation and update
- InMemoryUserDetailsManager is an implementation of the Managern as well as JdbcUserDetailsManager
  or LdapUsersDetailsManager


### Step 3: managing password with PasswordEncoder

Storing passwords with plain text in a storage like database may cause integrity and confidentiality issues. 
- DB can be accessed by DB Admins and could get credentials.
- Hackers could access to database too

3 options are available for password management
- encoding: 
  - defined as the process of converting data from one form to another and has nothing to do with cryptography
  - it envolves no secret and completely reversible
  - encoding can't be used for securing data
  - Some encodind algorithms: ASCII, base64, unicode
- encryption:
  - defined as the process of transforming data in such a way that guarantees confidentiality
  - to achieve confidentiality, encryption requires the use of a secret which in cryptographic terms is called a key
  - encryption can be reversible by using decryption with the help of the key. As long as the key is confidential, 
    encryption can be considered as secured
- hashing:
  - in hashing, data is converted to the hash value using hashing function
  - data once hashed is non-reversible. One cannot determine the original data from a hash value generated
  - given some arbitrary data along with the output, one can verify whether data matches the original input data without
    needing to see the original data

PasswordEncoder has 3 methods:
- endcode
- matches
- updagradeEncoding

Several password encoders implementations are available:
- **NoOpPasswordEndoder (not recommended)**
- **StandardPasswordEncoder (not recommended)**
- **Pbkdf2PasswordEncoder**: The algorithm is invoked on the concatenated bytes of the salt, secret and password (not really recommended).
  - a configurable random salt value length (default is 16 bytes)
  - a configurable number of iterations (default is 310000) 
  - a configurable key derivation function (see Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm)
  - a configurable secret appended to the random salt (default is empty)
- **BCryptPasswordEncoder**:  Implementation of PasswordEncoder that uses the BCrypt strong hashing function. Clients 
    can optionally supply a "version" ($2a, $2b, $2y) and a "strength" (a.k.a. log rounds in BCrypt) and a SecureRandom 
    instance. The larger the strength parameter the more work will have to be done (exponentially) to hash the 
    passwords. The default value is 10.
- **SCryptPasswordEncoder**: Implementation of PasswordEncoder that uses the SCrypt hashing function. Clients can 
    optionally supply a cpu cost parameter, a memory cost parameter and a parallelization parameter. The currently 
    implementation uses Bouncy castle which does not exploit parallelism/optimizations that password crackers will, so 
    there is an unnecessary asymmetry between attacker and defender. Scrypt is based on Salsa20 which performs poorly in 
    Java (on par with AES) but performs awesome (~4-5x faster) on SIMD capable platforms.
- **Argon2PasswordEncoder**: Implementation of PasswordEncoder that uses the Argon2 hashing function. Clients can  
    optionally supply the length of the salt to use, the length of the generated hash, a cpu cost parameter, a memory 
    cost parameter and a parallelization parameter. Note: The currently implementation uses Bouncy castle which does 
    not exploit parallelism/optimizations that password crackers will, so there is an unnecessary  asymmetry between 
    attacker and defender.

BCryptPasswordEncoder is the most recommended encoder for passwords. Argon2 do consumes more memory.  


### Step 4: custom authentication logic 
 
We may want to implement a custom authentication provider. We can also have several authentication provider using 
password & login, using OAUth, using OTP also...

Interface AuthenticationProvider provides 2 abstract methods: 
- authenticate
- supports (boolean): we inform spring security that the authentication provider is supporter

### Step 5: managing CORS issue

CORS is a protocol that enables script running on a browser client to interact with resources from different origin.
For example if an UI app wish to make an API call running on a different domain, it would be blocked from doing so by
default due to CORS. It is a specification from W3C implemented in most browsers. 

So, CORS is not a security issue / attack but the default protection provided by browsers to stop sharing the data / 
communication between differents origins. 

Other origins means: 
- a different SCHEME (http, https)
- a different domain
- a different port

**>> Solution 1: Using @CrossOrigin annotation at controller level :** 

    @CrossOrigin(origins="http://localhost:4200") 

If we want to allow all origins: 

    @CrossOrigin(origins="*")

A preflight call will be sent to the backend that will return authorization to be called.
This approach is not adapted to project containing many entry points. 

**>> Solution 2: Global configuration :**

We can set a CorsConfigurationSource to be used to define the defaultSecurityFilterChain

    private CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration corsConfiguration = new CorsConfiguration();
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        corsConfiguration.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
        // corsConfiguration.setAllowedOriginPatterns(Collections.singletonList("*"));
        corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
        corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
        corsConfiguration.setAllowCredentials(Boolean.TRUE);
        corsConfiguration.setMaxAge(3600L);
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

In the defaultSecurityFilterChain method : 

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(corsCustomizer -> corsCustomizer.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((requests) -> requests
                .requestMatchers("/myAccount", "/myBalance", "/myLoans", "/myCards", "/users").authenticated()
                .requestMatchers("/notices", "/contact", "/auth/register").permitAll())
                .formLogin(withDefaults())
                .httpBasic(withDefaults())
                .build();
    }


### Step 6: managing CSRF issue

A typical Cross Site Request Forgery (CSRF or XSTF) attack aims to perform an operation in a web application on behalf 
of a user without their explicit consent. In general, it doesn't directly steal the user's identity but it exploits the
user to carry out an action without their will. 

Let's consider we are using a site like Netflix.com and the attacker is: evil.com (very nasty).
- The netflix user login to netflix.com and the backend server of netflix will provide a cookie which will store in the 
  browser agains the domain name netflix.com
- the same netflix user opens evil.com website in another tab: let's consider the web page contains a link that can change
  email of the netflix account (fake link on some nice proposal like 90% off on Iphone)
- user tempted and click on malicious link which makes a request to Netflix.com And since login cookie is present in the
  same browser and the request to change email is beeing made on netflix.com, the backend should not differentiate from 
  where the request came. So here the evil.com forged the request as it was coming from netflix.com UI page... 

This is how CSRF works in a theoretical level.

**Solution**

To defeat a CSRF attacj, application need a way to determine if the HTTP request is legitimately generated via the 
application's user interface. The best way to achieve this is through a CSRF token. A CSRF token is a secure random
String that is used to prevent CSRF attacks. The token need to be unique per session and should be of large random 
value to make it hard to guess

- Step 1: the netflix use login to netflix.com and the backend server of netflix will provide a cookie which will store 
  in the browser against the domain name netflix.com along with a randomly generated unique CSRF token to this particular
  user session. CSRF is inserted within the hidden parameters of HTML forms to avoid exposure to session cookies.
- Step 2: the same netflix user opens an evil.com website in another tab as previously
- Step 3: user clicked on the malicious link and make the request to netflix. And since login cookie already present in
  the same browser and the request to change mail is being made to the same domain, the backend server is expecting
  CSRF token along with the cookie. The token must be the same as initial value generated during login operation.


    The CRSF token will be used by the application server to verify the legitimacy of the end user request if it is
    coming from the same App UI or not. The application server rejects the request if the CSRF token fails to match the
    test.


**Step 1: create a csrfTokenRequestAttributeHandler**

    public CsrfTokenRequestAttributeHandler csrfTokenRequestAttributeHandler() {
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName("_csrf");
        return requestHandler;
    }


**Step 2: add the csrfTokenRequestAttributeHandler to the security filter chain**

    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
            .cors(corsCustomizer -> corsCustomizer.configurationSource(corsConfigurationSource()))
            .csrf((csrf) -> csrf
            .csrfTokenRequestHandler(csrfTokenRequestAttributeHandler())
            .ignoringRequestMatchers("/contact", "/register")
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
            ...

**Step 3: add a filter**

We try to read the token from the http servlet and we convert it as attribute. The CSRF token will be present in the 
response.

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if(null != csrfToken.getHeaderName()){
            response.setHeader(csrfToken.getHeaderName(), csrfToken.getToken());
        }
        filterChain.doFilter(request, response);
    }

We just finally need to pass the filter to defaultSecurityFilterChain: 

    ...
    .csrf((csrf) -> csrf
        .csrfTokenRequestHandler(csrfTokenRequestAttributeHandler())
        .ignoringRequestMatchers("/contact", "/register")
        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
    .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
    ...

**Step 4: login and authentication**

Previously when we were trying to access the backend through the UI. We tell spring security to create 
the JSESSIONID after initial login is completed and to send it to the front end application. The front
end application can leverage the same id for all request that will be sent to backend.
For this we added to defaultSecurityFilterChain: 

    ...
    .securityContext((context) -> context.requireExplicitSave(false))
    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
    ...

Note that we have in AuthenticationController the following method that handles userDetails AFTER login.

    @RequestMapping("/users")
    public CustomerResponse getUserDetailsAfterLogin(Authentication authentication) {
        List<CustomerDto> customerDto = userService.findByEmail(authentication.getName());
        List<CustomerResponse> returnValue = modelMapper.map(customerDto, new TypeToken<List<CustomerResponse>>() {}.getType());
        if (!returnValue.isEmpty()) {
            return returnValue.get(0);
        } else {
            return null;
        }
    }

The authentication is managed in defaultSecurityFilterChain: 

      .requestMatchers("/myAccount", "/myBalance", "/myLoans", "/myCards", "/auth/users").authenticated()

### Step 7: managing authorizations

Authentication : 
- in authentication the identity of users are checked for providing access to the system
- authentication is done before authorization 
- it need usually user's login details
- if authentication fails, we usually get 401 error
- for example: a bank customer or employee must prove identity before any action

Authorization: 
- in authorization, person's or user's authorities are checked for accessing the resources
- authorizations are always happening after authentication
- it need user's roles and privileges
- if authorization fails, we usually get 403 error
- once logged in the application, role and authorities will define which action can be done

Authorities and roles information is stored inside GrantedAuthority. SimpleGrantedAuthority is the default 
implementation of the GrantedAuthority interface and contains one method which is getAuthority()

**Configuring authorities**
In spring security, the authorities can be configured using the following ways: 
- **hasAuthority():** accept a single authority for which the endpoint will be configured and user will be validated 
  against the authority mentioned. Only users having the same authority configured can invoke the endpoint.
- **hasAnyAuthority:** accept many authorities for which the endpoint will be configured ans user will be validated 
  against the authorities mentioned. Only users having any of the authorities configured can invoke the endpoint.
- **access():** Using Spring Expression Language (SPEL), provides unlimited possibilities for configuring authorities 
  which are not possible with the above methods. We can use operator like OR, AND inside access() method.


#### Authority vs Role
Authority: 
- Authority is like an individual privilege or an action
- restricting access in a fine grained manner
- ex. : VIEWACCOUNT, VIEWCARDS...

Role: 
- Role is a groupe of privilege / actions
- Restricting access in a coarse grained manner
- ex. ROLE_ADMIN, ROLE_USER

The name of authorities are arbitrary in nature and these names can be customized as per business requirement. 
Roles are also represented using the same contract GrantedAuthority in Spring Security. 
When defining a role, its name should start by ROLE_ prefix. This prefix specifies the difference between a role and an 
authority. An example would be to configure access based on country of the user or current time / date...


**Configuring roles**
In spring security, the roles can be configured using the following ways:
- **hasRole():** accept a single role name for which the endpoint will be configured and user will be validated
  against the role mentioned. Only users having the same role configured can invoke the endpoint.
- **hasAnyRole:** accept many roles for which the endpoint will be configured ans user will be validated
  against the roles mentioned. Only users having any of the roles configured can invoke the endpoint.
- **access():** Using Spring Expression Language (SPEL), provides unlimited possibilities for configuring roles
  which are not possible with the above methods. We can use operator like OR, AND inside access() method.


### Step 8: custom security filters

**Servlets and filters**

Typical scenario in a web application: In java web applications, Servlet Container takes care of translating the HTTP messages for java code to understand.
One of the mostly used servlet container is Apache Tomcat. Servlet container converts HTTP messages into ServletRequest and
hand over to Servlet method as a parameter. Similarly, ServletResponse returns as an output to servlet container from Servlet.
So any thing writen inside java web apps are driven by servlets.

Roles of filters: Filters inside Java Web Application, can be used to intercept each request and response and do some pre work
before out business logic. So using the same filters, Spring Security enforce security based on configuration inside a web application

Filters are commonly used for security for : 
- input validation
- tracing auditing and reporting
- logging
- encryption / decryption 
- multi factor authentication using OTP...

Security filter chain is the following
- DisableEncodeUrlFilter
- WebAsyncManagerIntegrationFilter
- SecurityContextHolderFilter
- HeaderWriterFilter
- CorsFilter
- CrsfFilter
- LogoutFilter
- UsernamePasswordAuthenticationFilter
- DefaultLoginPageGeneratingFilter
- BasicAuthenticationFilter
- RequestCacheAwareFilter
- SecurityContextHolderAwareRequestFilter
- AnonymousAuthenticationFilter
- ExceptionTranslationFilter
- FilterSecurityInterceptor

In FilterChainProxy (in package org.springframework.security.web), we have the doFilter method that will iterate all filters : 

		@Override
		public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
			if (this.currentPosition == this.size) {
				this.originalChain.doFilter(request, response);
				return;
			}
			this.currentPosition++;
			Filter nextFilter = this.additionalFilters.get(this.currentPosition - 1);
			if (logger.isTraceEnabled()) {
				String name = nextFilter.getClass().getSimpleName();
				logger.trace(LogMessage.format("Invoking %s (%d/%d)", name, this.currentPosition, this.size));
			}
			nextFilter.doFilter(request, response, this);
		}

	}

In console:  
![img_1.png](img_1.png)

Once filter defined, we have to pass it to the defaultSecurityFilterChain method in Security 
configuration with : 
- addFilterBefore(filter, class): filter is added before the position of the specified filter class
- addFilterAfter(filter, class): filter is added after
- addFilterAt(filter, class): filter is added at the specific location of the specified filter class

**Adding filters:** 

    ...
    .csrf((csrf) -> csrf
       .csrfTokenRequestHandler(csrfTokenRequestAttributeHandler())
           .ignoringRequestMatchers("/auth/register")
           .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
       .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
       .addFilterBefore(new RequestValidationBeforeFilter(), BasicAuthenticationFilter.class)
       .addFilterAt(new AuthoritiesLoggingAtFilter(), BasicAuthenticationFilter.class)
       .addFilterAfter(new AuthoritiesLoggingAfterFilter(), BasicAuthenticationFilter.class)
    ....



### Step 9: Using JWT Token
- A token can be a plain strng of format universally unique identifier (UUID) or of type JSON Web Token (JWT) usually
generated when user authenticates for the first time during login.
- On every request to a restricted resource, the client sends the access token in the query string or authorization header.

### Step 10: method level security
- Once security rules are applied in security configuration class, method level security can be enabled using annotation 
  @EnableMethodSecurity on the configuration class. 
- Method level security will also helps authorization rules even in the non web applications where will not have any 
  endpoint

Method level security provides the below approaches to apply the authorization rules and executing business logic. 
- invocation authorizations: validates if someone can invoke a method or not base on roles / authorities
- filtering authorizations: validates what a method can receive through its parameters and what the invoker can receive
  back from the method post business logic execution

Spring security will use the aspects from the AOP module and have the interceptors in between the method invocation to 
apply the authorization rule configured. 
Method level security offers below 3 different styles for configuring the authorization rules on top of the methods:
- the `prePostEnabled` property enables Spring Security `@PreAuthorize` and `@PostAuthorize` annotations 
- the `securedEnabled` property enables `@Secured` annotation
- the `jsr250Enabled` property enables `@RoleAllowed` annotations

  
    @Configuration
    @EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
    public class AppSecurityConfig {
        ....
    }

Note : `@Secured` and `@RoleAllowed` are less powerful compared to `@PreAuthorize` and `@PostAuthoriza` annotations.

Using invocation authorization we can decide if a user is authorized to invoke a method before the 
method (preAuthorization) or after the method execution is completed (postAuthorization). Fof filtering before: 

    @Service
    public class LoanService {
        @PreAuthorize("hasAuthority('VIEWLOANS')")
        @PrAuthoriza("HasRole('ADMIN')")
        @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
        @PreAuthorize('#username === authentication.principal.username")
        public Loan getLoanDetails(String username) {
            ...
        }
    }

When implementing complex authorization logic, we can separate the login using a separate class that implements
`PermissionEvaluator` and overwrite the method hasPermission() inside it which can be leveraged inside the hasPermission 
configurations.

In specific scenarios, we want to make sure the parameters sent and received from and to the method need to follow 
authorization rules or filtering criteria, then we can consider filtering.
For filtering, the parameters before calling the method we can use PreFilter annotation. Not the filterObject should be
of type collection interface

    @RestController
    public class ContactController {
        @PreFilter("filterObject.contactName != 'Test'")
        public List<ContactResponse> saveContactInquiryDetails(@RequestBody List<ContactRequest> contact) {
        }

    }

We can also use PotFilter: 

    @RestController
    public class ContactController {
        @PostFilter("filterObject.contactName != 'Test'")
        public List<ContactResponse> saveContactInquiryDetails(@RequestBody List<ContactRequest> contact) {
        }

    }

### Step11: OAuth2

scenario: the twitter user want to use a third party website called TweetAnalyzer to get some insights.

- Without OAuth2: Twitter user has to share his twitter account credentials to the tweetAnalyzer website, using user 
  credentials the tweetAnalyzer will invoke the API of Twitter app to fetch the tweet details and posts that generates 
  a report for the end user. The disadvantage is that the tweetAnalyzer can go fraud and make other operations on your 
  behalf like changing password, mails, etc.
- With OAuth2: twitter user doesn't have to share his twitter account credentials to the tweetAnalyzer website. Instead, 
  he will let twitter app to give a temporary access token with limited access like read only tweets...

**Definitions**
- OAuth: stands for Open Authorization. It's a free and open protocol built on IETF standard and licence from the open 
  web foundation.
- OAuth2: security standard where you give one application to access your data in another application. The steps to 
  grant permission or consent are often referred to as authorization or even delegated authorization. You authorize one 
  application to access your data or use features in another application on your behalf, without giving password.

The OAuth framework specifies several grants types for different use cases, as well as a framework for creating new 
grant types:
- authorization code
- PKCE
- Client credentials
- Device code
- Refresh token
- Implicit flow (legacy)
- Password grand (legacy)

**Definitions**
- Resource owner: end user 
- client: website that interact with external application after taking permissions from the resource owner
- authorization server: server which knows about resource owner. In other word, user should have an account on this server
- resource server: this is the server where the APIs services the client want to consume are hosted.
- scopes: these are the granular permissions the client wants, such access to data or access to actions.

**OAuth2 flow**
- Step 1: user calls for getting access to resources
- Step 2: client is returning a response
- step 3: user sends credentials to the auth server
- step 4: auth server communicated with client and give authorization code
- step 5: client sends credentials and ask for a token
- step 6: auth server returns a token 
- step 7: client asks for resources and provides the authentication token
- step 8: the resource service send back data to the client

For test purpose: https://www.oauth.com/playground/ 

**OAuth2 vs OpenID connect**
What is openID Connect: 
- OpenID is a protocol that sits on top of the OAuth2.0 framework. While OAuth2.0 provides authorization via an access 
  token containing SCOPES, OpenID Connect provides authentication by introducing a new ID token which contains set of 
  information and claims specifically for identity. 
- With the ID token, OpenID Connect brings standards around sharing identity details among the applications. 

### Step 12: OAuth2 & KeyCloak
KayCloak is available for downloads here: https://www.keycloak.org/downloads
For setup documentation : https://www.keycloak.org/guides#getting-started
See also readme in keycloack source code

If we want we can also run as docker image:
https://www.keycloak.org/getting-started/getting-started-docker

    docker run -p 8180:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:22.0.5 start-dev

Here admin password and username are already configured.

![img_2.png](img_2.png)

Once connected, we can configure a "realm". Each realm contains specific configuration for specific environment.

![img_3.png](img_3.png)


Once a real created, we can manage users, groups, scopes, roles, clients...

![img_4.png](img_4.png)

**Create client credentials**
Select clients in the left side navigation and click on "create client"

![img_5.png](img_5.png)

First, configure general setting then click next

![img_6.png](img_6.png)

![img_7.png](img_7.png)


**Registration access token:** eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI5YWMzODRlYy1kMzI3LTRlMmEtYTllYy1kOTNlODk3NDAxMzQifQ.eyJleHAiOjAsImlhdCI6MTY5ODgzMzY5NywianRpIjoiNTI1NWE0YTEtZGI1OC00ZDkxLWFjN2ItOTkxNzlkYTllYzRkIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MTgwL3JlYWxtcy9iYW5rQXBwIiwiYXVkIjoiaHR0cDovL2xvY2FsaG9zdDo4MTgwL3JlYWxtcy9iYW5rQXBwIiwidHlwIjoiUmVnaXN0cmF0aW9uQWNjZXNzVG9rZW4iLCJyZWdpc3RyYXRpb25fYXV0aCI6ImF1dGhlbnRpY2F0ZWQifQ.AmvrEjYLMR6p8xSwJRYCZwcTFdponUxHDOEk7o_uGTg
**Client secret:** bDIr4VnxR57eXHNYizrifZbPIgNfWSoH

**Setup resource server**
The resource server contains accounts: backend must be updated. 

We have to add maven dependency: 

    <!-- https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-oauth2-resource-server -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
    </dependency>

We have to implement a converter class: !!!!!!!

    public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    
        @Override
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access");
    
            if (realmAccess == null || realmAccess.isEmpty()) {
                return new ArrayList<>();
            }
    
            return ((List<String>) realmAccess.get("roles"))
                    .stream().map(roleName -> "ROLE_" + roleName)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }
    }

Then we have to setup a JwtAuthenticationConverter

    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
        return converter;
    }



And add it to defaultSecurityFilterChain

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityContext((context) -> context.requireExplicitSave(false))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                .cors(corsCustomizer -> corsCustomizer.configurationSource(corsConfigurationSource()))
                .csrf((csrf) -> csrf
                        .csrfTokenRequestHandler(csrfTokenRequestAttributeHandler())
                        .ignoringRequestMatchers("/auth/register")
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/myAccount").hasRole("USER")
                        .requestMatchers("/myBalance").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/myLoans").authenticated()
                        .requestMatchers("/myCards").hasRole("USER")
                        .requestMatchers( "/contact", "/auth/users").authenticated()
                        .requestMatchers("/notices", "/auth/register").permitAll())
                .oauth2ResourceServer(oauth2ResourceServerCustomizer ->
                                oauth2ResourceServerCustomizer.jwt(jwtCustomizer ->
                                        jwtCustomizer.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .build();
    }

Finally, in application properties, we have to set jwt url based on created realm.

    spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:8180/realms/bankApp/protocol/openid-connect/certs

**Usinng client credentials grant type : Getting an access token**
We need to send a request to authorization server to retrieve an access token. 
What is the URL to call ? 
By consulting the following URL: http://localhost:8180/realms/bankApp/.well-known/openid-configuration

![img_8.png](img_8.png)

We have to use the token endpoint: http://localhost:8180/realms/bankApp/protocol/openid-connect/token
We use the jwks-uri in application properties to get access to public certificate: 
- "jwks_uri": "http://localhost:8180/realms/bankApp/protocol/openid-connect/certs",

We can send a post request to get the token : 

![img_9.png](img_9.png)

In jwt website: 

![img_10.png](img_10.png)

Note: as response we retrieve the roles and we note that by default roles used by rest API are not defined in 
keycloak. We can create roles manually in Keycloak administration. 

![img_11.png](img_11.png)

After this step : click on "clients", then the name of the client created. 
In the screenshot, "bankAppId": 

![img_12.png](img_12.png)

Then select service account roles and click on assign roles: 

![img_13.png](img_13.png)

**Using Authorization code grant type**
This grant type is not recommended for front end application. It can be used for API to API communication.
- click on client and in client list, select "create client"
- set client id and name
- then click on "next"
- 
![img_14.png](img_14.png)

At next step: 
- enable client authentication
- select standard flow (we can disable direct access grant)

![img_15.png](img_15.png)

Finally, we should configure redirect URL. It should be a valid URL to redirect on front end application. 

![img_16.png](img_16.png)

We also need to create a User: 

![img_17.png](img_17.png)

Once created, we have to create credentials for the user: 

![img_18.png](img_18.png)

Click on set password: 

![img_19.png](img_19.png)

We also have to pass roles: 

![img_22.png](img_22.png)

For authentication we have to use the following entry point: 

- "authorization_endpoint": "http://localhost:8180/realms/bankApp/protocol/openid-connect/auth",
- we can set postman with params url and copy to browser
- we're redirected to keycloak login page and will be redirected after successful authentication 
- params are: 
  - client_id: bankClient
  - response_tyoe: code
  - scope: openid
  - redirect_uri : same url as defined in keycloak
  - state: some random string


![img_20.png](img_20.png)

After login, note we receive a session state in URL and a code :
http://localhost:4200/noclientUrl?state=someRandomValue&session_state=48edf6ea-4dc6-416a-8fe9-6d23f217d9a8&code=3c6f91c4-ac2d-4dc5-be70-c4e631a6cacb.48edf6ea-4dc6-416a-8fe9-6d23f217d9a8.9f28bd0c-1816-4584-9b38-e1d9e2382645
![img_21.png](img_21.png)

With the code we are building url to get access token: 
If we check well knowns URLs, here:
http://localhost:8180/realms/bankApp/.well-known/openid-configuration

We can find token endpoint:
"token_endpoint": "http://localhost:8180/realms/bankApp/protocol/openid-connect/token",

Using code we can then call entry point for getting bearer token :
code: 3c6f91c4-ac2d-4dc5-be70-c4e631a6cacb.48edf6ea-4dc6-416a-8fe9-6d23f217d9a8.9f28bd0c-1816-4584-9b38-e1d9e2382645

We call: 
- http://localhost:8180/realms/bankApp/protocol/openid-connect/token
- with params: 
  - client_id: bankClient
  - client_secret
  - grant_type: authorization_code
  - code: code retrieve 
  - redirect_uri : same url as defined in keycloak 
  - scope: openid

With retrieved token, we can call any authenticated entrypoint:
- http://localhost:8080/myBalance?email=me@me.com

**Using Authorization code grant type with PKCE**
PKCE enhances authorization code grant: PKCE means Proof Key for Code Exchange

- When public clients request access token, some additional security concerns are posed that are not mitigated by the 
  authorization code flow alone. This is because public client cannot securely store a client secret
- Given the situation, OAuth2 provides a version of the authorization Code Flow for public client applications which 
  uses a Proof Key for Code Exchange

The PKCE follow the following steps: 
1. once user clicks for login, client app creates a cryprographically random code_verifier and from this generates a 
   code_challenge
2. code challenge is a base 64 URL encoded string of the SHA256 hash of the code verifier
3. redirects the user to the authorization server along with the code challenge
4. Authorization server stores the code_challenge and redirects the user back to the application with an authorization 
   code which can be used one time
5. client application send the authorization code and the code verifier (step1) to the authorization server
6. Authorization server checks the code_challenge and the code_verifier. if they are valid, the it responds with ID 
   token and access token  

At step 2 & 3, following details are required: 
- client_id
- redirect_uri
- scope
- state: CSRF token value
- response_type: code
- code_challenge: code challenge generated
- code_challenge_method: S256

At step 5: 
- code
- client_id & client_secret(optional)
- grant_type
- redirect_uri
- code_verifier

In keyCloack: 
- Create a new client: 
![img_23.png](img_23.png)
- Select standard flow
![img_24.png](img_24.png)
- Set the redirect url and logout: 
![img_26.png](img_26.png)
- Click on save

In advanced settings tab: 
- set Proof Key for Code Exchange Code Challenge Method 
![img_27.png](img_27.png)
- Select 256


## Important Links
- Spring website to generate projects - https://start.spring.io/
- Spring Website - https://spring.io/
- Spring Projects website - https://spring.io/projects
- Spring Boot properties - https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html
- AWS website - https://aws.amazon.com/
- SQLECTRON website - https://sqlectron.github.io
- Free MySQL DB website - https://www.freemysqlhosting.net
- OAuth2 Website - https://oauth.net/2/
- OAuth2 playground - https://www.oauth.com/playground/
- KeyCloak website - https://www.keycloak.org
- KeyCloak Download page - https://www.keycloak.org/downloads
- KeyCloak setup - https://www.keycloak.org/getting-started/getting-started-zip
- KeyCloak guides - https://www.keycloak.org/guides
- KeyCloak Well known APIs - http://localhost:8180/realms/eazybankdev/.well-known/openid-configuration
- Angular Keycloak library - https://www.npmjs.com/package/keycloak-angular
- Keycloak official documentation - https://www.keycloak.org/documentation
- Keycloak Admin REST APIs - https://www.keycloak.org/docs-api/19.0.2/rest-api/index.html
