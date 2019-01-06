package de.thm.ap.groupexpenses.model;

import java.util.ArrayList;
import java.util.List;

public class DoubtTable {

    /* Building  a variable table :

    | id | Position | Creditor | Debtor | amount | settled? |
    |----|----------|----------|--------|--------|----------|
    | 0  | Kuchen   | Tom      | Tina   | 30.f   | false    |

     */

    List<Integer> id;
    List<Position> position;
    List<String> creditor;
    List<String> debtor;
    List<Float> amount;
    List<Boolean> isSettled;

    int nextId;

    DoubtTable() {
        nextId = 0;
        position = new ArrayList<>();
        creditor = new ArrayList<>();
        debtor = new ArrayList<>();
        amount = new ArrayList<>();
        isSettled = new ArrayList<>();
    }

    public void put(Position p, String creditorId, String debtorId, Float amount, boolean isSettled){
        this.id.add(nextId); this.nextId++;
        this.position.add(p);
        this.creditor.add(creditorId);
        this.debtor.add(debtorId);
        this.amount.add(amount);
        this.isSettled.add(isSettled);
    }


}
