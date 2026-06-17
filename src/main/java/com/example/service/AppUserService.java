package com.example.service;

import com.example.data.entity.AppUser;
import com.example.data.entity.Customer;
import com.example.data.repository.CustomerRepository;
import com.example.data.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class AppUserService {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int PASSWORD_RESET_CODE_EXPIRATION_MINUTES = 15;

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public AppUserService(UserRepository userRepository,
                          CustomerRepository customerRepository,
                          PasswordEncoder passwordEncoder,
                          EmailService emailService) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    public void registerCustomerUser(String username,
                                     String email,
                                     String password,
                                     String customerName,
                                     String customerSecondName,
                                     String phone
                                     ) {
        if (userRepository.findByUsername(username) != null) {
            throw new IllegalArgumentException("Username already exists.");
        }
        if (userRepository.findByEmail(email) != null) {
            throw new IllegalArgumentException("Email already exists.");
        }

        Customer customer = new Customer(customerName, customerSecondName, 0, 0, phone);
        Customer savedCustomer = customerRepository.save(customer);

        AppUser user = new AppUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles("USER");
        user.setBalance(BigDecimal.valueOf(0d));
        user.setCustomer(savedCustomer);

        userRepository.save(user);
        emailService.sendAccountCreatedEmail(email);
    }

    public AppUser findUserByUsernameOrEmail(String credentials){
        AppUser user = userRepository.findAppUserByUsernameOrEmail(credentials, credentials);

        if(user == null){
            throw new UsernameNotFoundException("Invalid username or password.");
        }

        return user;
    }

    @Transactional
    public void createPasswordResetCode(String email) {
        AppUser user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("No account found for this email.");
        }

        String code = generateResetCode();
        user.setPasswordResetCode(code);
        user.setPasswordResetCodeExpiresAt(LocalDateTime.now().plusMinutes(PASSWORD_RESET_CODE_EXPIRATION_MINUTES));
        userRepository.save(user);

        emailService.sendPasswordResetCode(user.getEmail(), code);
    }

    @Transactional
    public void resetPassword(String email, String code, String newPassword) {
        AppUser user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("No account found for this email.");
        }
        if (user.getPasswordResetCode() == null
                || user.getPasswordResetCodeExpiresAt() == null
                || user.getPasswordResetCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("The reset code expired. Request a new code.");
        }
        if (!user.getPasswordResetCode().equals(code)) {
            throw new IllegalArgumentException("Invalid reset code.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetCode(null);
        user.setPasswordResetCodeExpiresAt(null);
        userRepository.save(user);
    }

    private String generateResetCode() {
        return String.valueOf(100000 + SECURE_RANDOM.nextInt(900000));
    }

    public Double getBalance(){
        String username = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        AppUser user = userRepository.findByUsername(username);
        return user.getBalance().doubleValue();
    }

    public void setBalance(Double balance){
        String username = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        AppUser user = userRepository.findByUsername(username);
        user.setBalance(BigDecimal.valueOf(balance));

        userRepository.save(user);
    }

    public void subtractBalance(Double amount){
        Double balance = getBalance();
        balance = balance - amount;

        if(balance < 0){
            return;
        }

        setBalance(balance);
    }

    @Transactional
    public BigDecimal deposit(Double amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }

        String username = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        AppUser user = userRepository.findByUsername(username);

        BigDecimal currentBalance = user.getBalance() == null ? BigDecimal.ZERO : user.getBalance();
        BigDecimal depositAmount = BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP);
        BigDecimal updatedBalance = currentBalance.add(depositAmount);
        user.setBalance(updatedBalance);

        userRepository.save(user);
        return updatedBalance;
    }

    public Customer getCustomer(){
        String username = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        AppUser user = userRepository.findByUsername(username);
        return user.getCustomer();

    }

    public String getUsernameForCustomer(Customer customer) {
        AppUser user = userRepository.findByCustomer(customer);
        return user == null ? null : user.getUsername();
    }

    public AppUser getLoggedInUser(){
        String username = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        return userRepository.findByUsername(username);
    }
}
