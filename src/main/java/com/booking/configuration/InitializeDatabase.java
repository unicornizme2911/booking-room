package com.booking.configuration;

import com.booking.dto.request.*;
import com.booking.repository.CategoryRepository;
import com.booking.repository.TokenRepository;
import com.booking.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.booking.models.*;
import com.booking.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Type;
import java.util.Date;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;


@Configuration
public class InitializeDatabase {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TransportService transportService;

    @Autowired
    private MealService mealService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private FeatureService featureService;

    @Bean
    public CommandLineRunner loadDatabase() {
        return args -> {
            addUser("admin", "admin", "admin@gmail.com", "123456", Role.ADMIN);
            addUser("Trong", "Dat", "trongdat@gmail.com", "123456", Role.USER);
            addUser("Quoc", "Bao", "quocbao@gmail.com", "123456", Role.USER);
            addUser("Lam", "Nhu", "lamnhu@gmail.com", "123456", Role.USER);
            addUser("Minh", "Thu", "minhthu@gmail.com", "123456", Role.USER);
//            loadData(
//                    "/json/features.json",
//                    new TypeReference<List<FeatureRequest>>(){},
//                    featureService::add
//            );
//            loadData(
//                    "/json/categories.json",
//                    new TypeReference<List<CategoryRequest>>(){},
//                    categoryService::add
//            );
//            loadData(
//                    "/json/transports.json",
//                    new TypeReference<List<TransportRequest>>(){},
//                    transportService::add
//            );
//            loadData(
//                    "/json/meals.json",
//                    new TypeReference<List<MealRequest>>(){},
//                    mealService::add
//            );
//            loadData(
//                    "/json/rooms.json",
//                    new TypeReference<List<RoomRequest>>(){},
//                    roomService::add
//            );

        };
    }
    
    private <T> void loadData(String jsonPath, TypeReference<List<T>> typeReference, Consumer<T> saveFunction) {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream stream = getClass().getResourceAsStream(jsonPath)) {
            List<T> dataList = mapper.readValue(stream, typeReference);
            for (T data : dataList) {
                saveFunction.accept(data);
            }
            System.out.println("Data Saved!");
        } catch (IOException e){
            System.out.println("Unable to load json file: " + e.getMessage());
        }
    }

//    private void loadCartDetail() {
//        ObjectMapper mapper = new ObjectMapper();
//        TypeReference<List<CartDetailModel>> typeReference = new TypeReference<List<CartDetailModel>>() {};
//        InputStream inputStream = TypeReference.class.getResourceAsStream("/json/cart_details.json");
//        try {
//            List<CartDetailModel> cartDetails = mapper.readValue(inputStream, typeReference);
//            cartDetailService.save(cartDetails);
//            System.out.println("CartDetails Saved!");
//
//        } catch (IOException e) {
//            System.out.println("Unable to save cartDetails: " + e.getMessage());
//        }
//    }

    private void addUser(String firstName, String lastName, String email, String password, Role role) {
        var user = UserModel.builder()
                .firstname(firstName)
                .lastname(lastName)
                .email(email)
                .password(passwordEncoder.encode(password))
                .avatar("/images/avtDefault.jpg")
                .date_created(Date.from(java.time.Instant.now()))
                .date_updated(null)
                .role(role)
                .provider(Provider.LOCAL)
                .build();
        if (userRepository.existsUserModelByEmail(email)) {
            return;
        } else {
            var savedUser = userRepository.save(user);
            var refreshToken = jwtService.generateRefreshToken(user);
            saveUserToken(savedUser, refreshToken);
            userRepository.save(user);
            System.out.println("User saved!");
        }
    }

    private void saveUserToken(UserModel user, String jwtToken) {
        var token = TokenModel.builder()
                .users(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }
}
