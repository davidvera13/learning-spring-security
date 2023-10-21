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

### Step 1: Configuring the security filter chain
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


### Step 1: Customizing the security filter chain with secured and unsecured routes
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

