package com.example.t1g.firebaseproject;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * The Main Activity of the class, consisting of a filter toolbar and a RecyclerView containing Cards
 * created from data gathered from Firebase
 */
public class ProfileListActivity extends AppCompatActivity {

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference ref = database.getReference("profiles");

    CheckBox maleFilter;
    CheckBox femaleFilter;
    RadioGroup nameOrAgeSort;
    RadioGroup ascendOrDescend;
    Button resetFilter;
    RecyclerView profileRecyclerView;
    LinearLayoutManager linearLayoutManager;
    ProfileCardAdapter profileAdapter;
    List<Profile> profileList;
    FloatingActionButton newProfileFab;
    ProfileInput profileInput;

    boolean descending = false;
    boolean showMale = true;
    boolean showFemale = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        maleFilter = findViewById(R.id.male_filter);
        femaleFilter = findViewById(R.id.female_filter);
        nameOrAgeSort = findViewById(R.id.name_or_age);
        ascendOrDescend = findViewById(R.id.ascend_or_descend);
        resetFilter = findViewById(R.id.reset_filter);
        newProfileFab = findViewById(R.id.new_profile_fab);
        profileInput = findViewById(R.id.new_profile_overlay);
        profileInput.setActivity(this);

        setListeners();

        profileRecyclerView = findViewById(R.id.profiles_list);

        linearLayoutManager = new LinearLayoutManager(this);
        profileRecyclerView.setLayoutManager(linearLayoutManager);

        profileList = getAdapterList(ref.orderByChild("id"));
        profileAdapter = new ProfileCardAdapter(this, profileList);
        profileRecyclerView.setAdapter(profileAdapter);

    }

    /**
     * Sets various listeners for the UI elements
     */
    private void setListeners(){

        maleFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                showMale = b;
                refreshList();
            }
        });
        femaleFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                showFemale = b;
                refreshList();
            }
        });

        //resets the filters and refreshes the list
        resetFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ascendOrDescend.check(R.id.ascending);
                nameOrAgeSort.clearCheck();
                maleFilter.setChecked(true);
                femaleFilter.setChecked(true);
                refreshList();
            }
        });

        //The FAB is used to open the Add Profile view, and can also be used to close it
        newProfileFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(profileInput.getVisibility() == View.VISIBLE) {
                    profileInput.setVisibility(View.INVISIBLE);
                    profileInput.clearFields();
                }
                else{
                    profileInput.setVisibility(View.VISIBLE);
                }
            }
        });

        ascendOrDescend.setOnCheckedChangeListener(ascendOrDescendListener());

        nameOrAgeSort.setOnCheckedChangeListener(nameOrAgeSortListener());

    }

    //Uses the given query to create a new list for the adapter
    private List<Profile> getAdapterList(Query query) {
        final ArrayList<Profile> newCardList = new ArrayList<>();

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Profile profile = dataSnapshot.getValue(Profile.class);
                if (profile != null) {
                    Log.d("OUTPUT", "ID: " + profile.getId());

                    if(shouldShowBasedOnGender(profile)) {
                        if (descending) {
                            newCardList.add(0, profile);
                        } else {
                            newCardList.add(profile);
                        }
                    }
                }
                profileAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                int x = profileRecyclerView.getScrollX();
                int y = profileRecyclerView.getScrollY();
                refreshList();
                profileRecyclerView.scrollTo(x, y);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                refreshList();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                refreshList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return newCardList;

    }

    //Does not refresh the list when we switch between ascending or descending, just reverses the list instead
    private RadioGroup.OnCheckedChangeListener ascendOrDescendListener(){
        return new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                descending = (i == R.id.descending);
                Collections.reverse(profileList);
                profileAdapter = new ProfileCardAdapter(profileAdapter.context, profileList);
                profileRecyclerView.setAdapter(profileAdapter);
            }
        };
    }

    //We want to refresh the list if we change what we are sorting by
    private RadioGroup.OnCheckedChangeListener nameOrAgeSortListener(){
        return new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                refreshList();
            }
        };
    }

    /**
     * Given a profile, checks the filters to see if it should be shown based on gender
     * @param profile the profile we are checking
     * @return whether to show the profile or not
     */
    boolean shouldShowBasedOnGender(Profile profile){
        if (profile.getGender().toLowerCase().equals("male")){
            return showMale;
        }
        else if (profile.getGender().toLowerCase().equals("female")){
            return showFemale;
        }
        return false;
    }

    /**
     * Creates a new list and adapter for the RecyclerView
     */
    private void refreshList(){
        if (nameOrAgeSort.getCheckedRadioButtonId() == R.id.sort_by_name){
            profileList = getAdapterList(ref.orderByChild("name"));
        } else if (nameOrAgeSort.getCheckedRadioButtonId() == R.id.sort_by_age) {
            profileList = getAdapterList(ref.orderByChild("age"));
        } else{
            profileList = getAdapterList(ref.orderByChild("id"));
        }
        profileAdapter = new ProfileCardAdapter(profileAdapter.context, profileList);
        profileRecyclerView.setAdapter(profileAdapter);
    }

    /**
     * Adds a new profile to the Database
     * @param profile the Profile containing the data to be added
     */
    void addNewProfileToDatabase(Profile profile){
        ref.child(profile.getId()).setValue(profile);
    }
}
