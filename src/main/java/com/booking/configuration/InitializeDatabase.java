//package com.seesaw.configuration;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.seesaw.model.*;
//import com.seesaw.repository.CategoryRepository;
//import com.seesaw.repository.CollectionRepository;
//import com.seesaw.repository.TokenRepository;
//import com.seesaw.service.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import com.seesaw.repository.UserRepository;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.Date;
//import com.fasterxml.jackson.core.type.TypeReference;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.List;
//
//
//@Configuration
//public class InitializeDatabase {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private JwtService jwtService;
//
//    @Autowired
//    private TokenRepository tokenRepository;
//
//    @Autowired
//    private CollectionRepository collectionRepository;
//
//    @Autowired
//    private CategoryRepository categoryRepository;
//
//    @Autowired
//    private CategoryService categoryService;
//
//    @Autowired
//    private CollectionService collectionService;
//
//    @Autowired
//    private InvoiceService invoiceService;
//
//    @Autowired
//    private ProductService productService;
//
//    @Autowired
//    private OrderService orderService;
//
//    @Autowired
//    private FeedbackService feedbackService;
//
//    @Autowired
//    private CartService cartService;
//
//    @Autowired
//    private CartDetailService cartDetailService;
//
//    private CategoryModel categoryModel;
//    @Bean
//    public CommandLineRunner loadDatabase() {
//        return args -> {
//            addUser("admin", "admin", "admin@gmail.com", "123456", "male", "0123456789", Role.ADMIN);
//            addUser("trong", "dat", "trongdat@gmail.com", "123456", "male", "0123456789", Role.USER);
//            addUser("quoc", "bao", "quocbao@gmail.com", "123456", "male", "0123456789", Role.USER);
//            addUser("Lam", "Nhu", "lamnhu@gmail.com", "123456", "female", "0123456789", Role.USER);
//            addUser("Minh", "Thu", "minhthu@gmail.com", "123456", "female", "0123456789", Role.USER);
////            loadCategory();
////            loadCollection();
////            loadInvoices();
////            loadProducts();
////            loadFeedbacks();
////            loadCarts();
////            loadCartDetail();
////            loadOrder();
//        };
//    }
//
//    private void loadCategory() {
//        ObjectMapper mapper = new ObjectMapper();
//        TypeReference<List<CategoryModel>> categoryReference = new TypeReference<List<CategoryModel>>() {};
//        InputStream categoryStream = TypeReference.class.getResourceAsStream("/json/categories.json");
//        try {
//            List<CategoryModel> categories = mapper.readValue(categoryStream, categoryReference);
//            categoryService.save(categories);
//            System.out.println("Categories Saved!");
//        } catch (IOException e) {
//            System.out.println("Unable to save categories: " + e.getMessage());
//        }
//    }
//
//    private void loadCollection() {
//        ObjectMapper mapper = new ObjectMapper();
//        TypeReference<List<CollectionModel>> typeReference = new TypeReference<List<CollectionModel>>() {};
//        InputStream inputStream = TypeReference.class.getResourceAsStream("/json/collections.json");
//        try {
//            List<CollectionModel> collections = mapper.readValue(inputStream, typeReference);
//            collectionService.save(collections);
//            System.out.println("Collections Saved!");
//
//        } catch (IOException e) {
//            System.out.println("Unable to save collections: " + e.getMessage());
//        }
//    }
//
//    private void loadInvoices() {
//        ObjectMapper mapper = new ObjectMapper();
//        TypeReference<List<InvoiceModel>> typeReference = new TypeReference<List<InvoiceModel>>() {};
//        InputStream inputStream = TypeReference.class.getResourceAsStream("/json/invoices.json");
//        try {
//            List<InvoiceModel> invoices = mapper.readValue(inputStream, typeReference);
//            invoiceService.save(invoices);
//            System.out.println("Invoices Saved!");
//
//        } catch (IOException e) {
//            System.out.println("Unable to save invoices: " + e.getMessage());
//        }
//    }
//
//    private void loadProducts() {
//        ObjectMapper mapper = new ObjectMapper();
////        TypeReference<List<ProductModel>> typeReference = new TypeReference<List<ProductModel>>() {};
////        InputStream inputStream = TypeReference.class.getResourceAsStream("/json/products.json");
////        var collection = collectionRepository.findById("1b362eed-5446-413e-a013-8acfc2769830");
////        var category = categoryRepository.findByName("Glasses");
//        var collection = collectionService.getCollectionById("52d36858-00e7-49d8-a8bb-5cf8a448725d");
//        System.out.println(collection);
////        System.out.println(category);
////        try {
//////            List<ProductModel> products = mapper.readValue(inputStream, typeReference);
//////            products.forEach(product -> {
//////                ProductRequest productRequest = new ProductRequest();
//////                productRequest.setName(product.getName());
//////                productRequest.setPrice(product.getPrice());
//////                productRequest.setQuantity(product.getQuantity());
//////                productRequest.setDescription(product.getDescription());
//////                productRequest.setImage_path(product.getImage_path());
////////                productRequest.setCollection_id(collection.getId());
////////                productRequest.setCategory_id(category.getId());
//////                productService.addProduct(productRequest);
//////            });
////            System.out.println("Products Saved!");
////
////        } catch (IOException e) {
////            System.out.println("Unable to save products: " + e.getMessage());
////        }
//    }
//
//    private void loadOrder() {
//        ObjectMapper mapper = new ObjectMapper();
//        TypeReference<List<OrderModel>> typeReference = new TypeReference<List<OrderModel>>() {};
//        InputStream inputStream = TypeReference.class.getResourceAsStream("/json/orders.json");
//        try {
//            List<OrderModel> orders = mapper.readValue(inputStream, typeReference);
//            orderService.save(orders);
//            System.out.println("Orders Saved!");
//
//        } catch (IOException e) {
//            System.out.println("Unable to save orders: " + e.getMessage());
//        }
//    }
//
//    private void loadFeedbacks() {
//        ObjectMapper mapper = new ObjectMapper();
//        TypeReference<List<FeedbackModel>> typeReference = new TypeReference<List<FeedbackModel>>() {};
//        InputStream inputStream = TypeReference.class.getResourceAsStream("/json/feedbacks.json");
//        try {
//            List<FeedbackModel> feedbacks = mapper.readValue(inputStream, typeReference);
//            feedbackService.save(feedbacks);
//            System.out.println("Feedbacks Saved!");
//
//        } catch (IOException e) {
//            System.out.println("Unable to save feedbacks: " + e.getMessage());
//        }
//    }
//
////    private void loadCarts() {
////        ObjectMapper mapper = new ObjectMapper();
////        TypeReference<List<CartModel>> typeReference = new TypeReference<List<CartModel>>() {};
////        InputStream inputStream = TypeReference.class.getResourceAsStream("/json/carts.json");
////        try {
////            List<CartModel> carts = mapper.readValue(inputStream, typeReference);
////            cartService.save(carts);
////            System.out.println("Carts Saved!");
////
////        } catch (IOException e) {
////            System.out.println("Unable to save carts: " + e.getMessage());
////        }
////    }
//
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
//
//    private void addUser(String firstName, String lastName, String email, String password, String gender, String contact, Role role) {
//        var user = UserModel.builder()
//                .firstname(firstName)
//                .lastname(lastName)
//                .email(email)
//                .password(passwordEncoder.encode(password))
//                .gender(gender)
//                .contact(contact)
//                .address("tdtu")
//                .avatar("/images/avtDefault.jpg")
//                .date_created(Date.from(java.time.Instant.now()))
//                .date_updated(null)
//                .role(role)
//                .provider(Provider.LOCAL)
//                .build();
//        if (userRepository.existsUserModelByEmail(email)) {
//            userRepository.delete(user);
//            System.out.println("User deleted!");
//        } else {
//            var savedUser = userRepository.save(user);
//            var jwtToken = jwtService.generateToken(user);
//            var refreshToken = jwtService.generateRefreshToken(user);
//            saveUserToken(savedUser, jwtToken);
//            cartService.addCart(CartModel.builder()
//                    .user(savedUser)
//                    .total_amount(0.0D)
//                    .build());
//            System.out.println("User saved!");
//            userRepository.save(user);
//        }
//    }
//
////    private void checkUser(String email) {
////        if (userRepository.existsUserModelByEmail(email)) {
////            userRepository.delete(userRepository.findByEmail(email).orElseThrow());
////        }
////    }
//
//    private void saveUserToken(UserModel user, String jwtToken) {
//        var token = TokenModel.builder()
//                .users(user)
//                .token(jwtToken)
//                .tokenType(TokenType.BEARER)
//                .expired(false)
//                .revoked(false)
//                .build();
//        tokenRepository.save(token);
//    }
//}
