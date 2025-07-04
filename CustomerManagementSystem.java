Designing POC Core Banking System
---------------------------------

1. Tech Stack
- Spring Boot for backend development.
- Hibernate (JPA) for database interaction.
- MySQL / PostgreSQL as the relational database.
- Spring Security for authentication and authorization.

2.A. Customer Management
Handles registration, authentication, and profile updates

@Entity
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<Account> accounts;
}

B. Account Management
Maintains account balances and transactions.

@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accountNumber;
    private Double balance;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
}

@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;
    private String type; // DEPOSIT, WITHDRAWAL, TRANSFER

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
}

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    @Transactional
    public Customer getCustomerWithAccounts(Long id) {
        return customerRepository.findById(id).orElseThrow();
    }
}

Using a transactional method ensures objects are loaded within the same session.

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    @Transactional
    public Customer getCustomerWithAccounts(Long id) {
        return customerRepository.findById(id).orElseThrow();
    }
}

You'll need to introduce a Loan entity that links to Customer and Account.

@Entity
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String loanNumber;
    private Double principalAmount;
    private Double interestRate;
    private Integer tenureInMonths;
    private String status; // PENDING, APPROVED, REJECTED, CLOSED

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "loan", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RepaymentSchedule> repaymentSchedules;
}


@Entity
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String loanNumber;
    private Double principalAmount;
    private Double interestRate;
    private Integer tenureInMonths;
    private String status; // PENDING, APPROVED, REJECTED, CLOSED

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "loan", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RepaymentSchedule> repaymentSchedules;
}


2. Loan Processing
- Loan applications should go through an approval workflow.
- Interest should be calculated periodically.
- Repayment should reflect against the customer's account

@Service
public class LoanService {
    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Transactional
    public Loan applyForLoan(Long customerId, Double principalAmount, Double interestRate, Integer tenure) {
        Customer customer = customerRepository.findById(customerId).orElseThrow();
        Loan loan = new Loan();
        loan.setCustomer(customer);
        loan.setPrincipalAmount(principalAmount);
        loan.setInterestRate(interestRate);
        loan.setTenureInMonths(tenure);
        loan.setStatus("PENDING");
        return loanRepository.save(loan);
    }

    @Transactional
    public void approveLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId).orElseThrow();
        loan.setStatus("APPROVED");
        loanRepository.save(loan);
    }
}


3. Repayment Schedule
Each loan should have a structured repayment schedule.


@Entity
public class RepaymentSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dueDate;
    private Double installmentAmount;
    private Boolean paid;

    @ManyToOne
    @JoinColumn(name = "loan_id")
    private Loan loan;
}

4. Loan Repayment Mechanism
Once a customer repays an installment, it should be deducted from their Account balance.

@Service
public class RepaymentService {
    @Autowired
    private RepaymentScheduleRepository repaymentScheduleRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Transactional
    public void processRepayment(Long repaymentId, Long accountId) {
        RepaymentSchedule repayment = repaymentScheduleRepository.findById(repaymentId).orElseThrow();
        Account account = accountRepository.findById(accountId).orElseThrow();

        if (account.getBalance() >= repayment.getInstallmentAmount()) {
            account.setBalance(account.getBalance() - repayment.getInstallmentAmount());
            repayment.setPaid(true);
            repaymentScheduleRepository.save(repayment);
            accountRepository.save(account);
        } else {
            throw new RuntimeException("Insufficient Balance");
        }
    }
}

Each branch should have its own identifier and accounts

@Entity
public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String branchName;
    private String branchCode;
    
    @OneToMany(mappedBy = "branch", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Account> accounts;
}

Each account should belong to a specific branch

@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accountNumber;
    private Double balance;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;
}

3. Implement an Inter-Branch Transaction Mechanism
The Transaction entity should support inter-branch transfers.

@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;
    private String type; // DEPOSIT, WITHDRAWAL, TRANSFER

    @ManyToOne
    @JoinColumn(name = "source_account_id")
    private Account sourceAccount;

    @ManyToOne
    @JoinColumn(name = "destination_account_id")
    private Account destinationAccount;
}

4. Transfer Funds Between Branches
The TransactionService should handle inter-branch transfers.

@Service
public class TransactionService {
    @Autowired
    private AccountRepository accountRepository;

    @Transactional
    public void transferFunds(Long sourceAccountId, Long destinationAccountId, Double amount) {
        Account source = accountRepository.findById(sourceAccountId).orElseThrow();
        Account destination = accountRepository.findById(destinationAccountId).orElseThrow();

        if (source.getBalance() >= amount) {
            source.setBalance(source.getBalance() - amount);
            destination.setBalance(destination.getBalance() + amount);

            Transaction transaction = new Transaction();
            transaction.setAmount(amount);
            transaction.setType("TRANSFER");
            transaction.setSourceAccount(source);
            transaction.setDestinationAccount(destination);

            accountRepository.save(source);
            accountRepository.save(destination);
        } else {
            throw new RuntimeException("Insufficient Funds");
        }
    }
}

/=====================================================================/

@Entity
@Table(name = "accounts")
public class Account extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String accountNumber;

    @Column(nullable = false)
    private Double balance = 0.0;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Transaction> transactions = new ArrayList<>();

    // Add audit fields if not using BaseEntity
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}


/====================Customer========================/

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "customers")
public class Customer extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    

/=================Service Logic================================/
@Service
public class AccountService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(AccountService.class);


    @Transactional
    public Account openAccount(Account account) {
	
	  logger.info("Opening account for customerId: {}", request.getCustomerId());

	
       Customer customer = customerRepository.findById(request.getCustomerId())
        .orElseThrow(() -> {
            logger.warn("Customer not found: {}", request.getCustomerId());
            return new RuntimeException("Customer not found");
        });


        Account account = new Account();
        account.setAccountNumber(UUID.randomUUID().toString());
        account.setAccountType(request.getAccountType());
        account.setCurrency(request.getCurrency());
        account.setBalance(request.getInitialDeposit());
        account.setOpenedDate(LocalDate.now());
        account.setActive(true);
        account.setCustomer(customer);

        return accountRepository.save(account);
		logger.info("Account created: {} for customer {}", saved.getAccountNumber(), customer.getFullName());

    }
}


@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/open")
    public ResponseEntity<Account> openAccount(@RequestBody OpenAccountRequest request) {
        Account newAccount = accountService.openAccount(request);
        return new ResponseEntity<>(newAccount, HttpStatus.CREATED);
    }
}

/================Updated Service Layer==================/

@Service
public class AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Transactional
    public Account openAccount(Account account) {
        Long customerId = account.getCustomer().getCustomerId();
        logger.info("Opening account for customerId: {}", customerId);

        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> {
                logger.warn("Customer not found: {}", customerId);
                return new RuntimeException("Customer not found");
            });

        account.setCustomer(customer);
        account.setAccountNumber(UUID.randomUUID().toString());
        account.setOpenedDate(LocalDate.now());
        account.setActive(true);

        Account saved = accountRepository.save(account);
        logger.info("Account created: {} for customer {}", saved.getAccountNumber(), customer.getFullName());

        return saved;
    }
}

JSON
====
{
  "accountType": "SAVINGS",
  "currency": "INR",
  "balance": 10000,
  "customer": {
    "customerId": 1
  }
}

/=================================================================================================================/

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<List<Accounts>> getAllAccounts() {
        logger.debug("Received request to fetch all accounts");
        List<Accounts> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<Accounts> getAccountById(@PathVariable Long accountId) {
        logger.debug("Received request to fetch account with ID: {}", accountId);
        Accounts account = accountService.getAccountById(accountId);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Accounts>> getAccountsByCustomerId(@PathVariable Long customerId) {
        logger.debug("Received request to fetch accounts for customerId: {}", customerId);
        List<Accounts> accounts = accountService.getAccountsByCustomerId(customerId);
        return ResponseEntity.ok(accounts);
    }
}

public interface AccountRepository extends JpaRepository<Accounts, Long> {
    List<Accounts> findByCustomerCustomerId(Long customerId);
}


@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String email);
    List<Customer> findByKycStatus(String kycStatus);
}


@Service
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    private CustomerRepository customerRepository;

    public List<Customer> getAllCustomers() {
        logger.info("Fetching all customers");
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Long customerId) {
        logger.info("Fetching customer with ID: {}", customerId);
        return customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    logger.warn("Customer not found: {}", customerId);
                    return new RuntimeException("Customer not found");
                });
    }

    public Customer createCustomer(Customer customer) {
        logger.info("Creating new customer: {}", customer.getFullName());
        return customerRepository.save(customer);
    }

    public Customer updateCustomer(Long customerId, Customer updatedData) {
        logger.info("Updating customer with ID: {}", customerId);
        Customer existing = getCustomerById(customerId);

        existing.setFullName(updatedData.getFullName());
        existing.setEmail(updatedData.getEmail());
        existing.setPhoneNumber(updatedData.getPhoneNumber());
        existing.setAddress(updatedData.getAddress());
        existing.setDateOfBirth(updatedData.getDateOfBirth());
        existing.setKycStatus(updatedData.getKycStatus());

        return customerRepository.save(existing);
    }

    public void deleteCustomer(Long customerId) {
        logger.info("Deleting customer with ID: {}", customerId);
        customerRepository.deleteById(customerId);
    }
}

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        logger.debug("Received request to fetch all customers");
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long customerId) {
        logger.debug("Received request to fetch customer with ID: {}", customerId);
        return ResponseEntity.ok(customerService.getCustomerById(customerId));
    }

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        logger.debug("Received request to create customer: {}", customer.getFullName());
        Customer created = customerService.createCustomer(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long customerId, @RequestBody Customer customer) {
        logger.debug("Received request to update customer with ID: {}", customerId);
        return ResponseEntity.ok(customerService.updateCustomer(customerId, customer));
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long customerId) {
        logger.debug("Received request to delete customer with ID: {}", customerId);
        customerService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }
}


/================================================================================/

  public List<Accounts> getAccountsByCustomerId(Long customerId) {
        logger.info("Fetching accounts for customerId: {}", customerId);
        List<Accounts> accounts = accountRepository.findByCustomerCustomerId(customerId);

        if (accounts.isEmpty()) {
            logger.warn("No accounts found for customerId: {}", customerId);
        } else {
            logger.info("Found {} account(s) for customerId: {}", accounts.size(), customerId);
        }

        return accounts;
    }
}


@GetMapping("/customer/{customerId}")
public ResponseEntity<List<Accounts>> getAccountsByCustomerId(@PathVariable Long customerId) {
    List<Accounts> accounts = accountService.getAccountsByCustomerId(customerId);
    return ResponseEntity.ok(accounts);
}
