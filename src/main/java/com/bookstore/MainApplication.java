package com.bookstore;

import com.bookstore.service.BookstoreService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.TransactionDefinition;

@SpringBootApplication
public class MainApplication {

    private final BookstoreService bookstoreService;

    public MainApplication(BookstoreService bookstoreService) {
        this.bookstoreService = bookstoreService;
    }

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    @Bean
    public ApplicationRunner init() {
        return args -> {
            System.out.println("\n\nPESSIMISTIC_WRITE and UPDATE ...");
            System.out.println("-----------------------------------------------------------------");
            bookstoreService.pessimisticWriteUpdate();

            System.out.println("\n\nPESSIMISTIC_WRITE and INSERT ...");
            System.out.println("\n-------------------------REPEATABLE_READ---------------------");
            bookstoreService.pessimisticWriteInsert(TransactionDefinition.ISOLATION_REPEATABLE_READ);

            System.out.println("\n-------------------------READ_COMMITTED----------------------");
            bookstoreService.pessimisticWriteInsert(TransactionDefinition.ISOLATION_READ_COMMITTED);

            System.out.println("\n\nPESSIMISTIC_WRITE and DELETE ...");
            System.out.println("-----------------------------------------------------------------");
            bookstoreService.pessimisticWriteDelete();
        };
    }
}

/*
 * 
 * 
 * 
 * How PESSIMISTIC_WRITE Works With UPDATE/INSERT And DELETE Operations

Description: This application is an example of triggering UPDATE, INSERT and DELETE operations in the context of PESSIMISTIC_WRITE locking against MySQL. While UPDATE and DELETE are blocked until the exclusive lock is released, INSERT depends on the transaction isolation level. Typically, even with exclusive locks, inserts are possible (e.g., in PostgreSQL). In MySQL, for the default isolation level, REPEATABLE READ, inserts are prevented against a range of locked entries, but, if we switch to READ_COMMITTED, then MySQL acts as PostgreSQL as well.

Key points:

start Transaction A and trigger a SELECT with PESSIMISTIC_WRITE to acquire an exclusive lock
start a concurrent Transaction B that triggers an UPDATE, INSERT or DELETE on the rows locked by Transaction A
in case of UPDATE, DELETE and INSERT + REPEATABLE_READ, Transaction B is blocked until it timeouts or Transaction A releases the exclusive lock
in case of INSERT + READ_COMMITTED, Transaction B can insert in the range of rows locked by Transaction A even if Transaction A is holding an exclusive lock on this range

 * 
 */
