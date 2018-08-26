package com.example.t1g.firebaseproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class ProfileViewActivity extends AppCompatActivity {

    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    TextView nameView;
    TextView genderView;
    TextView ageView;
    EditText hobbiesInputView;
    ImageView imageView;

    Button deleteProfile;
    Button confirmDelete;
    Button cancelDelete;

    Profile profile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_view);

        Intent intent = getIntent();

        profile = new Profile(intent.getStringExtra("id"), intent.getStringExtra("name"),
                intent.getStringExtra("gender"), intent.getStringExtra("age"),
                intent.getStringExtra("hobbies"), intent.getStringExtra("image"));

        nameView = findViewById(R.id.name);
        genderView = findViewById(R.id.gender);
        ageView = findViewById(R.id.age);
        hobbiesInputView = findViewById(R.id.hobbies_text_field);
        imageView = findViewById(R.id.image);
        deleteProfile = findViewById(R.id.delete_button);
        confirmDelete = findViewById(R.id.confirm_delete);
        cancelDelete = findViewById(R.id.cancel_delete);

        setProfileViewData(profile);

        hobbiesInputView.setOnEditorActionListener(hobbiesEditorActionListener());
        deleteProfile.setOnClickListener(deleteProfileListener());
        confirmDelete.setOnClickListener(confirmDeleteListener());
        cancelDelete.setOnClickListener(cancelDeleteListener());

        //This listener is needed in case changes are made to the profile while we are viewing it
        database.getReference("profiles").child(profile.getId()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            //If the hobbies are editted while we are viewing the page they get changed
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getKey().toLowerCase().equals("hobbies")) {
                    hobbiesInputView.setText(dataSnapshot.getValue(String.class));
                }
            }

            //If the Profile we are viewing gets deleted, return to the main page
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Intent deleteProfileIntent = new Intent(getApplicationContext(), ProfileListActivity.class);
                startActivity(deleteProfileIntent);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * This is needed to make sure that the keyboard disappears and the Hobbies EditText
     * loses focus after we click Done, and if a change was made to Hobbies, the database
     * gets updated accordingly
     */
    TextView.OnEditorActionListener hobbiesEditorActionListener(){
        return new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i== EditorInfo.IME_ACTION_DONE){
                    //Clear focus here from edittext
                    hobbiesInputView.clearFocus();

                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(hobbiesInputView.getWindowToken(), 0);
                    }

                    if(!hobbiesInputView.getText().toString().equals(profile.getHobbies())){
                        database.getReference("profiles").child(profile.getId()).child("hobbies").setValue(hobbiesInputView.getText().toString());
                    }
                }
                return false;
            }
        };
    }

    View.OnClickListener deleteProfileListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.delete_confirmation).setVisibility(View.VISIBLE);
            }
        };
    }
    View.OnClickListener cancelDeleteListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.delete_confirmation).setVisibility(View.INVISIBLE);
            }
        };
    }

    //After deleting the profile, return to the ProfileListActivity
    View.OnClickListener confirmDeleteListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database.getReference("profiles").child(profile.getId()).removeValue();
                Intent deleteProfileIntent = new Intent(getApplicationContext(), ProfileListActivity.class);
                startActivity(deleteProfileIntent);
            }
        };
    }

    void setProfileViewData(Profile profile){

        //Use the Profile data to create the view
        nameView.setText(getResources().getString(R.string.name_with_blank, profile.getName()));
        genderView.setText(getResources().getString(R.string.gender_with_blank, profile.getGender()));
        ageView.setText(getResources().getString(R.string.age_with_blank, profile.getAge()));
        hobbiesInputView.setText(profile.getHobbies());

        hobbiesInputView.setRawInputType(InputType.TYPE_CLASS_TEXT);

        Picasso.get().load(profile.getImage()).into(imageView);

        int colorId = getResources().getColor(R.color.genderUndefined);

        if(profile.getGender().toLowerCase().equals("female")){
            colorId = getResources().getColor(R.color.genderFemale);
        } else if(profile.getGender().toLowerCase().equals("male")){
            colorId = getResources().getColor(R.color.genderMale);
        }

        //Sets background color based on gender
        findViewById(R.id.profile_view_container).setBackgroundColor(colorId);
    }
}
