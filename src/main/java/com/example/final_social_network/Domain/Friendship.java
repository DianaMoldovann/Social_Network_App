package com.example.final_social_network.Domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;


public class Friendship extends Entity<Tuple<Long,Long>> {

    LocalDate date;

    public Friendship(Tuple<Long, Long> id) {
        super(id);
        Supplier<LocalDate> supplierDate = LocalDate::now;
        this.date = supplierDate.get();
    }

    public Friendship(Tuple<Long, Long> id, LocalDate date) {
        super(id);
        this.date = date;
    }

    /**
     *
     * @return the date when the friendship was created
     */
    public LocalDate getDate() {
        return date;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return "Friendship{" +
                "user1 = " + id.getLeft() +  ", " +
                "user2 = " + id.getRight() +  ", " +
                "date = " + date.format(formatter) +
                '}';
    }
}

