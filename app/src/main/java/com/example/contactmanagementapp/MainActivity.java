package com.example.contactmanagementapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;
import android.widget.ToggleButton;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;

public class MainActivity extends AppCompatActivity {

    private TextInputLayout inputLayoutName, inputLayoutPhone, inputLayoutEmail;
    private TextInputEditText editTextName, editTextPhone, editTextEmail;
    private ToggleButton toggleEditMode;
    private MaterialButton btnSave;
    private SharedPreferences sharedPreferences;

    private static final String PREFS_NAME = "ContactPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputLayoutName = findViewById(R.id.inputLayoutName);
        inputLayoutPhone = findViewById(R.id.inputLayoutPhone);
        inputLayoutEmail = findViewById(R.id.inputLayoutEmail);
        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextEmail = findViewById(R.id.editTextEmail);
        toggleEditMode = findViewById(R.id.toggleEditMode);
        btnSave = findViewById(R.id.btnSave);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        loadSavedContact();
        setEditMode(false);

        toggleEditMode.setOnCheckedChangeListener((buttonView, isChecked) -> setEditMode(isChecked));

        addValidationListeners();

        btnSave.setOnClickListener(v -> saveContact());
    }

    private void loadSavedContact() {
        editTextName.setText(sharedPreferences.getString("name", ""));
        editTextPhone.setText(sharedPreferences.getString("phone", ""));
        editTextEmail.setText(sharedPreferences.getString("email", ""));
    }

    private void setEditMode(boolean enabled) {
        editTextName.setEnabled(enabled);
        editTextPhone.setEnabled(enabled);
        editTextEmail.setEnabled(enabled);
        btnSave.setEnabled(enabled);
    }

    private void addValidationListeners() {
        editTextName.addTextChangedListener(new ValidationWatcher(editTextName));
        editTextPhone.addTextChangedListener(new ValidationWatcher(editTextPhone));
        editTextEmail.addTextChangedListener(new ValidationWatcher(editTextEmail));
    }

    private class ValidationWatcher implements TextWatcher {
        private final TextInputEditText editText;

        ValidationWatcher(TextInputEditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override
        public void afterTextChanged(Editable s) {
            validateField(editText);
        }
    }

    private boolean validateField(TextInputEditText field) {
        if (field == editTextName) {
            String name = field.getText().toString().trim();
            if (!name.matches("^[A-Za-z ]+$")) {
                inputLayoutName.setError("Name can only contain letters");
                return false;
            } else {
                inputLayoutName.setError(null);
                return true;
            }
        } else if (field == editTextPhone) {
            String phone = field.getText().toString().trim();
            if (!phone.matches("^[0-9]{10}$")) {
                inputLayoutPhone.setError("Enter a valid 10-digit phone number");
                return false;
            } else {
                inputLayoutPhone.setError(null);
                return true;
            }
        } else if (field == editTextEmail) {
            String email = field.getText().toString().trim();
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                inputLayoutEmail.setError("Invalid email address");
                return false;
            } else {
                inputLayoutEmail.setError(null);
                return true;
            }
        }
        return true;
    }

    private void saveContact() {
        boolean validName = validateField(editTextName);
        boolean validPhone = validateField(editTextPhone);
        boolean validEmail = validateField(editTextEmail);

        if (validName && validPhone && validEmail) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("name", editTextName.getText().toString());
            editor.putString("phone", editTextPhone.getText().toString());
            editor.putString("email", editTextEmail.getText().toString());
            editor.apply();
            Toast.makeText(this, "Contact saved successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please fix errors", Toast.LENGTH_SHORT).show();
        }
    }
}
