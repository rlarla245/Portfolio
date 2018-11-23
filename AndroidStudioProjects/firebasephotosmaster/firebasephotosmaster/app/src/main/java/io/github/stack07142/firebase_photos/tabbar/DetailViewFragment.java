package io.github.stack07142.firebase_photos.tabbar;

import android.app.Fragment;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import io.github.stack07142.firebase_photos.MainActivity;
import io.github.stack07142.firebase_photos.R;
import io.github.stack07142.firebase_photos.databinding.ItemDetailviewBinding;
import io.github.stack07142.firebase_photos.model.AlarmDTO;
import io.github.stack07142.firebase_photos.model.ContentDTO;

import static io.github.stack07142.firebase_photos.util.StatusCode.FRAGMENT_ARG;

public class DetailViewFragment extends Fragment {

    private FirebaseUser user;

    public DetailViewFragment() {

        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detailview, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.detailviewfragment_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new DetailRecyclerViewAdapter());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        ((MainActivity) getActivity()).getBinding().progressBar.setVisibility(View.GONE);
    }

    private class DetailRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private ArrayList<ContentDTO> contentDTOs;
        private ArrayList<String> contentUidList;

        DetailRecyclerViewAdapter() {

            contentDTOs = new ArrayList<>();
            contentUidList = new ArrayList<>();

            FirebaseDatabase.getInstance().getReference().child("images").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    contentDTOs.clear();
                    contentUidList.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        contentDTOs.add(snapshot.getValue(ContentDTO.class));
                        contentUidList.add(snapshot.getKey());
                    }

                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detailview, parent, false);

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

            final int finalPosition = position;
            final ItemDetailviewBinding binding = ((CustomViewHolder) holder).getBinding();

            // Profile Image
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("profileImages").child(contentDTOs.get(position).uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()) {

                                @SuppressWarnings("VisibleForTests")
                                String url = dataSnapshot.getValue().toString();

                                Glide.with(holder.itemView.getContext())
                                        .load(url)
                                        .apply(new RequestOptions().circleCrop()).into(binding.detailviewitemProfileImage);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

            binding.detailviewitemProfileImage.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Fragment fragment = new UserFragment();

                    Bundle bundle = new Bundle();
                    bundle.putString("destinationUid", contentDTOs.get(finalPosition).uid);
                    bundle.putString("userId", contentDTOs.get(finalPosition).userId);
                    bundle.putInt(FRAGMENT_ARG, 5);

                    fragment.setArguments(bundle);
                    getActivity().getFragmentManager().beginTransaction()
                            .replace(R.id.main_content, fragment)
                            .commit();
                }
            });

            // 유저 아이디
            binding.detailviewitemProfileTextview.setText(contentDTOs.get(position).userId);

            // 가운데 이미지
            Glide.with(holder.itemView.getContext())
                    .load(contentDTOs.get(position).imageUrl)
                    .into(binding.detailviewitemImageviewContent);

            // 설명 텍스트
            binding.detailviewitemExplainTextview.setText(contentDTOs.get(position).explain);

            binding.detailviewitemFavoriteImageview.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    favoriteEvent(finalPosition);
                }
            });

            // 내가 눌렀을 경우
            if (contentDTOs.get(position)
                    .favorites.containsKey(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                binding.detailviewitemFavoriteImageview.setImageResource(R.drawable.ic_favorite);
            } else {

                binding.detailviewitemFavoriteImageview.setImageResource(R.drawable.ic_favorite_border);
            }

            binding.detailviewitemFavoritecounterTextview.setText("좋아요 " + contentDTOs.get(position).favoriteCount + "개");

            binding.detailviewitemCommentImageview.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(v.getContext(), CommentActivity.class);
                    intent.putExtra("imageUid", contentUidList.get(finalPosition));
                    intent.putExtra("destinationUid", contentDTOs.get(finalPosition).uid);
                    Log.d("DetailViewFragment", contentUidList.get(finalPosition) == null ? "NULL" : contentUidList.get(finalPosition));
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {

            return contentDTOs.size();
        }

        private void favoriteEvent(int position) {

            final int finalPosition = position;

            FirebaseDatabase.getInstance().getReference("images").child(contentUidList.get(position))
                    .runTransaction(new Transaction.Handler() {

                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {

                            ContentDTO contentDTO = mutableData.getValue(ContentDTO.class);

                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            if (contentDTO == null) {
                                return Transaction.success(mutableData);
                            }
                            if (contentDTO.favorites.containsKey(uid)) {

                                // Unstar the post and remove self from stars
                                contentDTO.favoriteCount = contentDTO.favoriteCount - 1;
                                contentDTO.favorites.remove(uid);
                            } else {

                                // Star the post and add self to stars
                                contentDTO.favoriteCount = contentDTO.favoriteCount + 1;
                                contentDTO.favorites.put(uid, true);
                                favoriteAlarm(contentDTOs.get(finalPosition).uid);
                            }

                            // Set value and report transaction success
                            mutableData.setValue(contentDTO);

                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                        }
                    });
        }

        // setValue 메소드를 통해 데이터를 전송합니다.
        public void favoriteAlarm(String destinationUid) {

            AlarmDTO alarmDTO = new AlarmDTO();

            alarmDTO.destinationUid = destinationUid;
            alarmDTO.userId = user.getEmail();
            alarmDTO.uid = user.getUid();
            alarmDTO.kind = 0; // TODO : TypeDef
            FirebaseDatabase.getInstance().getReference().child("alarms").push().setValue(alarmDTO);
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {

            private ItemDetailviewBinding binding;

            CustomViewHolder(View itemView) {
                super(itemView);

                binding = DataBindingUtil.bind(itemView);
            }

            ItemDetailviewBinding getBinding() {

                return binding;
            }
        }
    }
}
