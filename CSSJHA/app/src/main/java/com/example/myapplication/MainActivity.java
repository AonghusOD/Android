package com.example.myapplication;

import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import com.github.gcacace.signaturepad.views.SignaturePad;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.graphics.drawable.BitmapDrawable;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import android.content.Intent;
import android.net.Uri;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import android.graphics.drawable.Drawable;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private SignaturePad signaturePad;
    private Button clearButton;
    private Button addSignatureButton;
    private Button submitButton;
    private LinearLayout signatureContainer;
    private LinearLayout signatureLayout;
    private File signatureImagesDir;
    private int signatureImageIndex = 0;
    private CheckBox[] taskCheckboxes;
    private LinearLayout taskCheckboxContainer; // Container layout for the task checkboxes
    private CheckBox[] hazardCheckboxes;
    private Handler handler;
    private Runnable runnable;
    private Spinner scopeSpinner1;
    private TextView textViewData;
    private TextView printNameData;
    private TextView other_text;
    private EditText edittext_other;
    private EditText edittext_otherScope;
    private EditText edittext_otherHazard;
    private TextView dateTextView;
    String inputData;
    String dateEmail;
    private List<Bitmap> signatureBitmaps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize signature images directory
        signatureImagesDir = new File(getCacheDir(), "signature_images");
        if (!signatureImagesDir.exists()) {
            signatureImagesDir.mkdirs();
        } else {
            // Delete existing signature images
            deleteSignatureImages();
        }
        // Initialize the list to store signature bitmaps
        signatureBitmaps = new ArrayList<>();
        // Initialize views
        signaturePad = findViewById(R.id.signature_pad);
        clearButton = findViewById(R.id.clear_button);
        submitButton = findViewById(R.id.submit_button);
        addSignatureButton = findViewById(R.id.add_signature_button);
        signatureLayout = findViewById(R.id.signature_layout);
        //signatureContainer = findViewById(R.id.signature_container);
        clearButton.setEnabled(false);
        addSignatureButton.setEnabled(false);
        submitButton.setEnabled(true);
        other_text = findViewById(R.id.other_text); //
        edittext_other = findViewById(R.id.edittext_other); //
        edittext_otherScope = findViewById(R.id.edittext_otherScope);
        edittext_otherHazard = findViewById(R.id.edittext_otherHazard);


        dateTextView = findViewById(R.id.text_date);

        // Get current date
        Date currentDate = new Date();

        // Define date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");

        // Format the date
        String formattedDate = dateFormat.format(currentDate);
        dateEmail = formattedDate;
        // Set the formatted date to the TextView
        dateTextView.setText(formattedDate);

        // Add Signature button click listener
        addSignatureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current signature bitmap
                Bitmap signatureBitmap = signaturePad.getSignatureBitmap();

                // Add the signature bitmap to the list
                signatureBitmaps.add(signatureBitmap);

                // Clear the signature pad
                signaturePad.clear();

                // Show a toast indicating the signature was added
                Toast.makeText(MainActivity.this, "Signature added", Toast.LENGTH_SHORT).show();
            }
        });

        CheckBox checkbox_other = (CheckBox) findViewById(R.id.checkbox_task12);

        CheckBox checkbox_hazard = (CheckBox) findViewById(R.id.checkbox_hazard8);

        checkbox_other.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Checkbox is checked, perform your action here
                    other_text.setVisibility(View.VISIBLE);
                    edittext_other.setVisibility(View.VISIBLE);
                } else {
                    other_text.setVisibility(View.GONE);
                    edittext_other.setVisibility(View.GONE);
                }
            }
        });

        checkbox_hazard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Checkbox is checked, perform your action here
                    edittext_otherHazard.setVisibility(View.VISIBLE);
                } else {
                    edittext_otherHazard.setVisibility(View.GONE);
                }
            }
        });
        // Initialize the checkbox array
        hazardCheckboxes = new CheckBox[]{
                findViewById(R.id.checkbox_hazard1),
                findViewById(R.id.checkbox_hazard2),
                findViewById(R.id.checkbox_hazard3),
                findViewById(R.id.checkbox_hazard4),
                findViewById(R.id.checkbox_hazard5),
                findViewById(R.id.checkbox_hazard6),
                findViewById(R.id.checkbox_hazard7),
                findViewById(R.id.checkbox_hazard8)
        };

        textViewData = findViewById(R.id.edittext_job_name);
        printNameData = findViewById(R.id.edittext_print_name);

        // Initialize the checkbox array
        taskCheckboxes = new CheckBox[]{
                findViewById(R.id.checkbox_task1),
                findViewById(R.id.checkbox_task2),
                findViewById(R.id.checkbox_task3),
                findViewById(R.id.checkbox_task4),
                findViewById(R.id.checkbox_task5),
                findViewById(R.id.checkbox_task6),
                findViewById(R.id.checkbox_task7),
                findViewById(R.id.checkbox_task8),
                findViewById(R.id.checkbox_task9),
                findViewById(R.id.checkbox_task10),
                findViewById(R.id.checkbox_task11),
                findViewById(R.id.checkbox_task12)
        };

        taskCheckboxContainer = findViewById(R.id.checkbox_container); // Reference to the LinearLayout container


        // Setup the scope dropdown lists
        scopeSpinner1 = findViewById(R.id.spinner_scope);

        setupScopeSpinner(scopeSpinner1, R.array.scope_options);

        // Set the initial selection for all the spinners
        scopeSpinner1.setSelection(0);
//        // Clear button click listener
//        clearButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                signaturePad.clear();
//            }
//        });

        // Submit button click listener
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Retrieve the selected values from the spinners
                String scope1 = scopeSpinner1.getSelectedItem().toString();
                boolean isCheckboxHazard8Selected = hazardCheckboxes[7].isChecked();

                if (checkbox_other.isChecked()) {
                    other_text.setVisibility(View.VISIBLE);
                    edittext_other.setVisibility(View.VISIBLE);
                } else {
                    other_text.setVisibility(View.GONE);
                    edittext_other.setVisibility(View.GONE);
                }


                if (isCheckboxHazard8Selected) {
                    other_text.setVisibility(View.VISIBLE);
                    edittext_other.setVisibility(View.VISIBLE);
                } else {
                    other_text.setVisibility(View.GONE);
                    edittext_other.setVisibility(View.GONE);
                }

                // Retrieve the checkbox states for task
                boolean[] checkboxStates = new boolean[taskCheckboxes.length];
                boolean isAnyCheckboxChecked = false;
                for (int i = 0; i < taskCheckboxes.length; i++) {
                    checkboxStates[i] = taskCheckboxes[i].isChecked();
                    if (checkboxStates[i]) {
                        isAnyCheckboxChecked = true;
                    }
                }
                // Retrieve the checkbox states for hazards
                boolean[] checkboxStates2 = new boolean[hazardCheckboxes.length];
                boolean isAnyCheckboxChecked2 = false;
                for (int i = 0; i < hazardCheckboxes.length; i++) {
                    checkboxStates2[i] = hazardCheckboxes[i].isChecked();
                    if (checkboxStates2[i]) {
                        isAnyCheckboxChecked2 = true;
                    }
                }


                // Build the data string
                StringBuilder dataBuilder = new StringBuilder();

                // Retrieve the input value from the user
                String jobInput = textViewData.getText().toString();
                String printNameInput = printNameData.getText().toString();
                String otherHazard = edittext_otherHazard.getText().toString();
                String otherScope = edittext_otherScope.getText().toString();

                // Perform validation checks
                if (jobInput.isEmpty()) {
                    textViewData.setError("Job field is required");
                    return;
                }

                if (printNameInput.isEmpty()) {
                    printNameData.setError("Foreman name field is required");
                    return;
                }

                if (scope1.equals("Select Scope")) {
                    Toast.makeText(MainActivity.this, "Please select a scope", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isAnyCheckboxChecked) {
                    Toast.makeText(MainActivity.this, "Please select at least one task.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isAnyCheckboxChecked2) {
                    Toast.makeText(MainActivity.this, "Please select at least one hazard.", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (signaturePad.isEmpty() && signatureBitmaps.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please provide a signature.", Toast.LENGTH_SHORT).show();
                    clearButton.setEnabled(false); // Disable the clear button
                    return;
                } else {
                    clearButton.setEnabled(true); // Enable the clear button
                }

                dataBuilder.append("Date: ").append(formattedDate).append("\n");
                dataBuilder.append("Job: ").append(jobInput).append("\n");
                // Append the user input to the data string
                dataBuilder.append("Foreman Name: ").append(printNameInput).append("\n");


                //Add scope to data string
                dataBuilder.append("Scope: ").append(scope1).append("\n");
                if (!otherScope.isEmpty()) {
                    //toast.makeText(MainActivity.this, "otherScope", Toast.LENGTH_LONG).show();
                    dataBuilder.append("OtherScope: ").append(otherScope).append("\n");
                }

                //Add tasks to data string
                if (isAnyCheckboxChecked) {
                    //dataBuilder.append("Checked Tasks:\n");
                    for (int i = 0; i < checkboxStates.length; i++) {
                        if (checkboxStates[i]) {
                            dataBuilder.append("Tasks: ").append(taskCheckboxes[i].getText()).append(", ").append("\n");
                        }
                    }
                }


                //Add hazards to data string
                if (isAnyCheckboxChecked2) {
                    //dataBuilder.append("Checked Tasks:\n");
                    for (int x = 0; x < checkboxStates2.length; x++) {
                        if (checkboxStates2[x]) {
                            dataBuilder.append("Hazards: ").append(hazardCheckboxes[x].getText()).append(", ").append("\n");

                        }
                    }
                }
                if (!otherHazard.isEmpty()) {
                    //Toast.makeText(MainActivity.this, "otherHazard", Toast.LENGTH_LONG).show();
                    dataBuilder.append("OtherHazard: ").append(otherHazard).append("\n");
                }
                inputData = dataBuilder.toString();



//////////////////////////////////////////////////////////////////////////////////////////
                // Get the signature images as a list of Bitmaps
                //List<Bitmap> signatureBitmaps = getSignatureBitmaps();


//                // Save the signature bitmap to a file
//                String path = MediaStore.Images.Media.insertImage(
//                        getContentResolver(),
//                        signatureBitmaps,
//                        "Signature",
//                        null
//                );


               // Uri signatureUri = Uri.parse(path);

                // Create an intent to share the signature images
                Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"JHA@CorsettiSteel.com"});
                startActivity(Intent.createChooser(intent, "Share Signatures"));

                String emailTitle = jobInput + " - " + dateEmail;
                // Set the email subject
                intent.putExtra(Intent.EXTRA_SUBJECT, emailTitle);

                // Set the email body
                String emailBody = inputData;
                intent.putExtra(Intent.EXTRA_TEXT, emailBody);

                // Create an ArrayList to store signature URIs
                ArrayList<Uri> signatureUris = new ArrayList<>();

                // Convert each signature bitmap to a file and get its URI
                for (Bitmap signatureBitmap : signatureBitmaps) {
                    try {
                        File signatureFile = createImageFile();
                        OutputStream outputStream = new FileOutputStream(signatureFile);
                        signatureBitmap.compress(Bitmap.CompressFormat.PNG, 20, outputStream);
                        outputStream.close();

                        if (signatureFile.exists()) {
                            Uri signatureUri = FileProvider.getUriForFile(MainActivity.this, "com.example.myapplication2.fileprovider", signatureFile);
                            signatureUris.add(signatureUri);
                        } else {
                            // Handle the case when the file does not exist
                            // Add your error handling code here or show a message to the user
                            Toast.makeText(MainActivity.this, "Failed to attach signature image", Toast.LENGTH_SHORT).show();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // Add the signature URIs to the intent
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, signatureUris);

                // Grant read permission to the receiving app
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                // Start the email client activity
                startActivity(Intent.createChooser(intent, "Send email..."));

                // Initialize the handler
                handler = new Handler();

                // Create a new runnable to quit the program
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        // Forcefully quit the application
                        System.exit(0);
                    }
                };

                // Start the timer for 5 seconds
                handler.postDelayed(runnable, 1500);
            }
        });

        // Add a listener to the signature pad to detect when the user starts drawing
        signaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                // Enable the clear button
                clearButton.setEnabled(true);
                addSignatureButton.setEnabled(true); // Enable the clear button
            }

            @Override
            public void onSigned() {
                // No action needed
            }

            @Override
            public void onClear() {
                // Disable the clear button
                clearButton.setEnabled(false);
                addSignatureButton.setEnabled(false); // Enable the clear button
            }
        });

// Clear button click listener
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signaturePad.clear();
            }
        });

    }


    // Helper method to delete existing signature images
    private void deleteSignatureImages() {
        File[] files = signatureImagesDir.listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
    }

    // Helper method to convert the signature images to a list of Bitmaps
    private List<Bitmap> getSignatureBitmaps() {
        List<Bitmap> signatureBitmaps = new ArrayList<>();
        LinearLayout signatureLayout = findViewById(R.id.signature_layout);

        for (int i = signatureLayout.getChildCount() - 1; i >= 0; i--) {
            View view = signatureLayout.getChildAt(i);
            if (view instanceof ImageView) {
                ImageView imageView = (ImageView) view;
                Drawable drawable = imageView.getDrawable();
                if (drawable instanceof BitmapDrawable) {
                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                    if (bitmap != null) {
                        signatureBitmaps.add(bitmap);
                    }
                }
            }
        }

        return signatureBitmaps;
    }

    private File createImageFile() throws IOException {
        String imageFileName = "signature_" + signatureImageIndex++;
        File imageFile = new File(signatureImagesDir, imageFileName + ".png");
        return imageFile;
    }

    private void clearCheckboxes() {
        // Clear task checkboxes
        for (CheckBox checkBox : taskCheckboxes) {
            checkBox.setChecked(false);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedScope = parent.getItemAtPosition(position).toString();
        // Reset checkboxes to GONE for all options
        hideTaskCheckboxes();
        clearCheckboxes();
        other_text.setVisibility(View.GONE);
        edittext_other.setVisibility(View.GONE);
        edittext_otherScope.setVisibility(View.GONE);
        edittext_otherHazard.setVisibility(View.GONE);

        if (selectedScope.equals("Erect Steel")) {
            taskCheckboxContainer.setVisibility(View.VISIBLE);
            taskCheckboxes[0].setVisibility(View.VISIBLE);
            taskCheckboxes[1].setVisibility(View.VISIBLE);
            taskCheckboxes[2].setVisibility(View.VISIBLE);
            taskCheckboxes[3].setVisibility(View.VISIBLE);
            taskCheckboxes[4].setVisibility(View.VISIBLE);
            taskCheckboxes[5].setVisibility(View.VISIBLE);
            taskCheckboxes[6].setVisibility(View.VISIBLE);
            taskCheckboxes[11].setVisibility(View.VISIBLE);
        } else if (selectedScope.equals("Floor Deck")) {
            taskCheckboxContainer.setVisibility(View.VISIBLE);
            taskCheckboxes[1].setVisibility(View.VISIBLE);
            taskCheckboxes[6].setVisibility(View.VISIBLE);
            taskCheckboxes[3].setVisibility(View.VISIBLE);
            taskCheckboxes[7].setVisibility(View.VISIBLE);
            taskCheckboxes[9].setVisibility(View.VISIBLE);
            taskCheckboxes[4].setVisibility(View.VISIBLE);
            taskCheckboxes[11].setVisibility(View.VISIBLE);
        } else if (selectedScope.equals("Roof Deck")) {
            taskCheckboxContainer.setVisibility(View.VISIBLE);
            taskCheckboxes[1].setVisibility(View.VISIBLE);
            taskCheckboxes[6].setVisibility(View.VISIBLE);
            taskCheckboxes[3].setVisibility(View.VISIBLE);
            taskCheckboxes[7].setVisibility(View.VISIBLE);
            taskCheckboxes[11].setVisibility(View.VISIBLE);
        } else if (selectedScope.equals("Siding")) {
            taskCheckboxContainer.setVisibility(View.VISIBLE);
            taskCheckboxes[8].setVisibility(View.VISIBLE);
            taskCheckboxes[4].setVisibility(View.VISIBLE);
            taskCheckboxes[7].setVisibility(View.VISIBLE);
            taskCheckboxes[11].setVisibility(View.VISIBLE);
        } else if (selectedScope.equals("Stairs")) {
            taskCheckboxContainer.setVisibility(View.VISIBLE);
            taskCheckboxes[8].setVisibility(View.VISIBLE);
            taskCheckboxes[3].setVisibility(View.VISIBLE);
            taskCheckboxes[4].setVisibility(View.VISIBLE);
            taskCheckboxes[5].setVisibility(View.VISIBLE);
            taskCheckboxes[11].setVisibility(View.VISIBLE);
        } else if (selectedScope.equals("Railing")) {
            taskCheckboxContainer.setVisibility(View.VISIBLE);
            taskCheckboxes[8].setVisibility(View.VISIBLE);
            taskCheckboxes[10].setVisibility(View.VISIBLE);
            taskCheckboxes[3].setVisibility(View.VISIBLE);
            taskCheckboxes[4].setVisibility(View.VISIBLE);
            taskCheckboxes[5].setVisibility(View.VISIBLE);
            taskCheckboxes[11].setVisibility(View.VISIBLE);
        } else if (selectedScope.equals("Detailing")) {
            taskCheckboxContainer.setVisibility(View.VISIBLE);
            taskCheckboxes[2].setVisibility(View.VISIBLE);
            taskCheckboxes[3].setVisibility(View.VISIBLE);
            taskCheckboxes[4].setVisibility(View.VISIBLE);
            taskCheckboxes[5].setVisibility(View.VISIBLE);
            taskCheckboxes[11].setVisibility(View.VISIBLE);
        } else if (selectedScope.equals("Architectural")) {
            taskCheckboxContainer.setVisibility(View.VISIBLE);
            taskCheckboxes[8].setVisibility(View.VISIBLE);
            taskCheckboxes[3].setVisibility(View.VISIBLE);
            taskCheckboxes[4].setVisibility(View.VISIBLE);
            taskCheckboxes[5].setVisibility(View.VISIBLE);
            taskCheckboxes[11].setVisibility(View.VISIBLE);
        } else if (selectedScope.equals("Other")) {
            edittext_otherScope.setVisibility(View.VISIBLE);
        } else {
            hideTaskCheckboxes();
//        }


            adjustCheckboxContainer();
            // Check if "Scope 1" is selected
//        if (selectedScope.equals("Erect Steel")) {
//            // Show the task checkboxes
//            showTaskCheckboxes();
//        } else {
//            // Hide the task checkboxes
//            hideTaskCheckboxes();
//        }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove any pending callbacks to prevent leaks
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing
    }

    private void showTaskCheckboxes() {
        for (CheckBox checkBox : taskCheckboxes) {
            checkBox.setVisibility(View.VISIBLE);
        }
    }

    private void hideTaskCheckboxes() {
        for (CheckBox checkBox : taskCheckboxes) {
            checkBox.setVisibility(View.GONE);
        }
    }

    private void adjustCheckboxContainer() {
        boolean hasVisibleCheckboxes = false;

        // Check if any task checkboxes are visible
        for (CheckBox checkBox : taskCheckboxes) {
            if (checkBox.getVisibility() == View.VISIBLE) {
                hasVisibleCheckboxes = true;
                break;
            }
        }

        // Adjust the layout of the checkbox container
        if (hasVisibleCheckboxes) {
            taskCheckboxContainer.setVisibility(View.VISIBLE);
        } else {
            taskCheckboxContainer.setVisibility(View.GONE);
        }
    }


    private void setupScopeSpinner(Spinner spinner, int arrayResourceId) {
        ArrayAdapter<CharSequence> scopeAdapter = ArrayAdapter.createFromResource(
                this, arrayResourceId, android.R.layout.simple_spinner_item);
        scopeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(scopeAdapter);
        spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
    }


}