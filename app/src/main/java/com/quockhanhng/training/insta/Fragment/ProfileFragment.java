package com.quockhanhng.training.insta.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quockhanhng.training.insta.Activity.EditProfileActivity;
import com.quockhanhng.training.insta.Adapter.PhotoAdapter;
import com.quockhanhng.training.insta.Model.Post;
import com.quockhanhng.training.insta.Model.User;
import com.quockhanhng.training.insta.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfileFragment extends Fragment {

    private ImageView ivImageProfile, ivOptions;
    private TextView tvPosts, tvFollowers, tvFollowing, tvFullName, tvBio, tvUsername;
    private Button btnEditProfile;

    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;
    private List<Post> postList;

    private List<String> mySaved;
    private RecyclerView recyclerView_Saved;
    private PhotoAdapter photoAdapter_Saved;
    private List<Post> postList_Saved;

    private FirebaseUser firebaseUser;
    private String profileId;

    private ImageButton ibMyPhotos, ibSavedPhotos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileId = prefs.getString("profileId", "none");

        ivImageProfile = view.findViewById(R.id.image_profile);
        ivOptions = view.findViewById(R.id.options);
        tvPosts = view.findViewById(R.id.posts);
        tvFollowers = view.findViewById(R.id.followers);
        tvFollowing = view.findViewById(R.id.following);
        tvFullName = view.findViewById(R.id.full_name);
        tvBio = view.findViewById(R.id.bio);
        tvUsername = view.findViewById(R.id.username);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        ibMyPhotos = view.findViewById(R.id.my_photos);
        ibSavedPhotos = view.findViewById(R.id.saved_photos);

        // User post
        recyclerView = view.findViewById(R.id.recycle_view_photos);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        postList = new ArrayList<>();
        photoAdapter = new PhotoAdapter(getContext(), postList);
        recyclerView.setAdapter(photoAdapter);

        // User saved
        recyclerView_Saved = view.findViewById(R.id.recycle_view_saved_photos);
        recyclerView_Saved.setHasFixedSize(true);
        recyclerView_Saved.setLayoutManager(new GridLayoutManager(getContext(), 3));
        postList_Saved = new ArrayList<>();
        photoAdapter_Saved = new PhotoAdapter(getContext(), postList_Saved);
        recyclerView_Saved.setAdapter(photoAdapter_Saved);

        // Set visible list post
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView_Saved.setVisibility(View.GONE);

        userInfo();
        getFollower();
        getPostsCount();
        myPhotos();
        mySaved();

        if (profileId.equals(firebaseUser.getUid())) {
            btnEditProfile.setText("Edit profile");
        } else {
            checkFollow();
            ibSavedPhotos.setVisibility(View.GONE);
        }

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btnString = btnEditProfile.getText().toString();
                // Check if this is the owner profile
                if (btnString.equalsIgnoreCase("edit profile")) {
                    editProfile();
                } else if (btnString.equalsIgnoreCase("follow")) {
                    followUser();
                } else if (btnString.equalsIgnoreCase("following")) {
                    unFollowUser();
                }
            }
        });

        ibMyPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView_Saved.setVisibility(View.GONE);
            }
        });

        ibSavedPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setVisibility(View.GONE);
                recyclerView_Saved.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }

    private void userInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(profileId);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (getContext() == null) {
                    return;
                }
                User user = dataSnapshot.getValue(User.class);
                Glide.with(getContext()).load(user.getImageUrl()).into(ivImageProfile);
                tvUsername.setText(user.getUsername());
                tvFullName.setText(user.getFullName());
                tvBio.setText(user.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkFollow() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("Following");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(profileId).exists()) {
                    btnEditProfile.setText(R.string.following);
                } else {
                    btnEditProfile.setText(R.string.follow);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getFollower() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("Followers");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tvFollowers.setText(dataSnapshot.getChildrenCount() + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("Following");
        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tvFollowing.setText(dataSnapshot.getChildrenCount() + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getPostsCount() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileId))
                        count++;
                }

                tvPosts.setText(count + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void editProfile() {
        startActivity(new Intent(getContext(), EditProfileActivity.class));
    }

    private void followUser() {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                .child("Following").child(profileId).setValue(true);
        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                .child("Followers").child(firebaseUser.getUid()).setValue(true);
    }

    private void unFollowUser() {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                .child("Following").child(profileId).removeValue();
        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                .child("Followers").child(firebaseUser.getUid()).removeValue();
    }

    private void myPhotos() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileId))
                        postList.add(post);
                }
                Collections.reverse(postList);
                photoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void mySaved() {
        mySaved = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    mySaved.add(snapshot.getKey());
                }
                readKeys();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readKeys() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList_Saved.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);

                    for (String id : mySaved)
                        if (post.getPostId().equals(id))
                            postList_Saved.add(post);

                }
                photoAdapter_Saved.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
