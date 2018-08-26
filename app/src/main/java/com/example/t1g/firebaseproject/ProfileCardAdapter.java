package com.example.t1g.firebaseproject;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

//An adapter for the Profile Recycler View
public class ProfileCardAdapter extends RecyclerView.Adapter<ProfileCardAdapter.ProfileCardViewHolder> {

    Context context;
    private List<Profile> profileList;

    ProfileCardAdapter(Context context, List<Profile> profileList) {
        this.context = context;
        this.profileList = profileList;
    }

    @NonNull
    @Override
    public ProfileCardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ProfileCardViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.profile_card, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileCardViewHolder viewHolder, int i) {
        final Profile profile = profileList.get(i);

        viewHolder.nameView.setText(context.getResources().getString(R.string.name_with_blank, profile.getName()));
        viewHolder.genderView.setText(context.getResources().getString(R.string.gender_with_blank, profile.getGender()));
        viewHolder.ageView.setText(context.getResources().getString(R.string.age_with_blank, profile.getAge()));
        viewHolder.hobbiesView.setText(context.getResources().getString(R.string.hobbies_with_blank, profile.getHobbies()));
        viewHolder.idView.setText(context.getResources().getString(R.string.id_with_blank, profile.getId()));

        Picasso.get().load(profile.getImage()).into(viewHolder.imageView);

        int colorId = context.getResources().getColor(R.color.genderUndefined);

        if(profile.getGender().toLowerCase().equals("female")){
            colorId = context.getResources().getColor(R.color.genderFemale);
        } else if(profile.getGender().toLowerCase().equals("male")){
            colorId = context.getResources().getColor(R.color.genderMale);
        }
        //Set the background color based on gender
        viewHolder.itemView.setBackgroundColor(colorId);

        //Opens the ProfileView when tapping on the card
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openProfileViewIntent = new Intent(context, ProfileViewActivity.class);
                openProfileViewIntent.putExtra("id", profile.getId());
                openProfileViewIntent.putExtra("name", profile.getName());
                openProfileViewIntent.putExtra("gender", profile.getGender());
                openProfileViewIntent.putExtra("age", profile.getAge());
                openProfileViewIntent.putExtra("hobbies", profile.getHobbies());
                openProfileViewIntent.putExtra("image", profile.getImage());
                context.startActivity(openProfileViewIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return profileList.size();
    }


    class ProfileCardViewHolder extends RecyclerView.ViewHolder {

        TextView nameView;
        TextView genderView;
        TextView ageView;
        TextView hobbiesView;
        ImageView imageView;
        TextView idView;

        ProfileCardViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.name);
            genderView = itemView.findViewById(R.id.gender);
            ageView = itemView.findViewById(R.id.age);
            hobbiesView = itemView.findViewById(R.id.hobbies);
            imageView = itemView.findViewById(R.id.image);
            idView = itemView.findViewById(R.id.profile_id);
        }


    }
}