package com.example.petshelter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    TextView nameView;
    Button adoptBtn;
    Button nextBtn;
    ImageView imageView;
    TextView messageView;
    TextView countView;
    ImageView logoView;
    ArrayList<String> names = new ArrayList<>();
    int total = 0;
    String savedFile = "countsAdopts.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        messageView = findViewById(R.id.messageView);
        nameView = findViewById(R.id.nameView);
        adoptBtn = findViewById(R.id.adoptBtn);
        nextBtn = findViewById(R.id.nextBtn);
        imageView = findViewById(R.id.imageView);
        countView = findViewById(R.id.countView);
        logoView = findViewById(R.id.logoView);

        // Set Logo image
        try {
            logoView.setImageDrawable(Drawable.createFromStream(getAssets().open("images/logo.png"), null));
        } catch (IOException e) {
            ; // Don't do anything. No one needs to know there was an error.
        }

        // Set Button colors
        adoptBtn.setBackgroundColor(Color.rgb(10, 200, 100));
        nextBtn.setBackgroundColor(Color.rgb(100, 100, 100));

        // Set Button methods
        adoptBtn.setOnClickListener(v -> adopt());
        nextBtn.setOnClickListener(v -> next());

        // Read dog names from the file and put them into ArrayList
        try {
            InputStream is = getAssets().open("names.txt");
            Scanner scanner = new Scanner(is);

            // Read the file line by line, adding each to the list
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                names.add(line);
            }
        } catch (Exception e) {
            messageView.setText("Oops! There's a problem with the names!");
        }

        // Read total adoptions from file
        total = getSavedData();

        // starting message
        if (total > 0) {
            messageView.setText("Welcome back!");
        } else {
            messageView.setText("Welcome! Let's get started!");
        }

        countView.setText("You've adopted " + total + " pets!");


        // Get started by showing the first name and corresponding image
        String name = names.get(0);
        nameView.setText(name);

        // Show the image that matches the name.
        // Put in a try/catch in case the file doesn't exist
        try {
            imageView.setImageDrawable(Drawable.createFromStream(getAssets().open("images/" + name + ".jpg"), null));
        } catch (IOException e) {
            imageView.setImageDrawable(null);
        }
    }

    private void adopt() {
        adoptBtn.setEnabled(false);
        messageView.setText("Thank you for adopting " + names.get(0) + "!");
        names.remove(0); // remove the first element because it's been adopted

        total++;
        countView.setText("You've adopted " + total + " pets so far!");
        saveData(total);
    }

    private void next() {
        if (names.size() == 0) {
            messageView.setText("All pets have been adopted!");
            imageView.setImageDrawable(null);
            nameView.setText("");
            adoptBtn.setEnabled(false);
            nextBtn.setEnabled(false);
        } else {
            // Move the first element to the end of the list
            names.add(names.remove(0));
            adoptBtn.setEnabled(true);

            // Show the new current name
            String name = names.get(0);
            nameView.setText(name);

            // Show the image that goes with the name.
            // Put in a try/catch in case the file doesn't exist
            try {
                InputStream inputstream = getAssets().open("images/" + name + ".jpg");
                imageView.setImageDrawable(Drawable.createFromStream(inputstream, null));
            } catch (IOException e) {
                imageView.setImageDrawable(null);
            }
        }
    }

    private void saveData(int data) {
        String fileContents = data + "";
        try (FileOutputStream fos = getApplicationContext().openFileOutput(savedFile, Context.MODE_PRIVATE)) {
            fos.write(fileContents.getBytes());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int getSavedData() {
        int savedData = 0;

        try {
            FileInputStream fis = getApplicationContext().openFileInput(savedFile);
            Scanner scanner = new Scanner(fis);

            // Read the file
            while ((scanner.hasNextLine())) {
                savedData = Integer.parseInt(scanner.nextLine()); // Get the number from the file
            }
        } catch (Exception e) {
            ; // Don't do anything. No one needs to know there was an error.
        }

        return savedData;
    }
}