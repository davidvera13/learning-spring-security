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
