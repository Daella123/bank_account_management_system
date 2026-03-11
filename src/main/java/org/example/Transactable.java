package org.example;

public interface Transactable {
    boolean processTransaction(double amount, String type);
}
