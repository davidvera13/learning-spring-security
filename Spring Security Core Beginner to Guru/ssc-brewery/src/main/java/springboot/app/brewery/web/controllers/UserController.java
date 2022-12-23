package springboot.app.brewery.web.controllers;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import springboot.app.brewery.domain.security.UserEntity;
import springboot.app.brewery.repositories.security.UserRepository;

@Slf4j
@RequestMapping("/user")
@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final GoogleAuthenticator googleAuthenticator;


    @GetMapping("/register2fa")
    public String register2fa(Model model){
        // model.addAttribute("googleurl", "todo");
        UserEntity user = getUser();
        String url = GoogleAuthenticatorQRGenerator
                .getOtpAuthURL(
                        "SpringAppBuilder",
                        user.getUsername(),
                        googleAuthenticator.createCredentials(user.getUsername()));

        log.debug("Google QR URL: " + url);

        model.addAttribute("googleurl", url);

        return "user/register2fa";
    }

    @PostMapping("/register2fa")
    public String confirm2Fa(@RequestParam Integer verifyCode){
        UserEntity user = getUser();

        log.debug("Entered Code is:" + verifyCode);

        if (googleAuthenticator.authorizeUser(user.getUsername(), verifyCode)) {
            UserEntity savedUser = userRepository.findById(user.getId()).orElseThrow();
            savedUser.setUseGoogle2fa(true);
            userRepository.save(savedUser);
            return "index";
        } else {
            return "user/register2fa";
        }
    }

    @GetMapping("/verify2fa")
    public String verify2fa(){
        return "user/verify2fa";
    }

    @PostMapping("/verify2fa")
    public String verifyPostOf2Fa(@RequestParam Integer verifyCode){

        UserEntity user = getUser();

        if (googleAuthenticator.authorizeUser(user.getUsername(), verifyCode)) {
            ((UserEntity)SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal())
                    .setGoogle2faRequired(false);

            return "/index";
        } else {
            return "user/verify2fa";
        }
    }


    // helper method
    private UserEntity getUser() {
        return (UserEntity) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

}
