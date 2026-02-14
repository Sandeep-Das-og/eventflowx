package com.eventflowx.wallet.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "wallets")
public class Wallet {

    @Id
    private String userId;

    private double balance;

    @Version
    private Long version;

    public void debit(double amount) {
        if (balance < amount) {
            throw new IllegalStateException("Insufficient funds");
        }
        balance -= amount;
    }

    public void credit(double amount) {
        balance += amount;
    }

    // getters & setters
}
