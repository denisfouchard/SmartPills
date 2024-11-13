package com.example.smartpills;

public interface ItemClickListener {
    void onClick(String phoneNumber);

    void onClick(int position, char delete);

    void onClick(int position, boolean modify);
}
