package com.example.t1g.firebaseproject;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.commons.lang.RandomStringUtils;

public class ProfileInput extends RelativeLayout {

    ProfileListActivity activity;

    EditText nameInput;
    RadioGroup genderInput;
    EditText ageInput;
    EditText hobbiesInput;
    EditText imageInput;
    TextView errorText;
    Button addButton;
    Button cancelButton;

    Profile outputProfile;

    public ProfileInput(Context context) {
        super(context);
    }

    public ProfileInput(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProfileInput(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initializeViews();
    }

    void initializeViews(){
        nameInput = findViewById(R.id.name_input);
        genderInput = findViewById(R.id.gender_input);
        ageInput = findViewById(R.id.age_input);
        hobbiesInput = findViewById(R.id.hobbies_input);
        imageInput = findViewById(R.id.image_input);
        errorText = findViewById(R.id.error_text);
        addButton = findViewById(R.id.add_button);
        cancelButton = findViewById(R.id.cancel_button);

        addButton.setOnClickListener(addButtonListener());
        cancelButton.setOnClickListener(cancelButtonListener());
        outputProfile = null;
    }

    void setActivity(ProfileListActivity activity){
        this.activity = activity;
    }

    /**
     * When Adding a profile, checks if all the data has been entered first
     */
    Button.OnClickListener addButtonListener(){
        return new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!inputIsValid()){
                    errorText.setText(R.string.invalid_input);
                }
                else{
                    Profile profile = buildProfileFromFields();
                    activity.addNewProfileToDatabase(profile);
                    clearFields();
                    setVisibility(INVISIBLE);
                }
            }
        };
    }

    Button.OnClickListener cancelButtonListener(){
        return new OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFields();
                setVisibility(INVISIBLE);
            }
        };
    }

    /**
     * @return whether or not the input for the new profile is valid
     */
    boolean inputIsValid(){
        Boolean nameIsValid = !nameInput.getText().toString().equals("");
        Boolean genderIsValid = genderInput.getCheckedRadioButtonId() != -1;
        Boolean ageIsValid = !ageInput.getText().toString().equals("");
        Boolean hobbiesIsValid = !hobbiesInput.getText().toString().equals("");
        Boolean imageIsValid = !imageInput.getText().toString().equals("");

        return nameIsValid && genderIsValid && ageIsValid && hobbiesIsValid && imageIsValid;
    }

    /**
     * @return a new Profile constructed from the input fields
     */
    Profile buildProfileFromFields(){

        String name = nameInput.getText().toString();
        String gender = genderInput.getCheckedRadioButtonId() == R.id.male_input ? "Male" : "Female";
        String age = ageInput.getText().toString();
        String hobbies = hobbiesInput.getText().toString();
        String image = imageInput.getText().toString();

        String id = RandomStringUtils.randomNumeric(8);

        return new Profile(id, name, gender, age, hobbies, image);
    }

    void clearFields() {
        nameInput.setText("");
        genderInput.clearCheck();
        ageInput.setText("");
        hobbiesInput.setText("");
        imageInput.setText("");
        errorText.setText("");
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindowToken(), 0);
        }
    }
}
